package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.exception.EventException;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    private NotificacioEventRepository eventRepository;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;

    private static Boolean notificaConsultaActiva = null;

    public static void clearNotificaConsultaActiva() {
        notificaConsultaActiva = null;
    }
    public Boolean isNotificaConsultaActiva() {

        if (notificaConsultaActiva != null) {
            return notificaConsultaActiva;
        }
        try {
            return !configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu") &&
                    configHelper.getConfigAsBoolean("es.caib.notib.tasques.actives") &&
                    configHelper.getConfigAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
        } catch (Exception ex) {
            log.error("No s'ha pogut calcular si les consultes a notifica estan actives", ex);
            return true;
        }
    }

    public List<NotificacioEventEntity> getEvent(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto tipus) {
        return eventRepository.findByEnviamentIdAndTipus(enviament.getId(), tipus);
    }

    public NotificacioEventEntity addEvent(EventInfo eventInfo) {

        NotificacioEventEntity event = null;
        // Validacions
        if (eventInfo.getEnviament() == null) {
            throw new EventException("No s'ha indicat l'enviament al que assignar l'event");
        }
        if (eventInfo.getTipus() == null) {
            throw new EventException("No s'ha indicat el tipus d'event a generar");
        }
        // Només generem un event d'enviament (si hi ha reintents es modifica l'actual)
        // i de consulta SIR per ara (fins que no implementin el callback)
        var eventUnic = NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIO_OE.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.SIR_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(eventInfo.getTipus()) ||       // Per consulta Sir activa
                (isNotificaConsultaActiva() && NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA.equals(eventInfo.getTipus()));    // Per consulta notifica activa

        // Si es tracta d'un event de Callback, si ja n'existeix un, i aquest és amb error l'actualitzarem. Si no el crearem nou
        var eventNoUnic = NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT.equals(eventInfo.getTipus()) ||  NotificacioEventTipusEnumDto.API_CARPETA.equals(eventInfo.getTipus());
        var crearNouEvent = true;

        // Si es tracta d'un event únic o de callback comprovam si ja existeix un event del mateixtipus.
        // En aquest cas, si és un event únic s'actualitzarà, si és de callback, s'actualitzarà si l'existent és amb error
        if (eventUnic || eventNoUnic) {
            // Obtenim una llista per compatibilitat amb els events existents
            var events = eventRepository.findByEnviamentAndTipusOrderByIdDesc(eventInfo.getEnviament(), eventInfo.getTipus());
            if (events != null && !events.isEmpty()) {
                event = events.get(0);
                // Actualitza event
                if (!eventNoUnic || event.isError()) {
                    event.update(eventInfo.isError(), eventInfo.errorDescripcio, eventInfo.isFiReintents());
                    crearNouEvent = false;
                }
            }
        }
        // Si no es tracta d'un event únic, o aquest encara no existeix, es crea un event nou
        if (crearNouEvent) {
            event = NotificacioEventEntity.builder().notificacio(eventInfo.getEnviament().getNotificacio()).enviament(eventInfo.getEnviament())
                    .tipus(eventInfo.getTipus()).error(eventInfo.isError()).intents(1).fiReintents(eventInfo.isFiReintents()).errorDescripcio(eventInfo.getErrorDescripcio()).build();
        }
        if (!eventInfo.isError() && event.getFiReintents() && !NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(eventInfo.getTipus())) {
            event.setFiReintents(false);
        }
        if (eventInfo.getData() != null) {
            event.setData(eventInfo.getData());
        }
        eventRepository.saveAndFlush(event);
        // Actualitzar l'error de la notificació i enviament
        eventInfo.getEnviament().getNotificacio().updateEventAfegir(event);
        if (!eventNoUnic && ( NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT.equals(event.getTipus()) ||
                NotificacioEventTipusEnumDto.SIR_ENVIAMENT.equals(event.getTipus()) ||
                NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIO_OE.equals(eventInfo.getTipus()) ||
                NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT.equals(event.getTipus()))) {
            eventInfo.getEnviament().updateNotificaError(eventInfo.isError(), event);

            var notTable = notificacioTableViewRepository.findById(eventInfo.getEnviament().getNotificacio().getId()).get();
            notTable.setErrorLastEvent(event.isError());
            notificacioTableViewRepository.saveAndFlush(notTable);
        }
        return event;
    }

    // Events de Registre
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addRegistreEnviamentEvent(EventInfo eventInfo) {

        eventInfo.setTipus(NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT);
        addEvent(eventInfo);
    }

    public void addRegistreEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT).error(error)
                .errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addSirEnviamentEvent(EventInfo eventInfo) {

        eventInfo.setTipus(NotificacioEventTipusEnumDto.SIR_ENVIAMENT);
        addEvent(eventInfo);
    }

    public void addSirEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.SIR_ENVIAMENT)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addSirConsultaEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.SIR_CONSULTA)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addSirAdviserEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.SIR_ADVISER)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addSirConsultaEventFiPooling(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.SIR_CONSULTA)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    // Events de Notifica
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addNotificaEnviamentEvent(EventInfo eventInfo) {

        eventInfo.setTipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT);
        eventInfo.setData(eventInfo.getNotificacio().getNotificaEnviamentNotificaData());
        for (var enviament: eventInfo.notificacio.getEnviaments()) {
            if (enviament.isPerEmail()) {
                continue;
            }
            eventInfo.setEnviament(enviament);
            addEvent(eventInfo);
        }
    }

    public void addNotificaEnviamentEvent(NotificacioEntity notificacio, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        for (var enviament: notificacio.getEnviaments()) {
            if (enviament.isPerEmail()) {
                continue;
            }
            addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT)
                    .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
        }
    }

    public void addNotificaConsultaEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA).error(error)
                .errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addAdviserCertificacioEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.ADVISER_CERTIFICACIO)
                .error(error).errorDescripcio(errorDescripcio).build());
    }

    public void addAdviserDatatEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.ADVISER_DATAT)
                .error(error).errorDescripcio(errorDescripcio).build());
    }

    public void addCieEventEnviar(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CIE_ENVIAMENT)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addCieEventEnviar(NotificacioEntity notificacio, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        for (var enviament: notificacio.getEnviaments()) {
            addCieEventEnviar(enviament, error, errorDescripcio, errorMaxReintents);
        }
    }

    public void addCieEventCancelar(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CIE_CANCELAR)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addCieEventConsultaEstat(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CIE_CONSULTA_ESTAT)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addCieAdviserEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CIE_ADVISER)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addCieAdviserCertificacioEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CIE_ADVISER_CERTIFICACIO)
                .error(error).errorDescripcio(errorDescripcio).build());
    }

    public void addCieAdviserDatatEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CIE_ADVISER_DATAT)
                .error(error).errorDescripcio(errorDescripcio).build());
    }

    public void addNotificaAnular(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.NOTIFICA_ANULAR).error(error)
                .errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addNotificaEnvioOE(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.NOTIFICA_ENVIO_OE).error(error)
                .errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    public void addNotificaAmpliarPlazo(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.AMPLIAR_PLAZO_OE).error(error)
                .errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }

    // Events d'enviament via Email
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addEmailEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean fiReintents) {

         addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(fiReintents).build());
    }


    // Enviament de Callback
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addCallbackEnviamentEvent(NotificacioEnviamentEntity enviament, boolean error, String errorDescripcio, boolean errorMaxReintents) {

        addEvent(EventInfo.builder().enviament(enviament).tipus(NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT)
                .error(error).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build());
    }


    @Data
    @Builder
    public static class EventInfo {

        Date data;
        NotificacioEntity notificacio;
        NotificacioEnviamentEntity enviament;
        NotificacioEventTipusEnumDto tipus;
        boolean error;
        String errorDescripcio;
        boolean fiReintents;
    }
}
