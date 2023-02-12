package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.EventException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * Helper amb totes les operacions sobre els esdeveniments de les notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificacioEventHelper {

    @Autowired
    private NotificacioEventRepository notificacioEventRepository;
    @Autowired
    private AuditEnviamentHelper auditEnviamentHelper;
    @Resource
    private MessageHelper messageHelper;

    public NotificacioEventEntity addEvent(EventInfo eventInfo) {

        if (eventInfo.getNotificacio() == null && eventInfo.getEnviament() == null)
            throw new EventException("No s'ha indicat la notificació a la que assignar l'event");

        if (eventInfo.getNotificacio() == null) {
            eventInfo.setNotificacio(eventInfo.getEnviament().getNotificacio());
        }

        // TODO: Modificar aquest mètode
        deleteByNotificacioAndTipusAndError(eventInfo.getNotificacio(), eventInfo.getEnviament(), eventInfo.getTipus(),eventInfo.isError() );

            NotificacioEventEntity event = NotificacioEventEntity.builder()
                    .notificacio(eventInfo.getNotificacio())
                    .enviament(eventInfo.getEnviament())
                    .tipus(eventInfo.getTipus())
                    .descripcio(eventInfo.getDescripcio())
                    .error(eventInfo.isError())
                    .errorTipus(eventInfo.getErrorTipus())
                    .errorDescripcio(eventInfo.getErrorDescripcio()).build();


        if (eventInfo.isActivaCallback()) {
            boolean usuariAplicacio = eventInfo.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB;
            if (usuariAplicacio) {
                event.callbackInicialitza();
            }
        }

        eventInfo.getNotificacio().updateEventAfegir(event);
        notificacioEventRepository.saveAndFlush(event);

        if (eventInfo.getEnviament() != null) {
            eventInfo.getEnviament().updateNotificaError(eventInfo.isError(), eventInfo.isError() ? event : null);
        }
        return event;
    }



    // Events de Registre
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addRegistreEnviamentEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT)
                .descripcio(descripcio)
                .error(false)
                .activaCallback(true)
                .build());
    }

    public void addRegistreEnviamentEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(NotificacioEventHelper.EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .activaCallback(true)
                .build());

        if (errorMaxReintents) {
            addRegistreReintentsError(notificacio, enviament);
        }
    }

    public void addRegistreConsultaEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(NotificacioEventHelper.EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .activaCallback(false)
                .build());

        if (errorMaxReintents) {
            addRegistreReintentsError(notificacio, enviament);
        }
    }

    private void addRegistreReintentsError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR)
                .error(true)
                .errorTipus(NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR)
                .errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a SIR")
                .activaCallback(true)
                .build());
    }

    public void addRegistreNotificaEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE)
                .descripcio(descripcio)
                .error(false)
                .activaCallback(true)
                .build());
    }

    public void addRegistreNotificaEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .errorTipus(NotificacioErrorTipusEnumDto.ERROR_REGISTRE)
                .activaCallback(true)
                .build());
    }



    // Events de Notifica
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addNotificaEnviamentEvent(NotificacioEntity notificacio) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .error(false)
                .activaCallback(true)
                .build());
    }

    public void addNotificaEnviamentEventError(NotificacioEntity notificacio, NotificacioErrorTipusEnumDto errorTipus, String errorDescripcio) {
        NotificacioEventEntity event = addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .errorTipus(errorTipus)
                .activaCallback(true)
                .build());

        // TODO: Aquí nomes hauria d'actualitzar el link a l'event d'error dels enviaments
        for (NotificacioEnviamentEntity enviamentEntity : notificacio.getEnviaments()) {
            auditEnviamentHelper.updateErrorNotifica(enviamentEntity, true, event);
        }
    }

    public void addNotificaConsultaEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO)
                .error(false)
                .activaCallback(false)
                .build());
    }

    public void addNotificaConsultaEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .activaCallback(false)
                .build());

        if (errorMaxReintents) {
            addNotificaReintentsError(notificacio, enviament);
        }
    }

    private void addNotificaReintentsError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR)
                .error(true)
                .errorTipus(NotificacioErrorTipusEnumDto.ERROR_REINTENTS_CONSULTA)
                .errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a Notific@")
                .activaCallback(true)
                .build());
    }

    public void addNotificaCertificacioEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio, boolean activaCallback) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO)
                .descripcio(descripcio)
                .error(false)
                .activaCallback(activaCallback)
                .build());
    }

    public void addNotificaCertificacioEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio, String errorDescripcio, boolean activaCallback) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO)
                .descripcio(descripcio)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .activaCallback(activaCallback)
                .build());
    }

    public void addNotificaDatatEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio, boolean activaCallback) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT)
                .descripcio(descripcio)
                .error(activaCallback)
                .activaCallback(!enviament.isNotificaEstatFinal())
                .build());
    }

    public void addNotificaDatatEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio, String errorDescripcio, boolean activaCallback) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT)
                .descripcio(descripcio)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .activaCallback(activaCallback)
                .build());
    }


    // Enviament via Email
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addEmailEnviamentEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)
                .error(false)
                .activaCallback(true)
                .build());
    }

    public void addEmailEnviamentEventError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio) {
        NotificacioEventEntity event = addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)
                .error(true)
                .errorDescripcio(errorDescripcio)
                .errorTipus(NotificacioErrorTipusEnumDto.ERROR_EMAIL)
                .activaCallback(true)
                .build());

        auditEnviamentHelper.updateErrorNotifica(enviament, true, event);
    }


    // Enviament de Callback
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addCallbackEvent(NotificacioEntity notificacio, NotificacioEventEntity event, boolean isError) {

        log.debug("[Events-CALLBACK_CLIENT] Intentam afegir nou event de callback a client");
        NotificacioEnviamentEntity enviamentEventOld = event.getEnviament();

        NotificacioEventEntity nouEvent = addEvent(EventInfo.builder()
                .notificacio(notificacio)
                .enviament(enviamentEventOld)
                .tipus(NotificacioEventTipusEnumDto.CALLBACK_CLIENT)
                .descripcio("Callback " + event.getTipus())
                .error(isError)
                .errorDescripcio(isError ? event.getCallbackError() : null)
                .errorTipus(NotificacioErrorTipusEnumDto.ERROR_EMAIL)
                .activaCallback(true)
                .build());

//        // Elimina tots els events de callback anteriors
//        deleteByNotificacioAndTipusAndError(notificacio, null, NotificacioEventTipusEnumDto.CALLBACK_CLIENT, isError);
//
//        log.trace("[Events-CALLBACK_CLIENT] Cream un nou event de callback a client");
//        // Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
//        NotificacioEventEntity.BuilderOld eventBuilder;
//        if (event.getEnviament() != null) {
//            eventBuilder = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.CALLBACK_CLIENT, notificacio)
//                    .enviament(event.getEnviament()).descripcio("Callback " + event.getTipus());
//        } else {
//            eventBuilder = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.CALLBACK_CLIENT,notificacio)
//                    .descripcio("Callback " + event.getTipus());
//        }
//        if (isError) {
//            log.trace("[Events-CALLBACK_CLIENT] Eliminam els events d'error de callback a client innecessaris");
//            clearUselessErrors(notificacio, null, NotificacioEventTipusEnumDto.CALLBACK_CLIENT);
//            eventBuilder.error(true).errorDescripcio(event.getCallbackError());
//        }
//        // TODO: l'enviament de l'event s'esborra amb l'anotació preremove (degut a un problema d'inconsistencia). Tornar a assignar l'enviament només a aquest event (SOLUCIÓ TEMPORAL!!!)
//        eventBuilder.enviament(enviamentEventOld);
//
//        log.trace("[Events-CALLBACK_CLIENT] Actualitzam la base de dades");
//        NotificacioEventEntity callbackEvent = eventBuilder.build();
//        notificacio.updateEventAfegir(callbackEvent);
//        notificacioEventRepository.saveAndFlush(callbackEvent);
//        log.trace("[Events-CALLBACK_CLIENT] Event afegit satisfactoriament.");
    }

    public void addCallbackActivarEvent(NotificacioEnviamentEntity enviament) {

        NotificacioEventEntity event = NotificacioEventEntity.builder()
                .notificacio(enviament.getNotificacio())
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.CALLBACK_ACTIVAR).build();
        event.callbackInicialitza();

