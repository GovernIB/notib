package es.caib.notib.logic.accionsMassives;


import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElement;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.entity.AccioMassivaElementEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.AccioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccionsMassivesListener {

    private final AccioMassivaRepository accioMassivaRepository;
    private final NotificacioEnviamentRepository enviamentRepository;
    private final NotificacioService notificacioService;
    private final EnviamentService enviamentService;
    private final ConversioTipusHelper conversioTipusHelper;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_ACCIONS_MASSIVES, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveAccioMassiva(@Payload AccioMassivaExecucio accio, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        RespostaAccio<AccioMassivaElement> resposta;
//        List<String> errors;
        try {
            var error = false;
            Set<Long> seleccio = new HashSet<>(accio.getSeleccio());
            var tipus = accio.getSeleccioTipus().name().toLowerCase();
            /* Les seguents accions no passen per la cua:
                EXPORTAR_FULL_CALCUL, DESCARREGA_JUSTIFICANT_ENVIAMENT, DESCARREGA_CERTIFICAT_RECEPCIO, TORNA_ENVIAR_AMB_ERROR,
                ESBORRAR,
            * */
            switch (accio.getTipus()) {
                case TORNA_ACTIVAR_CONSULTES_CANVI_ESTAT:
                    resposta = notificacioService.resetConsultaEstat(accio);
                    actualitzarElements(accioEntity.getElements(), resposta);
                    error = !resposta.getErrors().isEmpty() || !resposta.getNoExecutables().isEmpty();
                    break;
                case REACTIVAR_SIR:
                    enviamentService.reactivaSir(accio);
                    break;
                case ACTUALITZAR_ESTAT:
                    for(var enviamentId : seleccio) {
                        try {
                            enviamentService.actualitzarEstat(enviamentId, accioEntity.getId());
//                            accioEntity.getElement(enviamentId).actualitzarData();
                        } catch (Exception ex) {
                            error = true;
                            accioEntity.getElement(enviamentId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                        }
                    }
                    break;
                case ENVIAR_CALLBACK:
                    try {
                        var enviamentsAmbError = enviamentService.enviarCallback(seleccio, accioEntity.getId());
                        error = !enviamentsAmbError.isEmpty();
                        accioEntity.setError(error);
                        if (error) {
                            for (var id : enviamentsAmbError) {
                                accioEntity.getElement(id).actualitzar("Error enviant el callback", "");
                            }
                        }
                    } catch (Exception ex) {
                        error = true;
                    }
                    break;
                case TORNA_ACTIVAR_CALLBACK:
                    for(var enviamentId : seleccio) {
                        try {
                            enviamentService.activarCallback(enviamentId);
                            accioEntity.getElement(enviamentId).actualitzar();
                        } catch (Exception ex) {
                            error = true;
                            accioEntity.getElement(enviamentId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                        }
                    }
                    accioEntity.setDataFi(new Date());
                    break;
                case REACTIVAR_REGISTRE:
                    for (var notificacioId : seleccio) {
                        try {
                            notificacioService.reactivarRegistre(notificacioId);
                            resposta = notificacioService.resetNotificacioARegistre(notificacioId);
                            actualitzarElements(accioEntity.getElements(), resposta);
                            error = !resposta.getErrors().isEmpty() || !resposta.getNoExecutables().isEmpty();
                        } catch (Exception ex) {
                            error = true;
                            accioEntity.getElement(notificacioId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                        }
                    }
                    break;
                case MARCAR_PROCESSADES:
                    for (var notificacioId : accio.getSeleccio()) {
                        try {
                            var resultat = notificacioService.marcarComProcessada(notificacioId, accio.getMotiu(), accio.isAdminEntitat());
                            if (Strings.isNullOrEmpty(resultat)) {
                                accioEntity.getElement(notificacioId).actualitzar();
                            } else {
                                error = true;
                                accioEntity.getElement(notificacioId).actualitzar(resultat, "");
                            }
                        } catch (Exception ex) {
                            error = true;
                            accioEntity.getElement(notificacioId).actualitzar(ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                        }
                    }
                    accioEntity.setDataFi(new Date());
                    break;
                case AMPLIAR_TERMINI:
                    accio.getAmpliacionPlazo().setAccioMassiva(accioEntity.getId());
                    var respostaAmpliarPlazo = notificacioService.ampliacionPlazoOE(accio.getAmpliacionPlazo());
                    NotificacioEnviamentEntity enviamentEntity;
                    Long id;
                    String errorAmpliacion;
                    for (var ampliacion : respostaAmpliarPlazo.getAmpliacionesPlazo().getAmpliacionPlazo()) {
                        enviamentEntity = enviamentRepository.findByUuid(ampliacion.getIdentificador()).orElseThrow();
                        errorAmpliacion = ampliacion.getMensajeError();
                        id = SeleccioTipus.NOTIFICACIO.equals(accio.getSeleccioTipus()) ? enviamentEntity.getNotificacio().getId() : enviamentEntity.getId();
                        accioEntity.getElement(id).actualitzar(errorAmpliacion, "");
                    }
                    break;
                case ENVIAR_NOT_MOVIL:
                    for (Long notificacioId : seleccio) {
                        try {
                            notificacioService.reenviarNotificaionsMovil(notificacioId);
                            accioEntity.getElement(notificacioId).actualitzar();
                        } catch (Exception e) {
                            error = true;
                            accioEntity.getElement(notificacioId).actualitzar(e.getMessage(), Arrays.toString(e.getStackTrace()));
                        }
                    }
                    accioEntity.setDataFi(new Date());
                    break;
                default:
                    log.error("[AccionsMassivesListener] Tipus accio massiva inexistent: " + accio.getTipus());
            }
            accioEntity.setError(error);
//            accioEntity.setErrorDescripcio(errorDescricpio);
//            accioEntity.setExcepcioStacktrace(excepcioStacktrace);
        } catch (Exception ex) {
            log.error("[AccionsMassivesListener] Error al executar no controlat l'acció massiva " + accio, ex);
            accioEntity.setError(true);
            accioEntity.setErrorDescripcio(ex.getMessage());
            accioEntity.setExcepcioStacktrace(Arrays.toString(ex.getStackTrace()));
        }
//        accioEntity.setDataFi(new Date());
        accioMassivaRepository.save(accioEntity);
    }

    @Transactional
    public List<AccioMassivaElementEntity> actualitzarElements(List<AccioMassivaElementEntity> elements, RespostaAccio<AccioMassivaElement> resposta) {

        var map = elements.stream().collect(Collectors.toMap(AccioMassivaElementEntity::getElementId, element -> element));
        for(var executades : resposta.getExecutades()) {
            map.get(executades.getId()).actualitzar();
        }
        for(var errors : resposta.getErrors()) {
            map.get(errors.getId()).actualitzar(errors.getErrorDesc(), errors.getErrorStackTrace());
        }
        for(var noExecutades : resposta.getNoExecutables()) {
            map.get(noExecutades.getId()).actualitzar(noExecutades.getErrorDesc(), noExecutades.getErrorStackTrace());
        }
        return map.values().stream().collect(Collectors.toList());
    }

}
