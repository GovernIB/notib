package es.caib.notib.core.service.callback;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.ws.callback.CallbackService;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioEventEntity.Builder;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.NotificacioEventRepository;

/**
 * Classe que implementa el servei de callback cap a les aplicacions clients de Notib
 * que estan configurades per rebre actualitzacions dels events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class CallbackServiceImpl implements CallbackService {
	    
    private CallbackService self;
    @Autowired private ApplicationContext applicationContext;
    
    /** Guarda la referència al bean CallbackService per poder-se invocar dins d'una transacció. */
    @PostConstruct
    public  void postContruct(){
        self = applicationContext.getBean(CallbackService.class);
    }
    
	@Resource
	CallbackHelper callbackHelper;
	
	@Resource
	private NotificacioEventRepository notificacioEventRepository;

	
	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.callback.notifica.events.periode}")
	public void notificarEventsPendens() {
				
		// Prepara la consulta
		int maxPendents = getEventsProcessarMaxProperty(); 
		Pageable page = new PageRequest(0, maxPendents, new Sort(new Order(Direction.ASC, "data")));
		
		// Consulta les notificacions pendents
		List<Long> pendentsIds = notificacioEventRepository.findEventsPendentsIds(page);

		if (pendentsIds.size() > 0) {
			logger.debug("Inici de les notificacions pendents cap a les aplicacions.");
			// Notificació de les peticions pendents
			int errors = 0;
			for(Long pendentsId : pendentsIds)
				// Invoca la notificació al bean
				if (!self.notifica(pendentsId))
					errors++;
			logger.debug("Fi de les notificacions pendents cap a les aplicacions: " + pendentsIds.size() + ", " + errors + " errors");
		}
	}

	@Override
	@Transactional
	public boolean notifica(Long eventId) {
		
		boolean ret = false;
		// Recupera l'event
		NotificacioEventEntity event = notificacioEventRepository.findOne(eventId);
		if (event == null)
			throw new NotFoundException(
					"eventId:" + eventId,
					NotificacioEventEntity.class);
		// Recupera la referència
		String referencia = null;
		if (event.getNotificacioDestinatari() != null)
			referencia = event.getNotificacioDestinatari().getReferencia();
		int intents = event.getCallbackIntents() + 1;
		Date ara = new Date();
		if (referencia != null) {
			// Notifica al client
			try {
				if (event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT
						|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) {
					// Invoca el mètode de notificació de l'aplicació client segons és estat o certificat:
					if (event.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT))
						callbackHelper.notificaEstat(event.getNotificacioDestinatari());
					else if (event.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO))
						callbackHelper.notificaCertificat(event.getNotificacioDestinatari());					
					// Marca l'event com a notificat
					event.updateCallbackClient(CallbackEstatEnumDto.NOTIFICAT, ara, intents, null);
					ret = true;
				} else {
					// És un event pendent de notificar que no és del tipus esperat
					event.updateCallbackClient(CallbackEstatEnumDto.ERROR, ara, intents, "L'event id=" + event.getId() + " és del tipus " + event.getTipus() + " i no es pot notificar a l'aplicació client.");
				}
			} catch (Exception e) {
				logger.debug("Error notificant l'event " + eventId + " amb referencia de destinatari " + referencia, e);
				// Marca un error a l'event
				Integer maxIntents = this.getEventsIntentsMaxProperty();
				CallbackEstatEnumDto estatNou = maxIntents == null || intents < maxIntents ? 
													CallbackEstatEnumDto.PENDENT
													: CallbackEstatEnumDto.ERROR;
				event.updateCallbackClient(estatNou, ara, intents, "Error notificant l'event al client: " + e.getMessage());
			}
		} else {
			// No és un event que es pugui notificar, el marca com a error
			event.updateCallbackClient(CallbackEstatEnumDto.ERROR, ara, intents, "L'event " + eventId + " no té referència de destinatari, no es pot fer un callback a l'aplicació client.");
		}
		
		// Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
		Builder eventBuilder = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
				event.getNotificacio()).
				notificacioDestinatari(event.getNotificacioDestinatari()).
				descripcio("Callback " + event.getTipus());
		if (!ret) {
			eventBuilder.error(true)
						.errorDescripcio(event.getCallbackError());
		}
		NotificacioEventEntity callbackEvent = eventBuilder.build();
		event.getNotificacio().updateEventAfegir(callbackEvent);
		
		return ret;
	}
	
	/** Propietat que assenayala el màxim de reintents. Si la propietat és null llavors no hi ha un màxim. */
	private Integer getEventsIntentsMaxProperty() {
		Integer ret = null;
		String maxIntents = PropertiesHelper.getProperties().getProperty("es.caib.notib.callback.notifica.events.intents.max");
		if (maxIntents != null && !"".equals(maxIntents))
			ret = Integer.parseInt(maxIntents);
		return ret;
	}	
	
	/** Propietat que assenayala el màxim d'events a processar en cada període.	 */
	private int getEventsProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.callback.notifica.events.processar.max",
				50);
	}

	private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);
}