//        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.CALLBACK_ACTIVAR, enviament.getNotificacio())
//                .enviament(enviament).callbackInicialitza().build();
        notificacioEventRepository.saveAndFlush(event);
    }

    private void deleteByNotificacioAndTipusAndError(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament,
                                                     NotificacioEventTipusEnumDto tipus, boolean isError){

        if (isError && notificacio.getEvents() != null) {
            for (NotificacioEventEntity e: new ArrayList<>(notificacio.getEvents())) {
                if (e.getTipus().equals(tipus) && e.isError() == isError){
                    preRemoveErrorEvent(e, notificacio, enviament);
                }
            }
        }
        if (notificacio.getEvents() != null) {
            for (NotificacioEventEntity e : new ArrayList<>(notificacio.getEvents())) {
                if (tipus.equals(e.getTipus()) && e.isError() == isError) {
                    notificacio.getEvents().remove(e);
                }
            }
        }
        notificacioEventRepository.deleteByNotificacioAndTipusAndError(notificacio, tipus, isError);
    }

    private void preRemoveErrorEvent(NotificacioEventEntity event, NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {

        if (event.getEnviament() != null) {
            event.getEnviament().setNotificacioErrorEvent(null);
        }
        if (enviament != null) {
            enviament.setNotificacioErrorEvent(null);
        }
        if (event.getNotificacio() != null && notificacio.getEvents() != null) {
            notificacio.getEvents().remove(event);
        }
    }

//    /**
//     * Elimina tots els events associats a la notificació indicada. Conserva els rellevants:
//     *  - Enviament registre OK
//     *  - Enviament notifica OK
//     *  - Notifica confirmació finalitzat
//     *
//     * @param notificacio Notificació objectiu
//     */
//    public void clearOldUselessEvents(NotificacioEntity notificacio) {
//        for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
//            if (enviament.isNotificaError() && enviament.isPerEmail()) {
//                continue;
//            }
//            auditEnviamentHelper.updateErrorNotifica(enviament, enviament.isNotificaError(), null);
//        }
//        notificacioEventRepository.deleteOldUselessEvents(notificacio);
//    }

    public void clearOldNotificaUselessEvents(NotificacioEntity notificacio) {
        for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
            if (!enviament.isPerEmail())
                auditEnviamentHelper.updateErrorNotifica(enviament, enviament.isNotificaError(), null);
        }
        notificacioEventRepository.deleteOldNotificaUselessEvents(notificacio);
    }
