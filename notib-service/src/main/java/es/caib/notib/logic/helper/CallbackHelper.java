package es.caib.notib.logic.helper;

import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.ws.callback.NotificacioCanviClient;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.logic.service.ws.NotificacioServiceWsImplV2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe per englobar la tasca de notificar l'estat o la certificació a l'aplicació
 * client a partir de la referència del destinatari de la notificació.
 * 
 * Recupera la informació de la notificació a partir de la referència i la informació
 * de l'aplicació client a partir del codi d'usuari que ha creat l'anotació.
 * Emplena la informació cap al client de la mateixa forma que el WS de consulta  NotificacioServiceWsImplV2.
 * 
 * @see es.caib.notib.logic.service.ws.NotificacioServiceWsImplV2
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class CallbackHelper {

	private static final String NOTIFICACIO_CANVI = "notificaCanvi";
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
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
	@Autowired
	private ConfigHelper configHelper;

	@Transactional (rollbackFor = RuntimeException.class)
	public boolean notifica(@NonNull Long eventId) throws Exception {

		var event = notificacioEventRepository.findById(eventId).orElse(null);
		if (event == null) {
			return false;
		}
		var notificacioProcessada = notifica(event);
		return notificacioProcessada == null || !notificacioProcessada.isErrorLastCallback();
	}

	@Transactional (rollbackFor = RuntimeException.class)
	public NotificacioEntity notifica(@NonNull NotificacioEventEntity event) throws Exception{

		log.trace("[Callback] Consultant aplicació de l'event. ");
		var aplicacio = getAplicacio(event);
		var notificacio = event.getNotificacio();
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_CLIENT,
				String.format("Enviament d'avís de canvi d'estat (%s)", aplicacio.getCallbackUrl()),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'event", String.valueOf(event.getId())),
				new AccioParam("Identificador de la notificació", String.valueOf(notificacio.getId())),
				new AccioParam("Codi aplicació", aplicacio.getUsuariCodi()),
				new AccioParam("Callback", aplicacio.getCallbackUrl()));

		info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
		info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
		var intents = event.getCallbackIntents() + 1;
		log.info(String.format("[Callback] Intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]",
				intents, event.getId(), notificacio.getId()));
		var isError = false;
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
				var enviament = event.getEnviament();
				var start = System.nanoTime();
				notificaCanvi(enviament, aplicacio.getCallbackUrl());
				var elapsedTime = System.nanoTime() - start;
				log.info("notificaCanvi "  + elapsedTime);

				//Marcar com a processada si la notificació s'ha fet des de una aplicació
				if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.APLICACIO && isAllEnviamentsEstatFinal(notificacio)) {
					log.info("[Callback] Marcant notificació com processada per ser usuari aplicació...");
					start = System.nanoTime();
					auditNotificacioHelper.updateNotificacioProcessada(notificacio, "Notificació processada de forma automàtica. Estat final: " + enviament.getNotificaEstat());
					elapsedTime = System.nanoTime() - start;
					log.info("marca processada: "  + elapsedTime);
				}

				// TODO: Això no hauria d'estar abans de la crida a notificaCanvi?
				if (!aplicacio.isActiva()) {
					// No s'ha d'enviar. El callback està inactiu
					log.info(String.format("[Callback] No s'ha enviat el callback [Id: %d], el callback està inactiu.", event.getId()));
					start = System.nanoTime();
					event.updateCallbackClient(CallbackEstatEnumDto.PROCESSAT, intents, null, getIntentsPeriodeProperty());
					auditNotificacioHelper.updateLastCallbackError(notificacio, false);
					elapsedTime = System.nanoTime() - start;
					log.info("el callback esta inactiu: "  + elapsedTime);
					return notificacio;
				}

				// Marca l'event com a notificat
				start = System.nanoTime();
				event.updateCallbackClient(CallbackEstatEnumDto.NOTIFICAT, intents, null, getIntentsPeriodeProperty());
				auditNotificacioHelper.updateLastCallbackError(notificacio, false);
				integracioHelper.addAccioOk(info);
				log.info(String.format("[Callback] Enviament del callback [Id: %d] de la notificacio [Id: %d] exitós", event.getId(), notificacio.getId()));
				elapsedTime = System.nanoTime() - start;
				log.info("marcar com a notificat: "  + elapsedTime);
			} else {
				isError = true;
				// És un event pendent de notificar que no és del tipus esperat
				var start = System.nanoTime();
				log.info(String.format("[Callback] No s'ha pogut enviar el callback [Id: %d], el tipus d'event és incorrecte.",event.getId()));
				var errorDescripcio = "L'event id=" + event.getId() + " és del tipus " + event.getTipus() + " i no es pot notificar a l'aplicació client.";
				event.updateCallbackClient(CallbackEstatEnumDto.ERROR, intents, errorDescripcio, getIntentsPeriodeProperty());
				auditNotificacioHelper.updateLastCallbackError(notificacio, true);
				integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat: " + errorDescripcio);
				var elapsedTime = System.nanoTime() - start;
				log.info("event pendent notifica no tipus esperat: "  + elapsedTime);
			}
		} catch (Exception ex) {
			var start = System.nanoTime();
			isError = true;
			log.info(String.format("[Callback] Excepció notificant l'event [Id: %d]: %s", event.getId(), ex.getMessage()));
			ex.printStackTrace();
			// Marca un error a l'event
			var maxIntents = this.getEventsIntentsMaxProperty();
			var estatNou = maxIntents == null || intents < maxIntents ? CallbackEstatEnumDto.PENDENT : CallbackEstatEnumDto.ERROR;
			log.info(String.format("[Callback] Actualitzam la base de dades amb l'error de l'event [Id: %d]", event.getId()));
			event.updateCallbackClient(estatNou, intents, "Error notificant l'event al client: " + ex.getMessage(), getIntentsPeriodeProperty());
			auditNotificacioHelper.updateLastCallbackError(notificacio, true);
			integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat", ex);
			var elapsedTime = System.nanoTime() - start;
			log.info("escepcio: "  + elapsedTime);
		}
		var start = System.nanoTime();
		notificacioEventHelper.addCallbackEvent(notificacio, event, isError);
		log.info(String.format("[Callback] Fi intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, event.getId(), notificacio.getId()));
		var elapsedTime = System.nanoTime() - start;
		log.info("addCallbackEvent: "  + elapsedTime);
		return notificacio;
	}

	public String notificaCanvi(@NonNull NotificacioEnviamentEntity enviament, @NonNull String urlBase) throws Exception {

		var notificacioCanvi = NotificacioServiceWsImplV2.isValidUUID(enviament.getNotificaReferencia()) ?
				new NotificacioCanviClient(enviament.getNotificacio().getReferencia(), enviament.getNotificaReferencia()) :
				new NotificacioCanviClient(notificaHelper.xifrarId(enviament.getNotificacio().getId()), notificaHelper.xifrarId(enviament.getId()));

		// Completa la URL al mètode
		var urlCallback = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CANVI;
		var response = requestsHelper.callbackAplicacioNotificaCanvi(urlCallback, notificacioCanvi);

		// Comprova que la resposta sigui 200 OK
		if (ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
		}
		return response.getEntity(String.class);
	}

	private AplicacioEntity getAplicacio(@NonNull NotificacioEventEntity event) throws Exception {

		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		var enviament = event.getEnviament();
		var usuari = enviament.getCreatedBy().get();
		var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		String errorMessage = null;
		if (aplicacio == null) {
			errorMessage = String.format("No s'ha trobat l'aplicació: codi usuari: %s, EntitatId: %d", usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		} else if (aplicacio.getCallbackUrl() == null) {
			errorMessage = "La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada";
		}
		if (errorMessage == null) {
			return aplicacio;
		}
		var info = new IntegracioInfo(
				IntegracioHelper.INTCODI_CLIENT,
				"Enviament d'avís de canvi d'estat",
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'event", String.valueOf(event.getId())),
				new AccioParam("Codi aplicació", aplicacio != null ? aplicacio.getUsuariCodi() : ""),
				new AccioParam("Identificador de la notificacio", String.valueOf(enviament.getNotificacio().getId())));

		info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
		var msg = "Error notificant l'event al client: " + errorMessage;
		event.updateCallbackClient(CallbackEstatEnumDto.ERROR, getEventsIntentsMaxProperty(), msg, getIntentsPeriodeProperty());
		integracioHelper.addAccioError(info, "Error consultant l'aplicació: " + errorMessage);
		notificacioEventHelper.addCallbackEvent(enviament.getNotificacio(), event, true);
		throw new Exception(errorMessage);
	}

	@Transactional
	public void marcarEventNoProcessable(@NonNull Long eventId, String errorDescripcio, String longErrorMessage){

		var event = notificacioEventRepository.findById(eventId).orElse(null);
		if (event == null) {
			log.info(String.format("[Callback] Event [Id: %d] a eliminar de la coa d'events no trobat a la base de dades. Error: %s", eventId, errorDescripcio));
			return;
		}
		errorDescripcio = errorDescripcio == null ? "" : errorDescripcio;
		longErrorMessage = longErrorMessage == null ? "" : longErrorMessage;
		var msg = "Error fatal: " + errorDescripcio + "\n" + longErrorMessage;
		event.updateCallbackClient(CallbackEstatEnumDto.ERROR, getEventsIntentsMaxProperty(), msg, getIntentsPeriodeProperty());
		log.info(String.format("[Callback] Event [Id: %d] eliminat de la coa d'events per error fatal. Error: %s", event.getId(), errorDescripcio));
	}

	public boolean isAllEnviamentsEstatFinal(NotificacioEntity notificacio) {

		if (notificacio == null) {
			return true;
		}
		var estatsEnviamentsFinals = true;
		for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
			if (!enviament.isNotificaEstatFinal()) {
				estatsEnviamentsFinals = false;
				break;
			}
		}
		return estatsEnviamentsFinals;
	}

	/** Propietat que assenayala el màxim de reintents. Si la propietat és null llavors no hi ha un màxim. */
	private Integer getEventsIntentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
	}

	public int getIntentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.periode");
	}

}
