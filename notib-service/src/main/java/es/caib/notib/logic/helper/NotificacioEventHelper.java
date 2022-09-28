package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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

    /**
     * Elimina tots els events associats a la notificació indicada. Conserva els rellevants:
     *  - Enviament registre OK
     *  - Enviament notifica OK
     *  - Notifica confirmació finalitzat
     *
     * @param notificacio Notificació objectiu
     */
    public void clearOldUselessEvents(NotificacioEntity notificacio) {
        for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
            if (enviament.isNotificaError() && enviament.isPerEmail()) {
                continue;
            }
            auditEnviamentHelper.updateErrorNotifica(enviament, enviament.isNotificaError(), null);
        }
        notificacioEventRepository.deleteOldUselessEvents(notificacio);
    }

    public void clearOldNotificaUselessEvents(NotificacioEntity notificacio) {
        for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
            if (!enviament.isPerEmail())
                auditEnviamentHelper.updateErrorNotifica(enviament, enviament.isNotificaError(), null);
        }
        notificacioEventRepository.deleteOldNotificaUselessEvents(notificacio);
    }

    public void addRegistreCallBackEstatEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio, boolean isError) {

        if (isError) {
            clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT);
        } else {
            deleteByNotificacioAndTipusAndError(notificacio, enviament, NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,false );
        }
        NotificacioEventEntity.BuilderOld eventBuilder = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
                enviament.getNotificacio()).
                error(isError).
                enviament(enviament);
        if (isError) {
            eventBuilder.errorDescripcio(descripcio);
        } else {
            eventBuilder.descripcio(descripcio);
        }

        if (enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            eventBuilder.callbackInicialitza();
        }
        NotificacioEventEntity event = eventBuilder.build();

        notificacio.updateEventAfegir(event);
        enviament.updateNotificaError(isError, isError ? event : null);
        notificacioEventRepository.save(event);
    }

    public void addRegistreConsultaInfoErrorEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String descripcio) {

        clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO);
        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO, notificacio)
                                    .enviament(enviament).error(true).errorDescripcio(descripcio).build();
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        enviament.updateNotificaError(true, event);
    }

    public NotificacioEventEntity addNotificaRegistreEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio,
                                                NotificacioErrorTipusEnumDto errorTipus) {

        clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE);
        //Crea un nou event
        NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE).notificacio(notificacio)
                                    .error(true).errorTipus(errorTipus).errorDescripcio(errorDescripcio).build();

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            event.callbackInicialitza();
        }


        //Actualitza l'event per cada enviament
        if (enviament != null) {
            event.setEnviament(enviament);
            notificacioEventRepository.saveAndFlush(event);
        } else {
            for (NotificacioEnviamentEntity enviamentEntity : notificacio.getEnviaments()) {
                event.setEnviament(enviamentEntity);
                notificacioEventRepository.saveAndFlush(event);
                enviamentEntity.updateNotificaError(true, event);
            }
        }
        updateNotificacio(notificacio, event);
        return event;
    }

    public void addCallbackEvent(NotificacioEntity notificacio, NotificacioEventEntity event, boolean isError) {

        log.debug("[Events-CALLBACK_CLIENT] Intentam afegir nou event de callback a client");
        NotificacioEnviamentEntity enviamentEventOld = event.getEnviament();
        // Elimina tots els events de callback anteriors
        deleteByNotificacioAndTipusAndError(notificacio, null, NotificacioEventTipusEnumDto.CALLBACK_CLIENT, isError);

        log.trace("[Events-CALLBACK_CLIENT] Cream un nou event de callback a client");
        // Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
        NotificacioEventEntity.BuilderOld eventBuilder;
        if (event.getEnviament() != null) {
            eventBuilder = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.CALLBACK_CLIENT, notificacio)
                        .enviament(event.getEnviament()).descripcio("Callback " + event.getTipus());
        } else {
            eventBuilder = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.CALLBACK_CLIENT,notificacio).descripcio("Callback " + event.getTipus());
        }
        if (isError) {
            log.trace("[Events-CALLBACK_CLIENT] Eliminam els events d'error de callback a client innecessaris");
            clearUselessErrors(notificacio, null, NotificacioEventTipusEnumDto.CALLBACK_CLIENT);
            eventBuilder.error(true).errorDescripcio(event.getCallbackError());
        }
        // TODO: l'enviament de l'event s'esborra amb l'anotació preremove (degut a un problema d'inconsistencia). Tornar a assignar l'enviament només a aquest event (SOLUCIÓ TEMPORAL!!!)
     	eventBuilder.enviament(enviamentEventOld);
     		
        log.trace("[Events-CALLBACK_CLIENT] Actualitzam la base de dades");
        NotificacioEventEntity callbackEvent = eventBuilder.build();
        updateNotificacio(notificacio, callbackEvent);
        notificacioEventRepository.saveAndFlush(callbackEvent);
        log.trace("[Events-CALLBACK_CLIENT] Event afegit satisfactoriament.");
    }

    public void addCallbackActivarEvent(NotificacioEnviamentEntity enviament) {

        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.CALLBACK_ACTIVAR, enviament.getNotificacio())
                                    .enviament(enviament).callbackInicialitza().build();
        notificacioEventRepository.saveAndFlush(event);
    }

    public void addNotificaConsultaSirErrorEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {

        deleteByNotificacioAndTipusAndError(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR, true);
        NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR)
                                    .notificacio(notificacio).enviament(enviament).error(true).errorTipus(NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR)
                                    .errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a SIR").build();

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            event.callbackInicialitza();
        }
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
    }

    public void addEnviamentRegistreOKEvent(NotificacioEntity notificacio, String registreNum, Date registreData, NotificacioRegistreEstatEnumDto registreEstat,
                                            Set<NotificacioEnviamentEntity> enviaments, boolean totsAdministracio) {

        //Crea un nou event
        NotificacioEventEntity.BuilderOld eventBuilder = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE, notificacio);
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            eventBuilder.callbackInicialitza();
        }
        NotificacioEventEntity event = eventBuilder.build();
