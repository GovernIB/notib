package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
        auditNotificacioHelper.netejarErrorsNotifica(notificacio);
        for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
            auditEnviamentHelper.actualizaErrorNotifica(enviament, false, null);
        }
        notificacioEventRepository.deleteOldUselessEvents(notificacio);
    }

    public void addRegistreCallBackEstatEvent(NotificacioEntity notificacio,
                                              NotificacioEnviamentEntity enviament,
                                              String descripcio,
                                              boolean isError) {
        if (isError) {
            clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT);
        } else {
            deleteByNotificacioAndTipusAndError(
                    notificacio,
                    enviament,
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

    public void addRegistreConsultaInfoErrorEvent(NotificacioEntity notificacio,
                                                  NotificacioEnviamentEntity enviament,
                                                  String descripcio) {
        clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO);
        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio(descripcio).
                build();
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        enviament.updateNotificaError(true, event);
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

    public void addCallbackEvent(NotificacioEntity notificacio, NotificacioEventEntity event) {
        deleteByNotificacioAndTipusAndError(
                notificacio,
                null,
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
            clearUselessErrors(notificacio, null, NotificacioEventTipusEnumDto.CALLBACK_CLIENT);
            eventBuilder.error(true)
                    .errorDescripcio(event.getCallbackError());
        }
        NotificacioEventEntity callbackEvent = eventBuilder.build();
        updateNotificacio(notificacio, callbackEvent);
        notificacioEventRepository.saveAndFlush(callbackEvent);
    }

    public void addCallbackActivarEvent(NotificacioEnviamentEntity enviament) {
        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.CALLBACK_ACTIVAR,
                enviament.getNotificacio())
                .enviament(enviament)
                .callbackInicialitza()
                .build();
        notificacioEventRepository.saveAndFlush(event);


    }

    public void addNotificaConsultaSirErrorEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        deleteByNotificacioAndTipusAndError(
                notificacio,
                enviament,
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

        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        auditNotificacioHelper.updateNotificacioErrorSir(notificacio, event);
    }


    public void addEnviamentRegistreOKEvent(NotificacioEntity notificacio,
                                            String registreNum,
                                            Date registreData,
                                            NotificacioRegistreEstatEnumDto registreEstat,
                                            Set<NotificacioEnviamentEntity> enviaments,
                                            boolean totsAdministracio) {

        //Crea un nou event
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
                notificacio);

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();
        NotificacioEventEntity event = eventBuilder.build();

        logger.info(" >>> Canvi estat a REGISTRADA ");
        for(NotificacioEnviamentEntity enviamentEntity: enviaments) {
            auditEnviamentHelper.actualitzaRegistreEnviament(
                    notificacio,
                    enviamentEntity,
                    registreNum,
                    registreData,
                    registreEstat,
                    totsAdministracio,
                    event);
        }
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
    }
    /**
     * Registre un event a la notificació per a cada enviament realitzat correctament indicant que
     * la notificació s'ha enviat correctament a notifica.
     *
     * @param notificacio Notificació on afegir els events
     * @param identificadorsResultatsEnviaments Diccionari amb els enviaments realitzats
     */
    public void addEnviamentNotificaOKEvent(NotificacioEntity notificacio,
                                            Map<NotificacioEnviamentEntity, String> identificadorsResultatsEnviaments) {
        NotificacioEventEntity.Builder eventBuilder = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
                notificacio);
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();

        // TODO: Revisar generació d'events amb múltiples enviaments
        NotificacioEventEntity event = eventBuilder.build();

        for (NotificacioEnviamentEntity enviament: identificadorsResultatsEnviaments.keySet()) {
            auditEnviamentHelper.updateEnviamentEnviat(notificacio, event,
                    identificadorsResultatsEnviaments.get(enviament),
                    enviament);
        }
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
    }

    /**
     *
     * @param notificacio Notificació on es vol afegir l'event
     * @param enviament Enviament relacionat amb l'event
     * @param eventTipus NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT |
     *                   NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO
     * @param descripcio Text descriptiu de l'event
     */
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

    /**
     *
     * @param notificacio Notificació on es vol afegir l'event
     * @param enviament Enviament relacionat amb l'event
     * @param eventTipus NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT |
     *                   NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO
     * @param descripcio Text descriptiu de l'event
     * @param errorDescripcio Si l'event està asociat a un error definir aquest parametre descrivint-lo
     * @param initialitzaCallback indicar si s'ha d'inicialitzar el callball a l'event
     *
     * @return L'event creat
     */
    public NotificacioEventEntity addNotificaCallbackEvent(NotificacioEntity notificacio,
                                         NotificacioEnviamentEntity enviament,
                                         NotificacioEventTipusEnumDto eventTipus,
                                         String descripcio,
                                         String errorDescripcio,
                                         boolean initialitzaCallback) {
        if (errorDescripcio != null){
            clearUselessErrors(notificacio, enviament, eventTipus);
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

        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        return event;
    }

    public void addNotificaConsultaInfoEvent(NotificacioEntity notificacio,
                                         NotificacioEnviamentEntity enviament,
                                         String descripcio,
                                         boolean isError) {
        if (isError){
            clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO);
        } else {
            deleteByNotificacioAndTipusAndError(
                    notificacio,
                    enviament,
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

        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        enviament.updateNotificaError(isError, isError ? event : null);
    }

    public void addNotificaConsultaErrorEvent(NotificacioEntity notificacio,
                                             NotificacioEnviamentEntity enviament) {
        clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR);
        NotificacioEventEntity.Builder eventBuilder  = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a Notific@");
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventBuilder.callbackInicialitza();

        NotificacioEventEntity event = eventBuilder.build();

        updateNotificacio(notificacio, event, NotificacioErrorTipusEnumDto.ERROR_REINTENTS_CONSULTA);
        notificacioEventRepository.saveAndFlush(event);
    }

    private NotificacioEventEntity addErrorEvent(NotificacioEntity notificacio,
                              NotificacioEventTipusEnumDto eventTipus,
                              NotificacioEnviamentEntity enviament,
                              String errorDescripcio,
                              boolean notificaError) {
        clearUselessErrors(notificacio, enviament, eventTipus);
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
            notificacioEventRepository.saveAndFlush(event);
        } else {
            for (NotificacioEnviamentEntity enviamentEntity : notificacio.getEnviaments()) {
                eventBuilder.enviament(enviamentEntity);
                notificacioEventRepository.saveAndFlush(event);

                switch (eventTipus)
                {
                    case NOTIFICA_REGISTRE:
                        enviamentEntity.updateNotificaError(true, event);
                        break;
                    case NOTIFICA_ENVIAMENT:
                        auditEnviamentHelper.actualizaErrorNotifica(enviamentEntity, notificaError, event);
                        break;
                    default:
                        break;
                }
            }
        }
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        return event;
    }
    private void updateNotificacio(NotificacioEntity notificacio, NotificacioEventEntity eventCreat) {
        updateNotificacio(notificacio, eventCreat, null);
    }
    private void updateNotificacio(NotificacioEntity notificacio, NotificacioEventEntity eventCreat, NotificacioErrorTipusEnumDto tipusError) {
        notificacio.updateEventAfegir(eventCreat);
        List<NotificacioEventTipusEnumDto> errorsTipus = Arrays.asList(
                NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
                NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
                NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
                NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
                NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR,
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR
        );

        if (notificacio.isTipusUsuariAplicacio() && eventCreat.isError() && errorsTipus.contains(eventCreat.getTipus())){
            notificacio.setErrorLastEvent(true);
        }

        if (tipusError != null) {
            notificacio.updateNotificaError(tipusError, eventCreat);
        }
    }

    private void deleteByNotificacioAndTipusAndError(NotificacioEntity notificacio,
                                                     NotificacioEnviamentEntity enviament,
                                                     NotificacioEventTipusEnumDto tipus, boolean isError){
        if (isError) {
            for (NotificacioEventEntity e: new ArrayList<>(notificacio.getEvents())) {
                if (e.getTipus().equals(tipus) && e.isError() == isError){
                    preRemoveErrorEvent(e, notificacio, enviament);
                }
            }
        }

        for (NotificacioEventEntity e: new ArrayList<>(notificacio.getEvents())) {
            if (e.getTipus().equals(tipus) && e.isError() == isError){
                notificacio.getEvents().remove(e);
            }
        }
        notificacioEventRepository.deleteByNotificacioAndTipusAndError(
                notificacio,
                tipus,
                isError
        );

    }

    private void preRemoveErrorEvent(NotificacioEventEntity event, NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        NotificacioEventEntity eventNotificacioNotificaError = notificacio.getNotificaErrorEvent();
        if (eventNotificacioNotificaError != null && eventNotificacioNotificaError.getId() == event.getId()) {
            auditNotificacioHelper.netejarErrorsNotifica(notificacio);
        }
        event.getEnviament().setNotificacioErrorEvent(null);
        if (enviament != null) {
            enviament.setNotificacioErrorEvent(null);
        }
        if (event.getNotificacio() != null) {
            notificacio.getEvents().remove(event);
        }
    }

    /**
     * Si hi ha més d'un error associat a la notificació els elimina, conservant el més antic.
     *
     * @param notificacio Notificació dels events
     * @param errorType Tipus d'events d'error
     */
    private void clearUselessErrors(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto errorType) {
        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataAsc(notificacio,
                errorType, true);
        if (events.size() > 1) {
            // conservam l'event més antic i eliminam els intermitjos,
            // si tot va correctament em aquest punt la llista només tendra dos elements.
            NotificacioEventEntity event = events.get(1);
            preRemoveErrorEvent(event, notificacio, enviament);
            notificacioEventRepository.delete(event);
            notificacioEventRepository.flush();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(NotificacioEventHelper.class);
}
