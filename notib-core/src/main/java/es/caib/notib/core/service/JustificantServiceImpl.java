package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.JustificantException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.JustificantService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei per a generar justificants
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class JustificantServiceImpl implements JustificantService {
    @Autowired
    private JustificantEnviamentHelper justificantEnviamentHelper;
    @Autowired
    private JustificantRecepcioSIRHelper justificantRecepcioSIRHelper;
    @Autowired
    private NotificacioRepository notificacioRepository;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Resource
    private MetricsHelper metricsHelper;
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PluginHelper pluginHelper;

    public static Map<String, ProgresDescarregaDto> progresDescarrega = new HashMap<String, ProgresDescarregaDto>();

    @Transactional
    @Override
    public FitxerDto generarJustificantEnviament(Long notificacioId,
                                                 String sequence) throws JustificantException {
        Timer.Context timer = metricsHelper.iniciMetrica();
        try {

            NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
            List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());

            if (enviamentsPendents != null && !enviamentsPendents.isEmpty())
                throw new ValidationException("No es pot generar el justificant d'una notificació amb enviaments pendents.");

            if (isABackgroundProcessNotEnded(sequence)) {
                log.error("Ja existeix un altre procés iniciat");
                getProgress(sequence).addInfo(ProgresDescarregaDto.TipusInfo.ERROR, messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant"));
                return null;
            } else {
                return generarJustificantEnviament(notificacio, sequence);
            }
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    @Transactional
    @Override
    public FitxerDto generarJustificantEnviament(Long notificacioId,
            Long entitatId,
            String sequence) throws JustificantException {
        Timer.Context timer = metricsHelper.iniciMetrica();
        try {

            NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
            List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());

            if (enviamentsPendents != null && !enviamentsPendents.isEmpty() && !NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat()))
                throw new ValidationException("No es pot generar el justificant d'una notificació amb enviaments pendents.");

            entityComprovarHelper.comprovarEntitat(
                    entitatId,
                    false,
                    true,
                    true,
                    false);
            if (isABackgroundProcessRunning(sequence)) {
                log.error("Ja existeix un altre procés iniciat");
                getProgress(sequence).addInfo(ProgresDescarregaDto.TipusInfo.ERROR, messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant"));
                return null;
            } else {
                return generarJustificantEnviament(notificacio, sequence);
            }
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    @Transactional
    @Override
    public FitxerDto generarJustificantComunicacioSIR(Long enviamentId,
                                                      Long entitatId,
                                                      String sequence) throws JustificantException {
        Timer.Context timer = metricsHelper.iniciMetrica();
        try {
            NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
            if (enviament == null) {
                throw new ValidationException("L'enviament del que s'intenta generar el justificant no existeix");
            }
            if (!enviament.isRegistreEstatFinal()){
                throw new ValidationException("No es pot generar un justificant de un enviament que no està en un estat final");
            }
            entityComprovarHelper.comprovarEntitat(
                    entitatId,
                    false,
                    true,
                    true,
                    false);
            if (isABackgroundProcessRunning(sequence)) {
                log.error("Ja existeix un altre procés iniciat");
                getProgress(sequence).addInfo(ProgresDescarregaDto.TipusInfo.ERROR, messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant"));
                return null;
            } else {
                return generarJustificantComunicacioSIR(enviament, sequence);
            }
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    @Override
    public ProgresDescarregaDto consultaProgresGeneracioJustificant(String sequence) {
        Timer.Context timer = metricsHelper.iniciMetrica();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ProgresDescarregaDto progres = progresDescarrega.get(auth.getName() + "_" + sequence);
            if (progres != null && progres.getProgres() != null &&  progres.getProgres() >= 100) {
                progresDescarrega.remove(auth.getName());
            }
            return progres;
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    private ProgresDescarregaDto getProgress(String sequence) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ProgresDescarregaDto progres;
        if (isABackgroundProcessRunning(sequence)){
            progres = progresDescarrega.get(auth.getName() + "_" + sequence);

        } else {
            progres = new ProgresDescarregaDto();
            progresDescarrega.put(auth.getName() + "_" + sequence, progres);
        }
        return progres;
    }

    private boolean isABackgroundProcessRunning(String sequence){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ProgresDescarregaDto progres = progresDescarrega.get(auth.getName() + "_" + sequence);
        return progres != null && progres.getProgres() != 0;
    }
    private boolean isABackgroundProcessNotEnded(String sequence){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ProgresDescarregaDto progres = progresDescarrega.get(auth.getName() + "_" + sequence);
        return progres != null && progres.getProgres() < 100;
    }
    private FitxerDto generarJustificantEnviament(
            NotificacioEntity notificacio,
            String sequence) throws JustificantException {

        //## Únic procés per usuari per evitar sobrecàrrega
        ProgresDescarregaDto progres = getProgress(sequence);

        //## GENERAR JUSTIFICANT
        log.debug("Recuperant el justificant de la notificacio (notificacioId=" + notificacio.getId() + ")");
        progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant"));
        byte[] contingut = justificantEnviamentHelper.generarJustificant(
                conversioTipusHelper.convertir(
                        notificacio,
                        NotificacioDtoV2.class),
                progres);
        FitxerDto justificantOriginal = new FitxerDto();
        justificantOriginal.setNom("justificant_notificació_" + notificacio.getId() + ".pdf");
        justificantOriginal.setContentType("application/pdf");
        justificantOriginal.setContingut(contingut);

        //## FIRMA EN SERVIDOR
        progres.setProgres(80);
        progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.aplicant.firma"));
        byte[] contingutFirmat = null;
        try {
            contingutFirmat = pluginHelper.firmaServidorFirmar(
                    notificacio,
                    justificantOriginal,
                    TipusFirma.PADES,
                    "justificant enviament Notib",
                    "ca");
            progres.setProgres(100);
        } catch (Exception ex) {
            progres.setProgres(100);
            String errorDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.aplicant.firma.error");
            progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorDescripcio);
            log.error(errorDescripcio, ex);
            progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.finalitzat"));
            notificacio.setJustificantCreat(true);
            return justificantOriginal;
        }
        progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.finalitzat.firma"));
        FitxerDto justificantFirmat = new FitxerDto();
        justificantFirmat.setContentType("application/pdf");
        justificantFirmat.setContingut(contingutFirmat);
        justificantFirmat.setNom("justificant_notificació_" + notificacio.getId() + "_firmat.pdf");
        justificantFirmat.setTamany(contingutFirmat.length);
        notificacio.setJustificantCreat(true);
        return justificantFirmat;
    }
    private FitxerDto generarJustificantComunicacioSIR(
            NotificacioEnviamentEntity enviament,
            String sequence) throws JustificantException {

        //## Únic procés per usuari per evitar sobrecàrrega
        ProgresDescarregaDto progres = getProgress(sequence);

        //## GENERAR JUSTIFICANT
        log.debug("Recuperant el justificant de la notificacio (enviamentId=" + enviament.getId() + ")");
        progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant"));
        byte[] contingut = justificantRecepcioSIRHelper.generarJustificant( enviament, progres);
        FitxerDto justificantOriginal = new FitxerDto();
        justificantOriginal.setNom("justificant_comunicacio_sir_" + enviament.getId() + ".pdf");
        justificantOriginal.setContentType("application/pdf");
        justificantOriginal.setContingut(contingut);

        //## FIRMA EN SERVIDOR
        progres.setProgres(80);
        progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.aplicant.firma"));
        byte[] contingutFirmat = null;
        try {
            contingutFirmat = pluginHelper.firmaServidorFirmar(
                    enviament.getNotificacio(),
                    justificantOriginal,
                    TipusFirma.PADES,
                    "justificant enviament Notib",
                    "ca");
            progres.setProgres(100);
        } catch (Exception ex) {
            progres.setProgres(100);
            String errorDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.aplicant.firma.error");
            progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorDescripcio);
            log.error(errorDescripcio, ex);
            progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.finalitzat"));
            return justificantOriginal;
        }
        progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.finalitzat.firma"));
        FitxerDto justificantFirmat = new FitxerDto();
        justificantFirmat.setContentType("application/pdf");
        justificantFirmat.setContingut(contingutFirmat);
        justificantFirmat.setNom("justificant_comunicacio_sir_" + enviament.getId() + "_firmat.pdf");
        justificantFirmat.setTamany(contingutFirmat.length);
        return justificantFirmat;
    }
}