//        log.info(" >>> Canvi estat a REGISTRADA ");
        for(NotificacioEnviamentEntity enviamentEntity: enviaments) {
            auditEnviamentHelper.updateRegistreEnviament(notificacio, enviamentEntity, registreNum, registreData, registreEstat, totsAdministracio, event);
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
    public void addEnviamentNotificaOKEvent(NotificacioEntity notificacio, Map<NotificacioEnviamentEntity, String> identificadorsResultatsEnviaments) {

        NotificacioEventEntity.BuilderOld eventBuilder = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT, notificacio);
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            eventBuilder.callbackInicialitza();
        }
        // TODO: Revisar generació d'events amb múltiples enviaments
        NotificacioEventEntity event = eventBuilder.build();
        for (NotificacioEnviamentEntity enviament: identificadorsResultatsEnviaments.keySet()) {
            auditEnviamentHelper.updateEnviamentEnviat(notificacio, event, identificadorsResultatsEnviaments.get(enviament), enviament);
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
    public void addNotificaCallbackEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto eventTipus, String descripcio) {
        addNotificaCallbackEvent(notificacio, enviament, eventTipus, descripcio,null,true);
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
    public NotificacioEventEntity addNotificaCallbackEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto eventTipus,
                                         String descripcio, String errorDescripcio, boolean initialitzaCallback) {

        if (errorDescripcio != null){
            clearUselessErrors(notificacio, enviament, eventTipus);
        }
        NotificacioEventEntity.BuilderOld eventBuilder = NotificacioEventEntity.getBuilder(eventTipus, notificacio).enviament(enviament);
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
        enviament.updateNotificaError(errorDescripcio != null, errorDescripcio != null ? event : null);

        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        return event;
    }

    public void addNotificaConsultaInfoEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, String errorDescripcio, boolean isError) {

        if (isError){
            clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO);
        } else {
            deleteByNotificacioAndTipusAndError(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,false);
        }
        NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO)
                                    .notificacio(notificacio).enviament(enviament).error(isError).errorDescripcio(errorDescripcio).build();

        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        enviament.updateNotificaError(isError, isError ? event : null);
    }

    public void addNotificaConsultaErrorEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {

        clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR);
        NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR)
                                .notificacio(notificacio).enviament(enviament).error(true)
                                .errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a Notific@")
                                .errorTipus(NotificacioErrorTipusEnumDto.ERROR_REINTENTS_CONSULTA).build();

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            event.callbackInicialitza();
        }
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
    }

    public void addErrorEvent(NotificacioEntity notificacio, NotificacioEventTipusEnumDto eventTipus, String errorDescripcio,
                              NotificacioErrorTipusEnumDto errorTipus, boolean notificaError) {

        clearUselessErrors(notificacio, null, eventTipus);

        //Actualitza l'event per cada enviament
        for (NotificacioEnviamentEntity enviamentEntity : notificacio.getEnviaments()) {
            if (enviamentEntity.isPerEmail()) {
                continue;
            }
            //Crea un nou event
            NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(eventTipus).notificacio(notificacio).error(true).errorTipus(errorTipus)
                                        .errorDescripcio(errorDescripcio).enviament(enviamentEntity).build();

            if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
                event.callbackInicialitza();
            }
            notificacioEventRepository.saveAndFlush(event);
            // TODO fer per la notificacio no a cada enviament
            updateNotificacio(notificacio, event);
            switch (eventTipus)
            {
                case NOTIFICA_REGISTRE:
                    enviamentEntity.updateNotificaError(true, event);
                    break;
                case NOTIFICA_ENVIAMENT:
                    auditEnviamentHelper.updateErrorNotifica(enviamentEntity, notificaError, event);
                    break;
                default:
                    break;
            }
        }
    }

    // Enviament via Email

    // Afegir event a un enviament
    public void addNotificacioEmailEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, boolean isError, String errorDescripcio) {

//        clearUselessErrors(notificacio, enviament, NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT);
        NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT).notificacio(notificacio)
                                    .enviament(enviament).error(isError).errorDescripcio(errorDescripcio)
                                    .errorTipus(isError ? NotificacioErrorTipusEnumDto.ERROR_EMAIL : null).build();

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            event.callbackInicialitza();
        }
