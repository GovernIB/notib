package es.caib.notib.logic.plugin.cie;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.SubsistemesHelper;
import es.caib.notib.logic.helper.plugin.AbstractPluginHelper;
import es.caib.notib.logic.helper.plugin.ArxiuPluginHelper;
import es.caib.notib.logic.helper.plugin.GestioDocumentalPluginHelper;
import es.caib.notib.logic.helper.plugin.RegistrePluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.RegistreAnnexDto;
import es.caib.notib.logic.intf.dto.RegistreModeFirmaDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreOrigenDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentalDtoEnum;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.EncryptionUtil;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaPostalRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.plugin.cie.CiePlugin;
import es.caib.notib.plugin.cie.EnviamentCie;
import es.caib.notib.plugin.cie.InfoCie;
import es.caib.notib.plugin.cie.RespostaCie;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.DocumentContingut;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static es.caib.notib.logic.helper.SubsistemesHelper.SubsistemesEnum.CIE;

@Slf4j
@Component
public class CiePluginHelper extends AbstractPluginHelper<CiePlugin> {

    private final NotificacioTableViewRepository notificacioTableViewRepository;
    private final MessageHelper messageHelper;
    private final NotificacioTableHelper notificacioTableHelper;
    private final ConversioTipusHelper conversioTipusHelper;
    private final NotificacioEventHelper notificacioEventHelper;
    private final NotificacioRepository notificacioRepository;
    private final NotificacioEventRepository notificacioEventRepository;
    private final GestioDocumentalPluginHelper gestioDocumentalPluginHelper;
    private final RegistrePluginHelper registrePluginHelper;
    private final ArxiuPluginHelper arxiuPluginHelper;
    private final EntregaPostalRepository entregaPostalRepository;
    private final NotificacioEnviamentRepository enviamentRepository;
    private final OrganGestorRepository organGestorRepository;

    public static final String GRUP = "CIE";
    private static final String ERROR_INESPERAT = "Error inesperat";
    private final CallbackHelper callbackHelper;

    public CiePluginHelper(ConfigHelper configHelper,
                           EntitatRepository entitatRepository,
                           IntegracioHelper integracioHelper,
                           ConversioTipusHelper conversioTipusHelper,
                           NotificacioEventHelper notificacioEventHelper,
                           NotificacioRepository notificacioRepository,
                           NotificacioEventRepository notificacioEventRepository,
                           GestioDocumentalPluginHelper gestioDocumentalPluginHelper,
                           RegistrePluginHelper registrePluginHelper,
                           ArxiuPluginHelper arxiuPluginHelper,
                           EntregaPostalRepository entregaPostalRepository,
                           NotificacioEnviamentRepository enviamentRepository,
                           NotificacioTableViewRepository notificacioTableViewRepository,
                           MessageHelper messageHelper,
                           NotificacioTableHelper notificacioTableHelper,
                           MeterRegistry meterRegistry, CallbackHelper callbackHelper,
                           OrganGestorRepository organGestorRepository) {

        super(integracioHelper, configHelper, entitatRepository, meterRegistry);

        this.conversioTipusHelper = conversioTipusHelper;
        this.notificacioEventHelper = notificacioEventHelper;
        this.notificacioRepository = notificacioRepository;
        this.notificacioEventRepository = notificacioEventRepository;
        this.gestioDocumentalPluginHelper = gestioDocumentalPluginHelper;
        this.registrePluginHelper = registrePluginHelper;
        this.arxiuPluginHelper = arxiuPluginHelper;
        this.entregaPostalRepository = entregaPostalRepository;
        this.enviamentRepository = enviamentRepository;
        this.notificacioTableViewRepository = notificacioTableViewRepository;
        this.messageHelper = messageHelper;
        this.notificacioTableHelper = notificacioTableHelper;
        this.callbackHelper = callbackHelper;
        this.organGestorRepository = organGestorRepository;
    }

