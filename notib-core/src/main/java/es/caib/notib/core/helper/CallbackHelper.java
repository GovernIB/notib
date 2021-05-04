package es.caib.notib.core.helper;

import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.AplicacioRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Classe per englobar la tasca de notificar l'estat o la certificació a l'aplicació
 * client a partir de la referència del destinatari de la notificació.
 * 
 * Recupera la informació de la notificació a partir de la referència i la informació
 * de l'aplicació client a partir del codi d'usuari que ha creat l'anotació.
 * Emplena la informació cap al client de la mateixa forma que el WS de consulta  NotificacioServiceWsImplV2.
 * 
 * @see es.caib.notib.core.service.ws.NotificacioServiceWsImplV2
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class CallbackHelper {

	private static final String NOTIFICACIO_CANVI = "notificaCanvi";

	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private RequestsHelper requestsHelper;

	@Transactional (rollbackFor = RuntimeException.class)
	public NotificacioEntity notifica(@NonNull NotificacioEventEntity event) throws Exception{
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_CLIENT, 
				"Enviament d'avís de canvi d'estat", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Identificador de l'event", String.valueOf(event.getId())));

		NotificacioEntity notificacio = event.getNotificacio();
//		info.getParams().add(new AccioParam("Identificador de la notificació", String.valueOf(notificacio.getId())));

		int intents = event.getCallbackIntents() + 1;
		log.debug(String.format("[Callback] Intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]",
				intents, event.getId(), notificacio.getId()));
		boolean isError = false;
		try {
			if (event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE
					|| event.getTipus() == NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR
					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR
					|| event.getTipus() == NotificacioEventTipusEnumDto.CALLBACK_ACTIVAR
					|| (event.isError() && event.getTipus() == NotificacioEventTipusEnumDto.CALLBACK_CLIENT)
			) {
				// Avisa al client que hi ha hagut una modificació a l'enviament
				String resposta = notificaCanvi(event.getEnviament());
				if ("INACTIVA".equals(resposta)) {
					// No s'ha d'enviar. El callback està inactiu
					log.debug(String.format("[Callback] No s'ha enviat el callback [Id: %d], el callback està inactiu.",
							event.getId()));
					event.updateCallbackClient(CallbackEstatEnumDto.PROCESSAT, intents, null, getIntentsPeriodeProperty());
					auditNotificacioHelper.updateLastCallbackError(notificacio, false);
					return notificacio;
				}
				// Marca l'event com a notificat
				event.updateCallbackClient(CallbackEstatEnumDto.NOTIFICAT, intents, null, getIntentsPeriodeProperty());
				auditNotificacioHelper.updateLastCallbackError(notificacio, false);
				integracioHelper.addAccioOk(info);
				log.debug(String.format("[Callback] Enviament del callback [Id: %d] de la notificacio [Id: %d] exitós",
						event.getId(), notificacio.getId()));
			} else {
				isError = true;
				// És un event pendent de notificar que no és del tipus esperat
				log.debug(String.format("[Callback] No s'ha pogut enviar el callback [Id: %d], el tipus d'event és incorrecte.",event.getId()));
				String errorDescripcio = "L'event id=" + event.getId() + " és del tipus " + event.getTipus() + " i no es pot notificar a l'aplicació client.";
				event.updateCallbackClient(CallbackEstatEnumDto.ERROR, intents, errorDescripcio, getIntentsPeriodeProperty());
				auditNotificacioHelper.updateLastCallbackError(notificacio, true);
				integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat: " + errorDescripcio);
			}
		} catch (Exception ex) {
			isError = true;
			log.debug(String.format("[Callback] Excepció notificant l'event [Id: %d]: %s", event.getId(), ex.getMessage()));
			ex.printStackTrace();
			// Marca un error a l'event
			Integer maxIntents = this.getEventsIntentsMaxProperty();
			CallbackEstatEnumDto estatNou = maxIntents == null || intents < maxIntents ? 
												CallbackEstatEnumDto.PENDENT
												: CallbackEstatEnumDto.ERROR;
			log.debug(String.format("[Callback] Actualitzam la base de dades amb l'error de l'event [Id: %d]", event.getId()));
			event.updateCallbackClient(
					estatNou,
					intents,
					"Error notificant l'event al client: " + ex.getMessage(),
					getIntentsPeriodeProperty());
			auditNotificacioHelper.updateLastCallbackError(notificacio, true);
			integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat", ex);
		}

		notificacioEventHelper.addCallbackEvent(notificacio, event, isError);

		log.debug(String.format("[Callback] Fi intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]",
				intents, event.getId(), notificacio.getId()));
		return notificacio;
	}

	private String notificaCanvi(NotificacioEnviamentEntity enviament) throws Exception {
		if (enviament == null)
			throw new Exception("El destinatari no pot ser nul.");
		
		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		UsuariEntity usuari = enviament.getCreatedBy();
		
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		if (aplicacio == null)
			throw new Exception(String.format("No s'ha trobat l'aplicació: codi usuari: %s, EntitatId: %d", usuari.getCodi(),
					enviament.getNotificacio().getEntitat().getId()));
		if (aplicacio.getCallbackUrl() == null)
			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
		if (!aplicacio.isActiva())
			return "INACTIVA";
				
		NotificacioCanviClient notificacioCanvi = new NotificacioCanviClient(
				notificaHelper.xifrarId(enviament.getNotificacio().getId()), 
				notificaHelper.xifrarId(enviament.getId()));

		// Completa la URL al mètode
		String urlBase = aplicacio.getCallbackUrl();
		String urlCallback = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CANVI;
		ClientResponse response = requestsHelper.callbackAplicacioNotificaCanvi(urlCallback, notificacioCanvi);
		
		// Comprova que la resposta sigui 200 OK
		if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
		} else {
			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO && isAllEnviamentsEstatFinal(enviament.getNotificacio())) {
				log.info("[Callback] Marcant notificació com processada per ser usuari aplicació...");
				enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.PROCESSADA);
				enviament.getNotificacio().updateMotiu("Notificació processada de forma automàtica. Estat final: " + enviament.getNotificaEstat());
				enviament.getNotificacio().updateEstatDate(new Date());
			}
		}

		return response.getEntity(String.class);
	}

	@Transactional
	public void marcarEventNoProcessable(@NonNull NotificacioEventEntity event,
										 String errorDescripcio,
										 String longErrorMessage){
		errorDescripcio = errorDescripcio == null ? "" : errorDescripcio;
		longErrorMessage = longErrorMessage == null ? "" : longErrorMessage;
		event.updateCallbackClient(
				CallbackEstatEnumDto.ERROR,
				getEventsIntentsMaxProperty(),
				"Error fatal: " + errorDescripcio + "\n" + longErrorMessage,
				getIntentsPeriodeProperty());
		log.debug(String.format("[Callback] Event [Id: %d] eliminat de la coa d'events per error fatal. Error: %s", event.getId(), errorDescripcio));
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

	private int getIntentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.callback.pendents.periode", 30000);
	}

}