//        enviament.updateNotificaError(isError, isError ? event : null);
        notificacioEventRepository.saveAndFlush(event);
        if (!isError) {
            auditEnviamentHelper.updateEnviamentEmailFinalitzat(enviament);
        } else {
            auditEnviamentHelper.updateErrorNotifica(enviament, true, event);
        }
        clearUselessEnviamentErrors(enviament, NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT);
    }

    // Afegir event d'enviament correcte a la notificació
    public void addEnviamentEmailOKEvent(NotificacioEntity notificacio) {

        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT, notificacio).build();
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            event.callbackInicialitza();
        }
        updateNotificacio(notificacio, event);
        notificacioEventRepository.saveAndFlush(event);
        clearUselessErrors(notificacio, null, NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT);
    }

    // Afegir event d'error a la notificació
    public void addEnviamentEmailErrorEvent(NotificacioEntity notificacio) {

        String msg = "S'ha produït algun error en l'enviament via email. Els errors es poden consultar en cada un dels enviaments.";
        NotificacioEventEntity event = NotificacioEventEntity.builder().tipus(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT).notificacio(notificacio)
                                    .error(true).errorTipus(NotificacioErrorTipusEnumDto.ERROR_EMAIL).errorDescripcio(msg).build();
                                    // TODO ERROR MULTIIDIOMA
                                    // .errorDescripcio(messageHelper.getMessage("error.notificacio.enviaments.email")).build();

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            event.callbackInicialitza();
        }
        notificacioEventRepository.saveAndFlush(event);
        updateNotificacio(notificacio, event);
        clearUselessNotificacioErrors(notificacio, NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT);
    }

    private void updateNotificacio(NotificacioEntity notificacio, NotificacioEventEntity eventCreat) {
        notificacio.updateEventAfegir(eventCreat);
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

    /**
     * Si hi ha més d'un error associat a la notificació els elimina, conservant el més antic.
     *
     * @param notificacio Notificació dels events
     * @param errorType Tipus d'events d'error
     */
    private void clearUselessErrors(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto errorType) {

        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio, errorType, true);
        if (events != null && events.size() > 1) {
            // conservam l'event més antic i eliminam els intermitjos,
            // si tot va correctament em aquest punt la llista només tendra dos elements.
            for (int i = 1; i < events.size(); i++) {
                NotificacioEventEntity event = events.get(i);
                preRemoveErrorEvent(event, notificacio, enviament);
                notificacioEventRepository.delete(event);
            }
            notificacioEventRepository.flush();
        }
    }

    private void clearUselessNotificacioErrors(NotificacioEntity notificacio, NotificacioEventTipusEnumDto errorType) {

        List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioAndTipusAndErrorAndEnviamentIsNullOrderByDataDescIdDesc(notificacio, errorType, true);
        if (events != null && events.size() > 1) {
            // conservam l'event més antic i eliminam els intermitjos,
            // si tot va correctament em aquest punt la llista només tendra dos elements.
            for (int i = 1; i < events.size(); i++) {
                NotificacioEventEntity event = events.get(i);
                notificacioEventRepository.delete(event);
            }
            notificacioEventRepository.flush();
        }
    }

    private void clearUselessEnviamentErrors(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto errorType) {

        List<NotificacioEventEntity> events = notificacioEventRepository.findByEnviamentAndTipusAndErrorOrderByDataDescIdDesc(enviament, errorType, true);
        if (events != null && events.size() > 1) {
            // conservam l'event més antic i eliminam els intermitjos,
            // si tot va correctament em aquest punt la llista només tendra dos elements.
            for (int i = 1; i < events.size(); i++) {
                NotificacioEventEntity event = events.get(i);
                notificacioEventRepository.delete(event);
            }
            notificacioEventRepository.flush();
        }
    }

}