    @Transactional
    public RespostaCie enviar(String notificacioReferencia) {

        long start = System.currentTimeMillis();
        boolean errorSbs = false;
        var resposta = new RespostaCie();
        try {
            var notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
            var codiDir3Entitat = notificacio.getEntitat().getDir3Codi();
            var info = new IntegracioInfo(IntegracioCodi.CIE, "Enviar entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                    new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                    new AccioParam("Notificacio", notificacio.getId() + ""));
            info.setNotificacioId(notificacio.getId());
            info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
            NotibLogger.getInstance().info("Inici enviar entrega postal", log, LoggingTipus.ENTREGA_CIE);
            try {
                EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
                if (entitat == null) {
                    throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat + "no trobada");
                }
                configHelper.setEntitatCodi(entitat.getCodi());
                info.setCodiEntitat(entitat.getCodi());
                byte[] contingut = null;
                if (notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
                    var baos = new ByteArrayOutputStream();
                    ConfigHelper.setEntitatCodi(entitat.getCodi());
                    gestioDocumentalPluginHelper.gestioDocumentalGet(notificacio.getDocument().getArxiuGestdocId(), PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, baos);
                    contingut = baos.size() > 0 ? baos.toByteArray() : null;
                } else {
                    contingut = documentToRegistreAnnexDto(notificacio.getDocument()).getArxiuContingut();
                }
                if (contingut == null) {
                    throw new Exception("El document no te contingut " + notificacio.getDocument().getId());
                }
                var enviamentCie = conversioTipusHelper.convertir(notificacio, EnviamentCie.class);
                enviamentCie.setEnviaments(enviamentCie.getEnviaments().stream().filter(e -> e.getEntregaPostal() != null).collect(Collectors.toList()));
                enviamentCie.setContingutDocument(contingut);
                enviamentCie.setCodiDir3Entitat(configHelper.getConfigAsBoolean("es.caib.notib.plugin.codi.dir3.entitat"));
                var entregaCieEfectiva = notificacio.getProcediment().getEntregaCieEfectiva();
                entregaCieEfectiva = entregaCieEfectiva == null ? notificacio.getOrganGestor().getEntregaCie() : entregaCieEfectiva;
                if (entregaCieEfectiva == null) {
                    entregaCieEfectiva = getEntregaCiePare(notificacio.getEntitat(), notificacio.getOrganGestor());
                    if (entregaCieEfectiva == null) {
                        throw new Exception("[CiePluginHelper.enviar] No existeix una entrega cie activa per cap dels organs ni els seus pares");
                    }
                }
                var pagadorCieEntity = entregaCieEfectiva.getCie();
                var apiKey = getApiKey(pagadorCieEntity);
                var cie = conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
                if (notificacio.getOrganGestor().isSobrescriureCieOrganEmisor()) {
                    cie.setOrganismeEmisorCodi(notificacio.getOrganGestor().getCodi());
                }
                cie.setApiKey(apiKey);
                enviamentCie.setEntregaCie(cie);
                enviamentCie.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEfectiva.getOperadorPostal(), OperadorPostalDto.class));
                NotibLogger.getInstance().info("Enviant entrega postal " + enviamentCie, log, LoggingTipus.ENTREGA_CIE);
                resposta = getPlugin().enviar(enviamentCie);
                if ("000".equals(resposta.getCodiResposta())) {
                    integracioHelper.addAccioOk(info);
                } else {
                    integracioHelper.addAccioError(info, resposta.getDescripcioError());
                }
            } catch (Exception ex) {
                var errorDescripcio = "Error al accedir al plugin CIE";
                integracioHelper.addAccioError(info, errorDescripcio, ex);
                if (ex.getCause() != null) {
                    errorDescripcio += " :" + ex.getCause().getMessage();
                }
                resposta.setCodiResposta(ERROR_INESPERAT);
                resposta.setDescripcioError(errorDescripcio);
                errorSbs = true;
//            throw new SistemaExternException(IntegracioCodiEnum.CIE.name(), errorDescripcio, ex);
            }
            guardarRespostaCie(notificacioReferencia, resposta);
            notificacioTableHelper.actualitzarRegistre(notificacio);
        } catch (Exception ex) {
            SubsistemesHelper.addErrorOperation(CIE);
            throw ex;
        }
        if (errorSbs) {
            SubsistemesHelper.addErrorOperation(CIE);
        } else {
            SubsistemesHelper.addSuccessOperation(CIE, System.currentTimeMillis() - start);
        }
        return resposta;
    }

    private void guardarRespostaCie(String notificacioReferencia, RespostaCie resposta) {

        try {
            var notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
            var enviaments = notificacio.getEnviaments();
            Map<String, NotificacioEnviamentEntity> envs = new HashMap<>();
            for (var env : enviaments) {
                envs.put(env.getTitular().getNif(), env);
            }
            NotificacioEnviamentEntity env;
            EntregaPostalEntity entregaPostal;
            if (resposta.getIdentificadors() == null) {
                afegirEventsEnviarCie(notificacioReferencia, resposta);
                return;
            }
            for (var id : resposta.getIdentificadors()) {
                if (Strings.isNullOrEmpty(id.getNifTitular())) {
                    log.error("[ENTREGA_POSTAL] Error al guardar l'enviament CIE. Resposta amb id " + id.getIdentificador() + " sense NIF");
                    continue;
                }
                if (Strings.isNullOrEmpty(id.getIdentificador())) {
                    log.error("[ENTREGA_POSTAL] Error al guardar l'enviament CIE. Resposta amb NIF " + id.getNifTitular() + " no te identificador");
                    continue;
                }
                env = envs.get(id.getNifTitular());
                entregaPostal = env.getEntregaPostal();
                if (entregaPostal == null) {
                    log.error("[ENTREGA_POSTAL] Error al guardar l'enviament CIE. El NIF " + id.getNifTitular() + " no pertany a cap dels enviamentsç ");
                    continue;
                }
                entregaPostal.setCieId(id.getIdentificador());
                entregaPostal.setCieEstat(CieEstat.ENVIADO_CI);
                callbackHelper.crearCallback(notificacio, env, false, "");
                entregaPostalRepository.save(entregaPostal);
            }
            afegirEventsEnviarCie(notificacioReferencia, resposta);
        } catch (Exception ex) {
            log.error("Error guardant l'enviament CIE a la BDD", ex);
        }
    }

    private void afegirEventsEnviarCie(String notificacioReferencia, RespostaCie resposta) {

        var notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
        var events = notificacioEventRepository.findEventsCieByNotificacioId(notificacio.getId());
        var maxReintents = configHelper.getConfigAsInteger("es.caib.notib.plugin.cie.max.reintents");
        var fiReintents = events != null && !events.isEmpty() && events.get(0).getIntents() >= maxReintents;
        for (var env : notificacio.getEnviaments()) {
            if (resposta == null) {
                notificacioEventHelper.addCieEventEnviar(env, true, ERROR_INESPERAT, fiReintents);
                continue;
            }
            var error = !"000".equals(resposta.getCodiResposta());
            notificacioEventHelper.addCieEventEnviar(env, error, error ? resposta.getDescripcioError() : "", fiReintents);
            if (error) {
                var entregaPostal = env.getEntregaPostal();
                entregaPostal.setCieEstat(CieEstat.ERROR);
                var errorDesc = !Strings.isNullOrEmpty(resposta.getDescripcioError()) ? resposta.getDescripcioError() : null;
                errorDesc = errorDesc.length() > 250 ? resposta.getDescripcioError().substring(0, 250) : errorDesc;
                entregaPostal.setCieErrorDesc(errorDesc);
                entregaPostalRepository.save(entregaPostal);
            }
        }
    }

    private RegistreAnnexDto documentToRegistreAnnexDto (DocumentEntity document) {

        var annex = new RegistreAnnexDto();
        annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
        annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
        annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
        annex.setData(new Date());
        annex.setIdiomaCodi("ca");

        if((document.getUuid() != null || document.getCsv() != null) && document.getContingutBase64() == null) {
            var loadFromArxiu = registrePluginHelper.isReadDocsMetadataFromArxiu() && document.getUuid() != null || document.getCsv() == null;
            DocumentContingut doc;
            if(loadFromArxiu) {
                try {
                    annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);

                    doc = arxiuPluginHelper.arxiuGetImprimible(document.getUuid(), true);
                    annex.setArxiuContingut(doc.getContingut());
                    annex.setArxiuNom(doc.getArxiuNom());
                } catch (ArxiuException ae) {
                    log.error("Error Obtenint el document per l'uuid");
                }
                return annex;
            }
            try {
                annex.setModeFirma(RegistreModeFirmaDtoEnum.AUTOFIRMA_SI);
                doc = arxiuPluginHelper.arxiuGetImprimible(document.getCsv(), false);
                annex.setArxiuContingut(doc.getContingut());
                annex.setArxiuNom(doc.getArxiuNom());
            } catch (ArxiuException ae) {
                log.error("Error Obtenint el document per csv");
            }
            return annex;
        }
        if(document.getContingutBase64() != null && (document.getUuid() == null && document.getCsv() == null)) {
            annex.setArxiuContingut(document.getContingutBase64().getBytes());
            annex.setArxiuNom(document.getArxiuNom());
            annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
        }
        return annex;
    }

    @Transactional
    public boolean cancelar(String uuidEnviament) {

        var enviament = enviamentRepository.findByUuid(uuidEnviament).orElse(null);
        if (enviament == null) {
            log.error("[ENTREGA_POSTAL] Error al cancelar l'enviament CIE. UUID " + uuidEnviament + " no trobat");
            return false;
        }
        NotibLogger.getInstance().info("Cancelar entrega postal " + uuidEnviament, log, LoggingTipus.ENTREGA_CIE);
        var notificacio = enviament.getNotificacio();
        var codiDir3Entitat = notificacio.getEntitat().getDir3Codi();
        var info = new IntegracioInfo(IntegracioCodi.CIE, "Cancelar entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                new AccioParam("Enviament", enviament.getId() + ""));
        info.setNotificacioId(notificacio.getId());
        info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
        var resposta = new RespostaCie();
        try {
            EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
            if (entitat == null) {
                throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
            }
            configHelper.setEntitatCodi(entitat.getCodi());
            info.setCodiEntitat(entitat.getCodi());
            var cieEntity = notificacio.getProcediment().getEntregaCieEfectiva();
            var apiKey = getApiKey(cieEntity.getCie());
            var cie = conversioTipusHelper.convertir(cieEntity, CieDto.class);
            cie.setApiKey(apiKey);
            var enviamentCie = new EnviamentCie();
            enviamentCie.setIdentificador(enviament.getEntregaPostal().getCieId());
            enviamentCie.setEntregaCie(cie);
            resposta = getPlugin().cancelar(enviamentCie);
            if ("000".equals(resposta.getCodiResposta())) {
                integracioHelper.addAccioOk(info);
                enviament.getEntregaPostal().setCieCancelat(true);
                enviament.getEntregaPostal().setCieEstat(CieEstat.CANCELADO);
                cancelarUpdateEntregaPostal(uuidEnviament, true);
            } else {
                integracioHelper.addAccioError(info, resposta.getDescripcioError());
                cancelarUpdateEntregaPostal(uuidEnviament, false);
            }
            return true;
        } catch (Exception ex) {
            var errorDescripcio = "Error al accedir al plugin CIE";
            integracioHelper.addAccioError(info, errorDescripcio, ex);
            cancelarUpdateEntregaPostal(uuidEnviament, false);
            if (ex.getCause() != null) {
                errorDescripcio += " :" + ex.getCause().getMessage();
            }
            resposta.setDescripcioError(errorDescripcio);
            throw new SistemaExternException(IntegracioCodi.CIE.name(), errorDescripcio, ex);
        }
    }

    private void cancelarUpdateEntregaPostal(String uuidEnviament, boolean ok) {

        var enviament = enviamentRepository.findByUuid(uuidEnviament).orElse(null);
        if (enviament == null) {
            log.error("[ENTREGA_POSTAL]  Error al guardar el resultat de cancelar l'enviament CIE. UUID " + uuidEnviament + " no trobat");
            return;
        }
        enviament.getEntregaPostal().setCieCancelat(ok);
        var event = notificacioEventRepository.findEventCieByEnviamentId(enviament.getId());
        var maxReintents = configHelper.getConfigAsInteger("es.caib.notib.plugin.cie.max.reintents");
        var fiReintents = event != null && event.getIntents() > maxReintents;
        notificacioEventHelper.addCieEventCancelar(enviament, !ok, !ok ? "Error al cancelar l'enviament" : "", fiReintents);
    }

    @Transactional
    public InfoCie consultarEstatEntregaPostal(Long enviamentId) {

        NotibLogger.getInstance().info("[CiePluginHelper] Consulta d'estat per l'enviament " + enviamentId, log, LoggingTipus.ENTREGA_CIE);
        var enviament = enviamentRepository.findById(enviamentId).orElse(null);
        if (enviament == null) {
            log.error("[ENTREGA_POSTAL] Error al consultar l'estat de l'entrega postal l'enviament CIE. enviamentId " + enviamentId + " no trobat");
            return null;
        }
        NotibLogger.getInstance().info("[CiePluginHelper] Obtingut " + enviamentId, log, LoggingTipus.ENTREGA_CIE);
        var codiDir3Entitat = enviament.getNotificacio().getEntitat().getDir3Codi();
        var info = new IntegracioInfo(IntegracioCodi.CIE, "Consulta estat entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                new AccioParam("Enviament", enviament.getId() + ""));
        var notificacio = enviament.getNotificacio();
        info.setNotificacioId(notificacio.getId());
        info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
        var infoCie = new InfoCie();
        try {
            NotibLogger.getInstance().info("[CiePluginHelper] Consulta de l'entitat " + codiDir3Entitat, log, LoggingTipus.ENTREGA_CIE);
            var entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
            if (entitat == null) {
                throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
            }
            NotibLogger.getInstance().info("[CiePluginHelper] Obtinguda " + codiDir3Entitat, log, LoggingTipus.ENTREGA_CIE);
            configHelper.setEntitatCodi(entitat.getCodi());
            info.setCodiEntitat(entitat.getCodi());
//            var cieEntity = notificacio.getProcediment().getEntregaCieEfectiva();
            var procediment = notificacio.getProcediment();
            var cieEntity = procediment.getEntregaCieEfectiva() != null ? procediment.getEntregaCieEfectiva() : notificacio.getOrganGestor().getEntregaCie();
            if (cieEntity == null) {
                cieEntity = getEntregaCiePare(notificacio.getEntitat(), notificacio.getOrganGestor());
                if (cieEntity == null) {
                    throw new Exception("[CiePluginHelper.consultarEstatEntregaPostal] No existeix una entrega cie activa per cap dels organs ni els seus pares");
                }
            }
            var apiKey = getApiKey(cieEntity.getCie());
            var cie = conversioTipusHelper.convertir(cieEntity, CieDto.class);
            cie.setApiKey(apiKey);
            var enviamentCie = new EnviamentCie();
            enviamentCie.setIdentificador(enviament.getEntregaPostal().getCieId());
            enviamentCie.setEntregaCie(cie);
            NotibLogger.getInstance().info("[CiePluginHelper] Consulta d'estat per l'enviament " + enviamentId + " amb CIE id " + enviamentCie.getIdentificador(), log, LoggingTipus.ENTREGA_CIE);
            infoCie = getPlugin().consultarEstat(enviamentCie);
            if ("000".equals(infoCie.getCodiResposta())) {
                integracioHelper.addAccioOk(info);
                enviament.getEntregaPostal().setCieEstat(infoCie.getCodiEstat());
                if (CieEstat.ERROR.equals(infoCie.getCodiEstat()) && Strings.isNullOrEmpty(enviament.getEntregaPostal().getCieErrorDesc())) {
                    enviament.getEntregaPostal().setCieErrorDesc(messageHelper.getMessage("entrega.postal.error.pendent.adviser"));
                }
                notificacioEventHelper.addCieEventConsultaEstat(enviament, false, "", false);
            } else {
                integracioHelper.addAccioError(info, infoCie.getDescripcioResposta());
                notificacioEventHelper.addCieEventConsultaEstat(enviament, true, infoCie.getDescripcioResposta(), false);
            }
            return infoCie;
        } catch (Exception ex) {
            var errorDescripcio = "Error al accedir al plugin CIE";
            integracioHelper.addAccioError(info, errorDescripcio, ex);
            if (ex.getCause() != null) {
                errorDescripcio += " :" + ex.getCause().getMessage();
            }
            infoCie.setDescripcioResposta(errorDescripcio);
            notificacioEventHelper.addCieEventConsultaEstat(enviament, true, infoCie.getDescripcioResposta(), false);
            throw new SistemaExternException(IntegracioCodi.CIE.name(), errorDescripcio, ex);
        }
    }

    private EntregaCieEntity getEntregaCiePare(EntitatEntity entitat, OrganGestorEntity organ) {

        if (organ.getCodiPare() == null || "A99999999".equals(organ.getCodiPare())) {
            return organ.getEntregaCie();
        }
        var o = organGestorRepository.findByEntitatAndCodi(entitat, organ.getCodiPare());
        if (o.getEntregaCie() != null) {
            return o.getEntregaCie();
        }
        return getEntregaCiePare(entitat, o);
    }

    private String getApiKey(PagadorCieEntity pagadorCieEntity) {

        var encripter = new EncryptionUtil(configHelper.getConfig("es.caib.notib.plugin.cie.encriptor.key"), pagadorCieEntity.getSalt());
        return encripter.decrypt(pagadorCieEntity.getApiKey());
    }

    protected CiePlugin getPlugin() {

        var codiEntitat = getCodiEntitatActual();
        if (Strings.isNullOrEmpty(codiEntitat)) {
            throw new RuntimeException("El codi d'entitat no pot ser nul");
        }

        var plugin = pluginMap.get(codiEntitat);
        if (plugin != null) {
            return plugin;
        }
        var pluginClass = getPluginClassProperty();
        if (Strings.isNullOrEmpty(pluginClass)) {
            var msg = "\"La classe del plugin CIE no està definida\"";
            log.error(msg);
            throw new SistemaExternException(IntegracioCodi.CIE.name(), msg);
        }
        try {
            var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
            var propietats = configHelper.getAllEntityProperties(codiEntitat);
            Class<?> clazz = Class.forName(pluginClass);
            plugin = (CiePlugin) clazz.getDeclaredConstructor(Properties.class, boolean.class)
                    .newInstance(propietats, configuracioEspecifica);
            plugin.init(meterRegistry, getCodiApp().name(), codiEntitat);
            pluginMap.put(codiEntitat, plugin);
            return plugin;
        } catch (Exception ex) {
            var msg = "\"Error al crear la instància del plugin CIE (\" + pluginClass + \") \"";
            log.error(msg, ex);
            throw new SistemaExternException(IntegracioCodi.CIE.name(), msg, ex);
        }
    }

    @Override
    protected String getPluginClassProperty() {
        return configHelper.getConfig("es.caib.notib.plugin.cie.class");
    }

    @Override
    protected IntegracioApp getCodiApp() {
        return IntegracioApp.CIE;
    }

    @Override
    protected String getConfigGrup() {
        return GRUP;
    }

    @Override
    public boolean diagnosticar(Map diagnostics) throws Exception {
        return false;
    }


}
