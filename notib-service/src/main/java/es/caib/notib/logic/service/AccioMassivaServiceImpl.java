package es.caib.notib.logic.service;

import es.caib.notib.logic.accionsMassives.AccionsMassivesListener;
import es.caib.notib.logic.helper.AccioMassivaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDetall;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElement;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.entity.AccioMassivaEntity;
import es.caib.notib.persist.entity.AccioMassivaElementEntity;
import es.caib.notib.persist.repository.AccioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.statemachine.AccioMassivaElementRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AccioMassivaServiceImpl implements AccioMassivaService {

    @Autowired
    private AccioMassivaRepository accioMassivaRepository;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private EnviamentService enviamentService;
    @Autowired
    private JustificantService justificantService;
    @Autowired
    private NotificacioService notificacioService;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private NotificacioRepository notificacioRepository;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
    private AccionsMassivesListener accionsMassivesListener;
    @Autowired
    private AccioMassivaElementRepository accioMassivaElementRepository;
    @Autowired
    private AccioMassivaHelper accioMassivaHelper;

    @Override
    public PaginaDto<AccioMassivaDto> findAmbFiltre(AccioMassivaFiltre filtre, PaginacioParamsDto paginacioParams) {

        try {
            var pageable = getMappeigPropietats(paginacioParams);
            var accions = accioMassivaRepository.findAmbFiltre(filtre, pageable);
            return paginacioHelper.toPaginaDto(accions, AccioMassivaDto.class);
        } catch (Exception ex) {
            var msg = "Error carregant les dades de la taula d'accions massives";
            log.error(msg, ex);
            throw ex;
        }
    }

    @Override
    public List<AccioMassivaDetall> findDetall(Long accioId) {

        try {
            var accio = accioMassivaRepository.findById(accioId).orElseThrow();
            List<AccioMassivaDetall> detalls  = new ArrayList<>();
            AccioMassivaDetall detall;
            String referencia;
            boolean tipusNotificacio;
            for (var element : accio.getElements()) {
                tipusNotificacio = SeleccioTipus.NOTIFICACIO.equals(element.getSeleccioTipus());
                referencia = tipusNotificacio ? notificacioRepository.findById(element.getElementId()).orElseThrow().getReferencia()
                : notificacioEnviamentRepository.findById(element.getElementId()).orElseThrow().getUuid();
                detall = AccioMassivaDetall.builder()
                        .referencia(referencia)
                        .seleccioTipus(element.getSeleccioTipus())
                        .data(element.getDataExecucio())
                        .errorDesc(element.getErrorDescripcio())
                        .errorStacktrace(element.getExcepcioStackTrace()).build();
                detalls.add(detall);
            }
            return detalls;
        } catch (Exception ex) {
            log.error("Error buscant el detall de l'accio massiva " + accioId, ex);
            return null;
        }
    }

    private Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {

        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
//        mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
//        mapeigPropietatsOrdenacio.put("endpoint", new String[] {"usuariCodi"});
//        mapeigPropietatsOrdenacio.put("data", new String[] {"data"});

//        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
        return paginacioHelper.toSpringDataPageable(paginacioParams);
    }

    // Execucio d'accions massives

    @Transactional
    @Override
    public Long altaAccioMassiva(AccioMassivaExecucio accio) {

        try {
            var entity = AccioMassivaEntity.builder().tipus(accio.getTipus()).entitatId(accio.getEntitatId()).build();
            entity = accioMassivaRepository.saveAndFlush(entity);
            AccioMassivaElementEntity elem;
            for (var element : accio.getSeleccio()) {
                elem = AccioMassivaElementEntity.builder().accioMassiva(entity).elementId(element).seleccioTipus(accio.getSeleccioTipus()).build();
                var elementEntity = accioMassivaElementRepository.saveAndFlush(elem);
            }
            return entity.getId();
        } catch (Exception ex) {
            log.error("Error creant l'accio massiva de tipus " + accio.getTipus() + " per l'entitat " + accio.getEntitatId(), ex);
            throw ex;
        }
    }

    @Transactional
    @Override
    public FitxerDto exportar(AccioMassivaExecucio accio) throws Exception {

        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        FitxerDto fitxer = null;
        try {
            fitxer = enviamentService.exportacio(accio.getEntitatId(), accio.getSeleccio(), accio.getFormat());
            for (var element :  accioEntity.getElements()) {
                element.actualitzar();
            }
            accioEntity.setError(false);
        } catch (Exception ex) {
            log.error("Error en l'accio massiva exporatar", ex);
            accioEntity.setError(true);
            accioEntity.setErrorDescripcio(ex.getMessage());
            accioEntity.setExcepcioStacktrace(Arrays.toString(ex.getStackTrace()));
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.save(accioEntity);
        return fitxer;
    }

    @Transactional
    @Override
    public List<FitxerDto> descarregarJustificant(AccioMassivaExecucio accio) {

        List<FitxerDto> justificants = new ArrayList<>();
        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        try {
            var seqNum = 0;
            FitxerDto justificant;
            for (var notificacioId : accio.getSeleccio()) {
                var sequence = "sequence" + UUID.randomUUID();
                seqNum++;
                try {
                    justificant = justificantService.generarJustificantEnviament(notificacioId, accio.getEntitatId(), sequence);
                    if (justificant == null) {
                        log.error("[MASSIVA DESCARREGAR JUSTIFICANT] Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document. Notificacio id " + notificacioId);
                        accioEntity.getElement(notificacioId).actualitzar("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document","");
                        continue;
                    }
                    accioEntity.getElement(notificacioId).actualitzar();
                } catch (Exception ex) {
                    log.error("Error descarregant el justificant per la notificacio " + notificacioId + " " + ex.getMessage());
                    accioEntity.getElement(notificacioId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                    continue;
                }
                justificants.add(justificant);
            }
            accioEntity.setError(false);
        } catch (Exception ex) {
            log.error("Error descarregant els justificants", ex);
            accioEntity.setError(true);
            accioEntity.setErrorDescripcio(ex.getMessage());
            accioEntity.setExcepcioStacktrace(Arrays.toString(ex.getStackTrace()));
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.save(accioEntity);
        return justificants;
    }

    @Transactional
    @Override
    public List<List<ArxiuDto>> descarregarCertificacio(AccioMassivaExecucio accio) {

        List<List<ArxiuDto>> certificacions = new ArrayList<>();
        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        try {
            List<ArxiuDto> notCertificacions;
            var contingut = false;
            for (var notificacioId : accio.getSeleccio()) {
                var enviaments = enviamentService.enviamentFindAmbNotificacio(notificacioId);
                Map<String, Integer> interessats = new HashMap<>();
                int numInteressats = 0;
                notCertificacions = new ArrayList<>();
                ArxiuDto certificacio;
                for (var env : enviaments) {
                    if (env.getNotificaCertificacioData() == null) {
                        continue;
                    }
                    try {
                        certificacio = notificacioService.enviamentGetCertificacioArxiu(env.getId());
                    } catch (Exception ex) {
                        log.error("Error descarregant la certificacio per l'enviament " + env.getId());
                        accioEntity.getElement(notificacioId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                        continue;
                    }
                    certificacio.setNom(env.getTitular().getNif() + "_" + certificacio.getNom());
                    if (interessats.get(env.getTitular().getNif()) == null) {
                        numInteressats++;
                        interessats.put(env.getTitular().getNif(), numInteressats);
                        certificacio.setNom(numInteressats + "_" + certificacio.getNom());
                    }
                    contingut = true;
                    notCertificacions.add(certificacio);
                }
                accioEntity.getElement(notificacioId).actualitzar();
                if (!contingut) {
                    continue;
                }
                certificacions.add(notCertificacions);
            }
            accioEntity.setError(false);
        } catch (Exception ex) {
            log.error("Error descarregant les certificacions", ex);
            accioEntity.setError(true);
            accioEntity.setErrorDescripcio(ex.getMessage());
            accioEntity.setExcepcioStacktrace(Arrays.toString(ex.getStackTrace()));
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.save(accioEntity);
        return certificacions;
    }

    @Transactional
    @Override
    public RespostaAccio<AccioMassivaElement> reactivarErrors(AccioMassivaExecucio accio) {

        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        RespostaAccio<AccioMassivaElement> resposta = null;
        try {
            Set<Long> seleccio = new HashSet<>(accio.getSeleccio());
            resposta = notificacioService.reactivarNotificacioAmbErrors(seleccio);
            accioEntity.setError(resposta.getErrors().isEmpty() || resposta.getNoExecutables().isEmpty());
            accionsMassivesListener.actualitzarElements(accioEntity.getElements(), resposta);
        } catch (Exception ex) {
            log.error("Error reactivant remeses/enviaments amb error", ex);
            accioEntity.setError(true);
            accioEntity.setErrorDescripcio(ex.getMessage());
            accioEntity.setExcepcioStacktrace(Arrays.toString(ex.getStackTrace()));
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.save(accioEntity);
        return resposta;
    }

    @Transactional
    @Override
    public List<AccioMassivaDetall> esborrarNotificacions(AccioMassivaExecucio accio) {

        List<AccioMassivaDetall> notificacionsNoEsborrades = new ArrayList<>();
        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        for (var notificacioId : accio.getSeleccio()) {
            try {
                notificacioService.delete(accio.getEntitatId(), notificacioId);
                accioEntity.getElement(notificacioId).actualitzar();
            } catch (Exception ex) {
                var msg = "Hi ha hagut un error esborrant la notificació ";
                log.error(msg + notificacioId, ex);
                var errorDesc = String.format(msg + " (Id: %s): %s", notificacioId, ex.getMessage());
                var error = AccioMassivaDetall.builder().id(notificacioId).errorDesc(errorDesc).build();
                accioEntity.getElement(notificacioId).actualitzar(errorDesc, "");
//                accioEntity.getElement(notificacioId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                notificacionsNoEsborrades.add(error);
            }
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.saveAndFlush(accioEntity);
        return notificacionsNoEsborrades;
    }

    @Override
    public List<AccioMassivaDetall> recuperarNotificacionsEsborrades(AccioMassivaExecucio accio) {

        List<AccioMassivaDetall> notificacionsNoEsborrades = new ArrayList<>();
        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        for (var notificacioId : accio.getSeleccio()) {
            try {
                notificacioService.restore(accio.getEntitatId(), notificacioId);
                accioEntity.getElement(notificacioId).actualitzar();
            } catch (Exception ex) {
                log.error("Hi ha hagut un error recuperant la notificacio " + notificacioId, ex);
                var errorDesc = String.format("Hi ha hagut un error recuperant la notificació (Id: %s): %s", notificacioId, ex.getMessage());
                var error = AccioMassivaDetall.builder().id(notificacioId).errorDesc(errorDesc).build();
                accioEntity.getElement(notificacioId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                notificacionsNoEsborrades.add(error);
            }
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.saveAndFlush(accioEntity);
        return notificacionsNoEsborrades;
    }

    @Override
    public void executarAccio(AccioMassivaExecucio accio) {

        jmsTemplate.convertAndSend(SmConstants.CUA_ACCIONS_MASSIVES, accio,
                m -> {m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000L);return m;
        });
    }

}


