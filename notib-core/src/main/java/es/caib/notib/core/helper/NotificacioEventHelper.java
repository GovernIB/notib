package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper amb totes les operacions sobre els esdeveniments de les notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioEventHelper {
    @Autowired
    private AuditNotificacioHelper auditNotificacioHelper;
    @Autowired
    private NotificacioEventRepository notificacioEventRepository;
    @Autowired
    private AuditEnviamentHelper auditEnviamentHelper;

    /**
     * Elimina tots els events associats a la notificació indicada. Conserva els rellevants:
     *  - Enviament registre OK
     *  - Enviament notifica OK
     *  - Notifica confirmació finalitzat
     *
     * @param notificacio Notificació objectiu
     */
    public void clearOldUselessEvents(NotificacioEntity notificacio) {
        notificacioEventRepository.deleteOldUselessEvents(notificacio);
    }

    public NotificacioEventEntity defaultEventInstance(NotificacioEntity notificacio,
                                     NotificacioEventTipusEnumDto eventTipus){
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                eventTipus,
                notificacio);

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();
        return eventBuilder.build();

    }

    public NotificacioEventEntity addErrorEvent(NotificacioEntity notificacio,
                                                NotificacioEventTipusEnumDto eventTipus,
                                                NotificacioEnviamentEntity enviament,
                                                String errorDescripcio) {
        return addErrorEvent(notificacio, eventTipus, enviament, errorDescripcio, true);
    }

    public NotificacioEventEntity addErrorEvent(NotificacioEntity notificacio,
                                                NotificacioEventTipusEnumDto eventTipus,
                                                String errorDescripcio,
                                                boolean notificaError) {
        return addErrorEvent(notificacio, eventTipus, null, errorDescripcio, notificaError);
    }


    public void addCallbackEvent(NotificacioEntity notificacio,
                                 NotificacioEventEntity event) {
        notificacioEventRepository.deleteByNotificacioAndTipusAndError(
                notificacio,
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
                event.getCallbackEstat().equals(CallbackEstatEnumDto.ERROR)
        );

        // Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
        NotificacioEventEntity.Builder eventBuilder;
        if (event.getEnviament() != null) {
            eventBuilder = NotificacioEventEntity.getBuilder(
                    NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
                    notificacio). //event.getEnviament().getNotificacio()).
                    enviament(event.getEnviament()).
                    descripcio("Callback " + event.getTipus());
        } else {
            eventBuilder = NotificacioEventEntity.getBuilder(
                    NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
                    notificacio). // event.getNotificacio()).
                    descripcio("Callback " + event.getTipus());
        }
        if (event.getCallbackEstat().equals(CallbackEstatEnumDto.ERROR)) {
            clearUselessErrors(notificacio, NotificacioEventTipusEnumDto.CALLBACK_CLIENT);
            eventBuilder.error(true)
                    .errorDescripcio(event.getCallbackError());
        }
        NotificacioEventEntity callbackEvent = eventBuilder.build();
        if (event.getEnviament() != null)
            event.getEnviament().getNotificacio().updateEventAfegir(callbackEvent);
        else
            event.getNotificacio().updateEventAfegir(callbackEvent);
        notificacioEventRepository.save(callbackEvent);
    }

    public void addRegistreCallBackEstatEvent(NotificacioEntity notificacio,
                                              NotificacioEnviamentEntity enviament,
                                              String descripcio,
                                              boolean isError) {
        if (isError) {
            clearUselessErrors(notificacio, NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT);
        } else {
            notificacioEventRepository.deleteByNotificacioAndTipusAndError(
                    notificacio,
                    NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
                    false
            );
        }
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
                enviament.getNotificacio()).
                error(isError).
                enviament(enviament);
        if (isError) {
            eventBuilder.errorDescripcio(descripcio);
        } else {
            eventBuilder.descripcio(descripcio);
        }


        if (enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();
        NotificacioEventEntity event = eventBuilder.build();

        notificacio.updateEventAfegir(event);
        enviament.updateNotificaError(isError, isError ? event : null);
        notificacioEventRepository.save(event);
    }
    public void addRegistreConsultaInfoEvent(NotificacioEntity notificacio,
                                              NotificacioEnviamentEntity enviament,
                                              String descripcio) {
        clearUselessErrors(notificacio, NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO);
        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio(descripcio).
                build();
        notificacio.updateEventAfegir(event);
        notificacioEventRepository.save(event);
        enviament.updateNotificaError(true, event);
    }

    public void addNotificaConsultaSirErrorEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        notificacioEventRepository.deleteByNotificacioAndTipusAndError(
                notificacio,
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR,
                true
        );
        NotificacioEventEntity.Builder eventBuilder  = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a SIR");
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();

        NotificacioEventEntity event = eventBuilder.build();

        notificacio.updateEventAfegir(event);
        notificacioEventRepository.save(event);
        auditNotificacioHelper.updateNotificacioErrorSir(notificacio, event);
    }

    public void addNotificaEnviamentEvent(NotificacioEntity notificacio, ResultadoAltaRemesaEnvios resultadoAlta) {
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
                notificacio);
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();

        // TODO: Revisar generació d'events amb múltiples enviaments
        NotificacioEventEntity event = eventBuilder.build();

        for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
            for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
                if (enviament.getTitular().getNif().equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
                    auditEnviamentHelper.updateEnviamentEnviat(notificacio, event, resultadoEnvio, enviament);
                }
            }
        }
    }

    public void addNotificaCallbackEvent(NotificacioEntity notificacio,
                                         NotificacioEnviamentEntity enviament,
                                         NotificacioEventTipusEnumDto eventTipus,
                                         String descripcio) {
        addNotificaCallbackEvent(notificacio, enviament,
                eventTipus,
                descripcio,
                null,
                true
        );
    }

    public NotificacioEventEntity addNotificaCallbackEvent(NotificacioEntity notificacio,
                                         NotificacioEnviamentEntity enviament,
                                         NotificacioEventTipusEnumDto eventTipus,
                                         String descripcio,
                                         String errorDescripcio,
                                         boolean initialitzaCallback) {
        if (errorDescripcio != null){
            clearUselessErrors(notificacio, eventTipus);
        }
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                eventTipus,
                notificacio).
                enviament(enviament);
        if (descripcio != null) {
            eventBuilder.descripcio(descripcio);
        }
        if (errorDescripcio != null) {
            eventBuilder.errorDescripcio(errorDescripcio);
            eventBuilder.error(true);
        }
        if (initialitzaCallback && enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();

        NotificacioEventEntity event = eventBuilder.build();
        if (errorDescripcio != null) {
            logger.debug("Error event: " + event.getDescripcio());
        }
        enviament.updateNotificaError(errorDescripcio != null, errorDescripcio != null ? event : null);

        enviament.getNotificacio().updateEventAfegir(event);
        notificacioEventRepository.save(event);
        return event;
    }

    public void addNotificaConsultaInfoEvent(NotificacioEntity notificacio,
                                         NotificacioEnviamentEntity enviament,
                                         String descripcio,
                                         boolean isError) {
        if (isError){
            clearUselessErrors(notificacio, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO);
        } else {
            notificacioEventRepository.deleteByNotificacioAndTipusAndError(
                    notificacio,
                    NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
                    false
            );
        }
        NotificacioEventEntity.Builder eventBuilder =NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
                notificacio).
                enviament(enviament).error(isError);
        if (isError) {
            eventBuilder.descripcio(descripcio);
        }
        NotificacioEventEntity event = eventBuilder.build();

        notificacio.updateEventAfegir(event);
        notificacioEventRepository.save(event); // #235 Faltava desar l'event
        enviament.updateNotificaError(isError, isError ? event : null);
    }

    public void addNotificaConsultaErrorEvent(NotificacioEntity notificacio,
                                             NotificacioEnviamentEntity enviament) {
        clearUselessErrors(notificacio, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR);
        NotificacioEventEntity.Builder eventBuilder  = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a Notific@");
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();

        NotificacioEventEntity event = eventBuilder.build();

        notificacio.updateEventAfegir(event);
        notificacioEventRepository.save(event);
        notificacio.updateNotificaError(
                NotificacioErrorTipusEnumDto.ERROR_REINTENTS_CONSULTA,
                event);
    }

    private NotificacioEventEntity addErrorEvent(NotificacioEntity notificacio,
                              NotificacioEventTipusEnumDto eventTipus,
                              NotificacioEnviamentEntity enviament,
                              String errorDescripcio,
                              boolean notificaError) {
        clearUselessErrors(notificacio, eventTipus);
        //Crea un nou event
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                eventTipus,
                notificacio).
                error(true).
                errorDescripcio(errorDescripcio);

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            eventBuilder.callbackInicialitza();
        }

        NotificacioEventEntity event = eventBuilder.build();

        //Actualitza l'event per cada enviament
        if (enviament != null) {
            eventBuilder.enviament(enviament);

        } else {
            for (NotificacioEnviamentEntity enviamentEntity : notificacio.getEnviaments()) {
                eventBuilder.enviament(enviamentEntity);
                updateEnviamentErrorEvent(eventTipus, enviamentEntity, event, notificaError);
            }
        }

        notificacioEventRepository.saveAndFlush(event);
        return event;
    }

    private void updateEnviamentErrorEvent(NotificacioEventTipusEnumDto eventTipus,
                                      NotificacioEnviamentEntity enviament,
                                      NotificacioEventEntity event,
                                      boolean notificaError) {
        switch (eventTipus)
        {
            case NOTIFICA_REGISTRE:
                enviament.updateNotificaError(true, event);
                break;
            case NOTIFICA_ENVIAMENT:
                auditEnviamentHelper.actualizaErrorNotifica(enviament, notificaError, event);
                break;
            default:
                break;
        }
    }

    /**
     * Si hi ha més d'un error associat a la notificació els elimina conservant el més antic.
     *
     * @param notificacio Notificació dels events
     * @param errorType Tipus d'events d'error
     */
    private void clearUselessErrors(NotificacioEntity notificacio, NotificacioEventTipusEnumDto errorType) {
        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacio,
                errorType, true);
        if (events.size() > 1) {
            // conservam l'event més antic i eliminam els intermitjos,
            // si tot va correctament em aquest punt la llista només tendra dos elements.
            notificacioEventRepository.delete(events.get(1));
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(NotificacioEventHelper.class);
}
