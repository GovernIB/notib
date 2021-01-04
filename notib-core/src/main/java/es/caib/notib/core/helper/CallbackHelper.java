/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioEventEntity.Builder;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;

/**
 * Classe per englobar la tasca de notificar l'estat o la certificació a l'aplicació
 * client a partir de la referència del destinatari de la notificació.
 * 
 * Recupera la informació de la notificació a partir de la referència i la informació
 * de l'aplicació client a partir del codi d'usuari que ha creat l'anotació.
 * Emplena la informació cap al client de la mateixa forma que el WS de consulta  NotificacioWsServiceImpl.
 * 
 * @see NotificacioWsServiceImpl
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CallbackHelper {

	private static final String NOTIFICACIO_CANVI = "notificaCanvi";

	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;


	@Transactional (rollbackFor = RuntimeException.class)
	public NotificacioEntity notifica(Long eventId) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_CLIENT, 
				"Enviament d'avís de canvi d'estat", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Identificador de l'event", String.valueOf(eventId)));
		
		boolean ret = false;
		// Recupera l'event
		NotificacioEventEntity event = notificacioEventRepository.findOne(eventId);
		if (event == null)
			throw new NotFoundException("eventId:" + eventId, NotificacioEventEntity.class);
		NotificacioEntity notificacio = event.getNotificacio(); //notificacioRepository.findById(event.getNotificacioId());
		int intents = event.getCallbackIntents() + 1;
		Date ara = new Date();
		try {
			if (event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE
					|| event.getTipus() == NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR
					|| (event.isError() && event.getTipus() == NotificacioEventTipusEnumDto.CALLBACK_CLIENT)) {
				// Avisa al client que hi ha hagut una modificació a l'enviament
				String resposta = notificaCanvi(event.getEnviament());
				if ("INACTIVA".equals(resposta)) {
					// No s'ha d'enviar. El callback està inactiu
					event.updateCallbackClient(CallbackEstatEnumDto.PROCESSAT, ara, intents, null);
					auditNotificacioHelper.updateLastCallbackError(notificacio, false);
					return notificacio;
				}
				// Marca l'event com a notificat
				event.updateCallbackClient(CallbackEstatEnumDto.NOTIFICAT, ara, intents, null);
				auditNotificacioHelper.updateLastCallbackError(notificacio, false);
				ret = true;
				integracioHelper.addAccioOk(info);
			} else {
				// És un event pendent de notificar que no és del tipus esperat
				String errorDescripcio = "L'event id=" + event.getId() + " és del tipus " + event.getTipus() + " i no es pot notificar a l'aplicació client.";
				event.updateCallbackClient(CallbackEstatEnumDto.ERROR, ara, intents, errorDescripcio);
				auditNotificacioHelper.updateLastCallbackError(notificacio, true);
				integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat: " + errorDescripcio);
			}
		} catch (Exception ex) {
			logger.debug("Error notificant l'event " + eventId, ex);
			// Marca un error a l'event
			Integer maxIntents = this.getEventsIntentsMaxProperty();
			CallbackEstatEnumDto estatNou = maxIntents == null || intents < maxIntents ? 
												CallbackEstatEnumDto.PENDENT
												: CallbackEstatEnumDto.ERROR;
			event.updateCallbackClient(
					estatNou,
					ara,
					intents,
					"Error notificant l'event al client: " + ex.getMessage());
			auditNotificacioHelper.updateLastCallbackError(notificacio, true);
			integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat", ex);
		}
		notificacioRepository.save(notificacio);
		
		// Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
		Builder eventBuilder = null;
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
		if (!ret && eventBuilder != null) {
			eventBuilder.error(true)
						.errorDescripcio(event.getCallbackError());
		}
		NotificacioEventEntity callbackEvent = eventBuilder.build();
		if (event.getEnviament() != null)
			event.getEnviament().getNotificacio().updateEventAfegir(callbackEvent);
		else
			event.getNotificacio().updateEventAfegir(callbackEvent);
		notificacioEventRepository.save(callbackEvent);
		return notificacio;
	}

	private String notificaCanvi(NotificacioEnviamentEntity enviament) throws Exception {
		if (enviament == null)
			throw new Exception("El destinatari no pot ser nul.");
		
		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		UsuariEntity usuari = enviament.getCreatedBy();
		
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		if (aplicacio == null)
			throw new NotFoundException("codi usuari: " + usuari.getCodi(), AplicacioEntity.class);
		if (aplicacio.getCallbackUrl() == null)
			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
		if (!aplicacio.isActiva())
			return "INACTIVA";
				
		NotificacioCanviClient notificacioCanvi = new NotificacioCanviClient(
				notificaHelper.xifrarId(enviament.getNotificacio().getId()), 
				notificaHelper.xifrarId(enviament.getId()));

		// Passa l'objecte a JSON
		ObjectMapper mapper  = new ObjectMapper();
		String body = mapper.writeValueAsString(notificacioCanvi);
				
		// Prepara el client JSON per a la crida POST
		Client jerseyClient = this.getClient(aplicacio);

		// Completa la URL al mètode
		String urlBase = aplicacio.getCallbackUrl();
		String urlAmbMetode = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CANVI;
		
		// Fa la crida POST passant les dades JSON
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		// Comprova que la resposta sigui 200 OK
		if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
		} else {
			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO && isAllEnviamentsEstatFinal(enviament.getNotificacio())) {
				logger.info("Marcant notificació com processada per ser usuari aplicació...");
				enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.PROCESSADA);
				enviament.getNotificacio().updateMotiu("Notificació processada de forma automàtica. Estat final: " + enviament.getNotificaEstat());
				enviament.getNotificacio().updateEstatDate(new Date());
			}
		}

		return response.getEntity(String.class);
	}
	
	private boolean isAllEnviamentsEstatFinal(NotificacioEntity notificacio) {
		boolean estatsEnviamentsFinals = true;
		if (notificacio != null) {
			for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
				if (!enviament.isNotificaEstatFinal()) {
					estatsEnviamentsFinals = false;
					break;
				}
			}
		}
		return estatsEnviamentsFinals;
	}

	/** Propietat que assenayala el màxim de reintents. Si la propietat és null llavors no hi ha un màxim. */
	private Integer getEventsIntentsMaxProperty() {
		Integer ret = null;
		String maxIntents = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
		if (maxIntents != null && !"".equals(maxIntents))
			ret = Integer.parseInt(maxIntents);
		return ret;
	}

	private Client getClient(AplicacioEntity aplicacio) {
		Client jerseyClient =  new Client();
		// Només per depurar la sortida, esborrar o comentar-ho:
		jerseyClient.addFilter(new LoggingFilter(System.out));		
		return jerseyClient;
	}

	private static final Logger logger = LoggerFactory.getLogger(CallbackHelper.class);

}
