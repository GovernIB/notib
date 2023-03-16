package es.caib.notib.core.helper;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.CallbackEntity;
import es.caib.notib.core.entity.EnviamentTableEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.CallbackRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.service.ws.NotificacioServiceWsImplV2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private CallbackRepository callbackRepository;
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


	@Transactional
	public void crearCallback(NotificacioEntity not, NotificacioEnviamentEntity env, boolean isError, String errorDesc) {

		try {
			if (not.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB) {
				return;
			}
			log.debug("[CALLBACK_CLIENT] Afegint callback per l'enviament " + env.getId());
			CallbackEntity c = CallbackEntity.builder().usuariCodi(env.getCreatedBy().getCodi()).notificacioId(not.getId()).enviamentId(env.getId())
					.estat(CallbackEstatEnumDto.PENDENT).data(new Date()).error(isError).errorDesc(errorDesc).build();
			callbackRepository.save(c);
		} catch (Exception ex) {
			log.error("Error creant el callback per l'enviamnet " + env.getId());
		}
 	}

	@Transactional
	public CallbackEntity updateCallback(NotificacioEnviamentEntity env, boolean isError, String errorDesc) {

		CallbackEntity callback = callbackRepository.findByEnviamentId(env.getId());
		if (callback == null) {
			callback = CallbackEntity.builder().usuariCodi(env.getCreatedBy().getCodi()).notificacioId(env.getNotificacio().getId()).enviamentId(env.getId()).build();
		}
		callback.setData(new Date());
		callback.setError(isError);
		callback.setErrorDesc(errorDesc);
		callback.setEstat(CallbackEstatEnumDto.PENDENT);
		return callback;
	}

	@Transactional
	public void updateCallbacks(NotificacioEntity not, boolean isError, String errorDesc) {

		Set<NotificacioEnviamentEntity> enviaments = not.getEnviaments();
		CallbackEntity callback;
		List<CallbackEntity> callbacks = new ArrayList<>();
		for(NotificacioEnviamentEntity env : enviaments) {
			callback = updateCallback(env, isError, errorDesc);
			callbacks.add(callback);
		}
		callbackRepository.save(callbacks);
	}

	@Transactional (rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
	public boolean notifica(@NonNull Long eventId) throws Exception {
		NotificacioEventEntity event = notificacioEventRepository.findOne(eventId);

		if (event == null)
			return false;

		NotificacioEntity notificacioProcessada = notifica(event);
		if (notificacioProcessada != null && notificacioProcessada.isErrorLastCallback()) {
			return false;
		}

		return true;
	}

	@Transactional (rollbackFor = RuntimeException.class)
	public NotificacioEntity notifica(@NonNull NotificacioEventEntity event) throws Exception{
		log.trace("[Callback] Consultant aplicació de l'event. ");
		AplicacioEntity aplicacio = getAplicacio(event);

		NotificacioEntity notificacio = event.getNotificacio();
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_CLIENT,
				String.format("Enviament d'avís de canvi d'estat (%s)", aplicacio.getCallbackUrl()),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'event", String.valueOf(event.getId())),
				new AccioParam("Identificador de la notificació", String.valueOf(notificacio.getId())),
				new AccioParam("Codi aplicació", aplicacio.getUsuariCodi()),
				new AccioParam("Callback", aplicacio.getCallbackUrl())
		);
		info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
		info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
//		int intents = event.getCallbackIntents() + 1;
//		log.info(String.format("[Callback] Intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]",
//				intents, event.getId(), notificacio.getId()));
		boolean isError = false;
		NotificacioEnviamentEntity enviament = null;
		String errorDescripcio = null;
		try {

			if (event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT
//					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT
//					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO
//					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE
//					|| event.getTipus() == NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT
//					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR
//					|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR
//					|| event.getTipus() == NotificacioEventTipusEnumDto.CALLBACK_ACTIVAR
//					|| (event.isError() && event.getTipus() == NotificacioEventTipusEnumDto.CALLBACK_CLIENT)
			) {
				// Avisa al client que hi ha hagut una modificació a l'enviament
				enviament = event.getEnviament();
				long start = System.nanoTime();
				notificaCanvi(enviament, aplicacio.getCallbackUrl());
				long elapsedTime = System.nanoTime() - start;
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
//					event.updateCallbackClient(CallbackEstatEnumDto.PROCESSAT, intents, null, getIntentsPeriodeProperty());
					auditNotificacioHelper.updateLastCallbackError(notificacio, false);
					elapsedTime = System.nanoTime() - start;
					log.info("el callback esta inactiu: "  + elapsedTime);
					return notificacio;
				}

				// Marca l'event com a notificat
				start = System.nanoTime();
//				event.updateCallbackClient(CallbackEstatEnumDto.NOTIFICAT, intents, null, getIntentsPeriodeProperty());
				auditNotificacioHelper.updateLastCallbackError(notificacio, false);
				integracioHelper.addAccioOk(info);
				log.info(String.format("[Callback] Enviament del callback [Id: %d] de la notificacio [Id: %d] exitós", event.getId(), notificacio.getId()));
				elapsedTime = System.nanoTime() - start;
				log.info("marcar com a notificat: "  + elapsedTime);
			} else {
				isError = true;
				// És un event pendent de notificar que no és del tipus esperat
				long start = System.nanoTime();
				log.info(String.format("[Callback] No s'ha pogut enviar el callback [Id: %d], el tipus d'event és incorrecte.",event.getId()));
				errorDescripcio = "L'event id=" + event.getId() + " és del tipus " + event.getTipus() + " i no es pot notificar a l'aplicació client.";
//				event.updateCallbackClient(CallbackEstatEnumDto.ERROR, intents, errorDescripcio, getIntentsPeriodeProperty());
				auditNotificacioHelper.updateLastCallbackError(notificacio, true);
				integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat: " + errorDescripcio);
				long elapsedTime = System.nanoTime() - start;
				log.info("event pendent notifica no tipus esperat: "  + elapsedTime);
			}
		} catch (Exception ex) {
			long start = System.nanoTime();
			isError = true;
			log.info(String.format("[Callback] Excepció notificant l'event [Id: %d]: %s", event.getId(), ex.getMessage()));
			ex.printStackTrace();
			// Marca un error a l'event
			Integer maxIntents = this.getEventsIntentsMaxProperty();
//			CallbackEstatEnumDto estatNou = maxIntents == null || intents < maxIntents ? CallbackEstatEnumDto.PENDENT : CallbackEstatEnumDto.ERROR;
			log.info(String.format("[Callback] Actualitzam la base de dades amb l'error de l'event [Id: %d]", event.getId()));
//			event.updateCallbackClient(estatNou, intents, "Error notificant l'event al client: " + ex.getMessage(), getIntentsPeriodeProperty());
			errorDescripcio = "Error notificant l'event al client: " + ex.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex);
			auditNotificacioHelper.updateLastCallbackError(notificacio, true);
			integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat", ex);
			long elapsedTime = System.nanoTime() - start;
			log.info("excepcio: "  + elapsedTime);
		}
		long start = System.nanoTime();
		if (enviament != null)
			notificacioEventHelper.addCallbackEnviamentEvent(enviament, isError, errorDescripcio);