//
//    /**
//     * Si hi ha més d'un error associat a la notificació els elimina, conservant el més antic.
//     *
//     * @param notificacio Notificació dels events
//     * @param errorType Tipus d'events d'error
//     */
//    private void clearUselessErrors(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto errorType) {
//
//        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio, errorType, true);
//        if (events != null && events.size() > 1) {
//            // conservam l'event més antic i eliminam els intermitjos,
//            // si tot va correctament em aquest punt la llista només tendra dos elements.
//            for (int i = 1; i < events.size(); i++) {
//                NotificacioEventEntity event = events.get(i);
//                preRemoveErrorEvent(event, notificacio, enviament);
//                notificacioEventRepository.delete(event);
//            }
//            notificacioEventRepository.flush();
//        }
//    }
//
//    private void clearUselessNotificacioErrors(NotificacioEntity notificacio, NotificacioEventTipusEnumDto errorType) {
//
//        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorAndEnviamentIsNullOrderByDataDescIdDesc(notificacio, errorType, true);
//        if (events != null && events.size() > 1) {
//            // conservam l'event més antic i eliminam els intermitjos,
//            // si tot va correctament em aquest punt la llista només tendra dos elements.
//            for (int i = 1; i < events.size(); i++) {
//                NotificacioEventEntity event = events.get(i);
//                notificacioEventRepository.delete(event);
//            }
//            notificacioEventRepository.flush();
//        }
//    }
//
//    private void clearUselessEnviamentErrors(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto errorType) {
//
//        List<NotificacioEventEntity> events = notificacioEventRepository.findByEnviamentAndTipusAndErrorOrderByDataDescIdDesc(enviament, errorType, true);
//        if (events != null && events.size() > 1) {
//            // conservam l'event més antic i eliminam els intermitjos,
//            // si tot va correctament em aquest punt la llista només tendra dos elements.
//            for (int i = 1; i < events.size(); i++) {
//                NotificacioEventEntity event = events.get(i);
//                notificacioEventRepository.delete(event);
//            }
//            notificacioEventRepository.flush();
//        }
//    }


    @Data
    @Builder
    public static class EventInfo {
        NotificacioEntity notificacio;
        NotificacioEnviamentEntity enviament;
        NotificacioEventTipusEnumDto tipus;
        String descripcio;
        boolean error;
        NotificacioErrorTipusEnumDto errorTipus;
        String errorDescripcio;
        boolean activaCallback;
    }
}
