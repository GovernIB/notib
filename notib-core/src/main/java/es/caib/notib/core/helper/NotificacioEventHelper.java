package es.caib.notib.core.helper;

import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
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

import java.util.List;

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
    private ConfigHelper configHelper;

    private static Boolean notificaConsultaActiva = null;

    public static void clearNotificaConsultaActiva() {
        notificaConsultaActiva = null;
    }
    public Boolean isNotificaConsultaActiva() {
        if (notificaConsultaActiva == null) {
            try {
                notificaConsultaActiva = !configHelper.getAsBoolean("es.caib.notib.adviser.actiu") &&
                        configHelper.getAsBoolean("es.caib.notib.tasques.actives") &&
                        configHelper.getAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
            } catch (Exception ex) {
                log.error("No s'ha pogut calcular si les consultes a notifica estan actives", ex);
                notificaConsultaActiva = true;
            }
        }
        return notificaConsultaActiva;
    }

    public NotificacioEventEntity addEvent(EventInfo eventInfo) {

        NotificacioEventEntity event = null;

        // Validacions
        if (eventInfo.getEnviament() == null)
            throw new EventException("No s'ha indicat l'enviament al que assignar l'event");
        if (eventInfo.getTipus() == null)
            throw new EventException("No s'ha indicat el tipus d'event a generar");

        // Només generem un event d'enviament (si hi ha reintents es modifica l'actual)
        // i de consulta SIR per ara (fins que no implementin el callback)
        boolean eventUnic = NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.SIR_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(eventInfo.getTipus()) ||       // Per consulta Sir activa
                (isNotificaConsultaActiva() && NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA.equals(eventInfo.getTipus()));    // Per consulta notifica activa

        // Si es tracta d'un event de Callback, si ja n'existeix un, i aquest és amb error l'actualitzarem. Si no el crearem nou
        boolean eventCallback = NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT.equals(eventInfo.getTipus());
        boolean crearNouEvent = true;

        // Si es tracta d'un event únic o de callback comprovam si ja existeix un event del mateixtipus.
        // En aquest cas, si és un event únic s'actualitzarà, si és de callback, s'actualitzarà si l'existent és amb error
        if (eventUnic || eventCallback) {
            // Obtenim una llista per compatibilitat amb els events existents
            List<NotificacioEventEntity> events = notificacioEventRepository.findByEnviamentAndTipusOrderByIdDesc(eventInfo.getEnviament(), eventInfo.getTipus());
            if (events != null && !events.isEmpty()) {
                event = events.get(0);
                // Actualitza event
                if (!eventCallback || eventCallback && event.isError()) {
                    event.update(eventInfo.isError(), eventInfo.errorDescripcio, eventInfo.isFiReintents());
                    crearNouEvent = false;
                }
            }
        }

        // Si no es tracta d'un event únic, o aquest encara no existeix, es crea un event nou
        if (crearNouEvent) {
            event = NotificacioEventEntity.builder()
                    .notificacio(eventInfo.getEnviament().getNotificacio())
                    .enviament(eventInfo.getEnviament())
                    .tipus(eventInfo.getTipus())
                    .error(eventInfo.isError())
                    .intents(1)
                    .fiReintents(eventInfo.isFiReintents())
                    .errorDescripcio(eventInfo.getErrorDescripcio()).build();
        }
        if (!eventInfo.isError() && event.getFiReintents()) {
            event.setFiReintents(false);
        }
        notificacioEventRepository.saveAndFlush(event);

        // Actualitzar l'error de la notificació i enviament
        eventInfo.getEnviament().getNotificacio().updateEventAfegir(event);
        if (!eventCallback) {
            eventInfo.getEnviament().updateNotificaError(eventInfo.isError(), eventInfo.isError() ? event : null);
        }
        return event;
    }

    // Events de Registre
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addRegistreEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .fiReintents(errorMaxReintents)
                .build());
    }

    public void addSirEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.SIR_ENVIAMENT)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .fiReintents(errorMaxReintents)
                .build());
    }

    public void addSirConsultaEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.SIR_CONSULTA)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .fiReintents(errorMaxReintents)
                .build());
    }


    // Events de Notifica
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addNotificaEnviamentEvent(NotificacioEntity notificacio, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
            if (InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
                continue;
            }
            addEvent(EventInfo.builder()
                    .enviament(enviament)
                    .tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                    .error(error)
                    .errorDescripcio(errorDescripcio)
                    .fiReintents(errorMaxReintents)
                    .build());
        }
    }

    public void addNotificaConsultaEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {
        addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .fiReintents(errorMaxReintents)
                .build());
    }

    public void addAdviserCertificacioEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {
        addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.ADVISER_CERTIFICACIO)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .build());
    }

    public void addAdviserDatatEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {
        addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.ADVISER_DATAT)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .build());
    }


    // Events d'enviament via Email
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addEmailEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {
        NotificacioEventEntity event = addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .build());
    }


    // Enviament de Callback
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addCallbackEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {
        NotificacioEventEntity event = addEvent(EventInfo.builder()
                .enviament(enviament)
                .tipus(NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT)
                .error(error)
                .errorDescripcio(errorDescripcio)
                .fiReintents(errorMaxReintents)
                .build());
    }


    @Data
    @Builder
    public static class EventInfo {
//        NotificacioEntity notificacio;
        NotificacioEnviamentEntity enviament;
        NotificacioEventTipusEnumDto tipus;
//        String descripcio;
        boolean error;
        String errorDescripcio;
        boolean fiReintents;
//        boolean activaCallback;
    }
}