//		notificacioEventHelper.addCallbackEvent(notificacio, event, isError);
//		log.info(String.format("[Callback] Fi intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, event.getId(), notificacio.getId()));
		long elapsedTime = System.nanoTime() - start;
		log.info("addCallbackEvent: "  + elapsedTime);
		return notificacio;
	}

	public String notificaCanvi(@NonNull NotificacioEnviamentEntity enviament, @NonNull String urlBase) throws Exception {

		NotificacioCanviClient notificacioCanvi = NotificacioServiceWsImplV2.isValidUUID(enviament.getNotificaReferencia()) ?
				new NotificacioCanviClient(enviament.getNotificacio().getReferencia(), enviament.getNotificaReferencia()) :
				new NotificacioCanviClient(notificaHelper.xifrarId(enviament.getNotificacio().getId()), notificaHelper.xifrarId(enviament.getId()));

		// Completa la URL al mètode
		String urlCallback = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CANVI;
		ClientResponse response = requestsHelper.callbackAplicacioNotificaCanvi(urlCallback, notificacioCanvi);
		
		// Comprova que la resposta sigui 200 OK
		if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
		}

		return response.getEntity(String.class);
	}

	private AplicacioEntity getAplicacio(@NonNull NotificacioEventEntity event) throws Exception {
		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		NotificacioEnviamentEntity enviament = event.getEnviament();
		UsuariEntity usuari = enviament.getCreatedBy();

		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		String errorMessage = null;
		if (aplicacio == null)
			errorMessage = String.format("No s'ha trobat l'aplicació: codi usuari: %s, EntitatId: %d", usuari.getCodi(),
					enviament.getNotificacio().getEntitat().getId());
		else if (aplicacio.getCallbackUrl() == null)
			errorMessage = "La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada";

		if (errorMessage != null) {

			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT,
					"Enviament d'avís de canvi d'estat",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					new AccioParam("Identificador de l'event", String.valueOf(event.getId())),
					new AccioParam("Codi aplicació", aplicacio != null ? aplicacio.getUsuariCodi() : ""),
					new AccioParam("Identificador de la notificacio", String.valueOf(enviament.getNotificacio().getId()))
			);
			info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
//			event.updateCallbackClient(
//					CallbackEstatEnumDto.ERROR,
//					getEventsIntentsMaxProperty(),
//					"Error notificant l'event al client: " + errorMessage,
//					getIntentsPeriodeProperty());


			integracioHelper.addAccioError(info, "Error consultant l'aplicació: " + errorMessage);
//			notificacioEventHelper.addCallbackEvent(enviament.getNotificacio(), event, true);
			notificacioEventHelper.addCallbackEnviamentEvent(enviament, true, errorMessage);

			throw new Exception(errorMessage);
		}
		return aplicacio;
	}

	@Transactional
	public void marcarEventNoProcessable(@NonNull Long eventId,
										 String errorDescripcio,
										 String longErrorMessage){
		NotificacioEventEntity event = notificacioEventRepository.findOne(eventId);
		if (event == null) {
			log.info(String.format("[Callback] Event [Id: %d] a eliminar de la coa d'events no trobat a la base de dades. " +
					"Error: %s", eventId, errorDescripcio));
			return;
		}
		errorDescripcio = errorDescripcio == null ? "" : errorDescripcio;
		longErrorMessage = longErrorMessage == null ? "" : longErrorMessage;
//		event.updateCallbackClient(
//				CallbackEstatEnumDto.ERROR,
//				getEventsIntentsMaxProperty(),
//				"Error fatal: " + errorDescripcio + "\n" + longErrorMessage,
//				getIntentsPeriodeProperty());
		log.info(String.format("[Callback] Event [Id: %d] eliminat de la coa d'events per error fatal. Error: %s", event.getId(), errorDescripcio));
	}

	public boolean isAllEnviamentsEstatFinal(NotificacioEntity notificacio) {
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
		return configHelper.getAsInt("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
	}

	public int getIntentsPeriodeProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.callback.pendents.periode");
	}

}
