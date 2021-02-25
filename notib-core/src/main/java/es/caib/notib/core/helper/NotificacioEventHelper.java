package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void clearOldEvents(NotificacioEntity notificacio) {

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

    public void addSuccessEvent(NotificacioEventTipusEnumDto eventTipus) {

    }

    public void addCallbackEvent(NotificacioEntity notificacio,
                                 NotificacioEventEntity event) {
        // Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
        NotificacioEventEntity.Builder eventBuilder = null;
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
        if (event.getCallbackEstat().equals(CallbackEstatEnumDto.ERROR) && eventBuilder != null) {
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
        NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio(descripcio).
                build();
        notificacio.updateEventAfegir(event);
        notificacioEventRepository.save(event);
        enviament.updateNotificaError(
                true,
                event);
    }

    public void addNotificaConsultaSirErrorEvent(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament) {
        NotificacioEventEntity.Builder eventReintentsBuilder  = NotificacioEventEntity.getBuilder(
                NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR,
                notificacio).
                enviament(enviament).
                error(true).
                errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a SIR");
        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
            eventReintentsBuilder.callbackInicialitza();

        NotificacioEventEntity eventReintents = eventReintentsBuilder.build();

        notificacio.updateEventAfegir(eventReintents);
        notificacioEventRepository.save(eventReintents);
        auditNotificacioHelper.updateNotificacioErrorSir(notificacio, eventReintents);
    }

    private NotificacioEventEntity addErrorEvent(NotificacioEntity notificacio,
                              NotificacioEventTipusEnumDto eventTipus,
                              NotificacioEnviamentEntity enviament,
                              String errorDescripcio,
                              boolean notificaError) {
        //Crea un nou event
        NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
                eventTipus,
                notificacio).
                error(true).
                errorDescripcio(errorDescripcio);

        if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
            eventBulider.callbackInicialitza();
        }
        NotificacioEventEntity event = eventBulider.build();

        //Actualitza l'event per cada enviament
        if (enviament != null) {
            eventBulider.enviament(enviament);

        } else {
            for (NotificacioEnviamentEntity enviamentEntity : notificacio.getEnviaments()) {
                eventBulider.enviament(enviamentEntity);
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
     * Si hi ha més d'un error associat a la notificació elimina el darrer introduït.
     *
     * @param notificacio
     */
    private void clearUselessErrors(NotificacioEntity notificacio) {

    }
}
