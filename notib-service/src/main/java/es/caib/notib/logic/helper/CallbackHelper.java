package es.caib.notib.logic.helper;

import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.callback.NotificacioCanviClient;
import es.caib.notib.logic.intf.dto.notificacio.NotTableUpdate;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static es.caib.notib.logic.helper.SubsistemesHelper.SubsistemesEnum.CBK;

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

	public static final String NOTIFICACIO_CANVI = "notificaCanvi";
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private NotificacioEnviamentRepository enviamentRepository;
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private RequestsHelper requestsHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private AuditHelper auditHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;

	private boolean isInterficieWeb(NotificacioEntity not) {
		return not.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB;
	}

	@Transactional
	public void crearCallback(NotificacioEntity not, NotificacioEnviamentEntity env, boolean isError, String errorDesc) {

		try {
			if (isInterficieWeb(not)) {
				return;
			}
			log.debug("[CALLBACK_CLIENT] Afegint callback per l'enviament " + env.getId());
			var c = callbackRepository.findByEnviamentId(env.getId());
			var usuari = env.getCreatedBy().orElse(env.getNotificacio().getCreatedBy().orElseThrow());
			if (c == null) {
				c = CallbackEntity.builder().usuariCodi(usuari.getCodi()).notificacioId(not.getId()).enviamentId(env.getId()).build();
			}
			c.setData(new Date());
			c.setDataCreacio(new Date());
			c.setError(isError);
			c.setErrorDesc(errorDesc);
			c.setEstat(CallbackEstatEnumDto.PENDENT);
			callbackRepository.save(c);
		} catch (NoSuchElementException ex) {
			log.error("L'enviament " + env.getId() + " i la notificacio " + env.getNotificacio().getId() + " no tenen assignat el createdBy", ex);
		} catch (Exception ex) {
			log.error("Error creant el callback per l'enviamnet " + env.getId());
		}
	}

	@Transactional
	public void reactivarCallback(NotificacioEnviamentEntity env) {

		var c = updateCallback(env, false, null);
		if (c == null) {
			return;
		}
		c.setIntents(0);
	}

	@Transactional
	public CallbackEntity updateCallback(NotificacioEnviamentEntity env, boolean isError, String errorDesc) {

		if (isInterficieWeb(env.getNotificacio())) {
			return null;
		}
		var callback = callbackRepository.findByEnviamentId(env.getId());
		if (callback == null) {
			var usuari = env.getCreatedBy().orElse(env.getNotificacio().getCreatedBy().orElse(null));
			var codi = "";
			if (usuari == null) {
				log.error("[CALLBACK] Error usuari null per enviament " + env.getId() + "  i null a la notificacio " + env.getNotificacio().getId());
				return null;
			}
			callback = CallbackEntity.builder().usuariCodi(codi).notificacioId(env.getNotificacio().getId()).enviamentId(env.getId()).build();
		}
		callback.setData(new Date());
		callback.setEstat(CallbackEstatEnumDto.PENDENT);
		return callback;
	}

	@Transactional
	public void updateCallbacks(NotificacioEntity not, boolean isError, String errorDesc) {

		var enviaments = not.getEnviaments();
		CallbackEntity callback;
		List<CallbackEntity> callbacks = new ArrayList<>();
		for(var env : enviaments) {
			callback = updateCallback(env, isError, errorDesc);
			if (callback == null) {
				continue;
			}
			callbacks.add(callback);
		}
		callbackRepository.saveAll(callbacks);
	}

	@Transactional (rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
	public boolean notifica(@NonNull Long enviamentId) throws Exception {

		var env = enviamentRepository.findById(enviamentId).orElse(null);
		if (env == null) {
			return false;
		}
		var notificacioProcessada = notifica(env);
		return notificacioProcessada == null || notificacioProcessada.isErrorLastCallback();
	}

	@Transactional (rollbackFor = RuntimeException.class)
	public NotificacioEntity notifica(@NonNull NotificacioEnviamentEntity env) throws Exception {

		var callback = callbackRepository.findByEnviamentId(env.getId());
		var notificacio = env.getNotificacio();
		var info = new IntegracioInfo(IntegracioCodi.CALLBACK, "Enviament d'avís de canvi d'estat", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'enviament", String.valueOf(env.getId())),
				new AccioParam("Identificador de la notificació", String.valueOf(notificacio.getId())));
		info.setNotificacioId(env.getNotificacio().getId());
		if (callback == null) {
			integracioHelper.addAccioError(info, "Error enviant avis de canvi d'estat. No existeix un callback per l'enviament " + env.getId());
			return notificacio;
		}
		log.trace("[Callback] Consultant aplicacio de l'event. ");
		int intents = callback.getIntents() + 1;
		var aplicacio = getAplicacio(callback, env);
		if (!aplicacio.isActiva()) {
			var start = System.nanoTime();
			var errorDescripcio = "Error notificant canvis al client: No està activa la aplicació " + aplicacio.getCallbackUrl() ;
			var maxIntents = this.getEventsIntentsMaxProperty();
			var errorMaxReintents = intents >= maxIntents;
			notificacio.updateLastCallbackError(true);
			notificacioEventHelper.addCallbackEnviamentEvent(env, true, errorDescripcio, errorMaxReintents);
			callback.update(CallbackEstatEnumDto.ERROR, intents, errorDescripcio, getIntentsPeriodeProperty());
			log.info(String.format("[Callback] Fi intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, callback.getId(), notificacio.getId()));
			long elapsedTime = System.nanoTime() - start;
			log.info("addCallbackEvent: "  + elapsedTime);
			auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "CallbackHelper.notifica");
			// Marcar per actualitzar
			notificacioTableHelper.actualitzar(NotTableUpdate.builder().id(notificacio.getId()).build());
			return notificacio;
		}
		info.addParam("Codi aplicació", aplicacio.getUsuariCodi());
		info.addParam("Callback id", callback.getId() + "");
		info.addParam("Callback URL", aplicacio.getCallbackUrl());
		info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
		info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
		log.info(String.format("[Callback] Intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, callback.getId(), notificacio.getId()));
		var isError = false;
		String errorDescripcio = null;
		var errorMaxReintents = false;
		try {
			var start = System.nanoTime();
			notificaCanvi(env, aplicacio);
			var elapsedTime = System.nanoTime() - start;
			log.info("notificaCanvi "  + elapsedTime);

			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.APLICACIO && isAllEnviamentsEstatFinal(notificacio)) {
				log.info("[Callback] Marcant notificacio com processada per ser usuari aplicacio...");
				start = System.nanoTime();
				notificacio.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
				notificacio.updateEstatProcessatDate(new Date());
				notificacio.updateMotiu("Notificacio processada de forma automatica. Estat final: " + env.getNotificaEstat());
				notificacioTableHelper.actualitzar(NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.PROCESSADA).estatProcessatDate(new Date()).build());
				elapsedTime = System.nanoTime() - start;
				log.info("marca processada: "  + elapsedTime);
			}

			// Marca l'event com a notificat
			start = System.nanoTime();
			callback.update(CallbackEstatEnumDto.NOTIFICAT, intents, null, getIntentsPeriodeProperty());
			notificacio.updateLastCallbackError(false);
			integracioHelper.addAccioOk(info);
			log.info(String.format("[Callback] Enviament del callback [Id: %d] de la notificacio [Id: %d] exitos", callback.getId(), notificacio.getId()));
			elapsedTime = System.nanoTime() - start;
			log.info("marcar com a notificat: "  + elapsedTime);
		} catch (Exception ex) {
			var start = System.nanoTime();
			isError = true;
			log.info(String.format("[Callback] Excepcio notificant el callback [Id: %d]: %s", callback.getId(), ex));
			// Marca un error a l'event
			var maxIntents = this.getEventsIntentsMaxProperty();
			errorMaxReintents = intents >= maxIntents;
			var estatNou = maxIntents == null || intents < maxIntents ? CallbackEstatEnumDto.PENDENT : CallbackEstatEnumDto.ERROR;
			log.info(String.format("[Callback] Actualitzam la base de dades amb l'error de l'event [Id: %d]", callback.getId()));
			errorDescripcio = "Error notificant canvis al client: " + ex.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex);
			callback.update(estatNou, intents, "Error notificant canvis al client: " + ex.getMessage(), getIntentsPeriodeProperty());
			notificacio.updateLastCallbackError(true);
			integracioHelper.addAccioError(info, "Error enviant l'avis de canvi d'estat", ex);
			var elapsedTime = System.nanoTime() - start;
			log.info("excepcio: "  + elapsedTime);
		}
		var start = System.nanoTime();
		notificacioEventHelper.addCallbackEnviamentEvent(env, isError, errorDescripcio, errorMaxReintents);
		log.info(String.format("[Callback] Fi intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, callback.getId(), notificacio.getId()));
		long elapsedTime = System.nanoTime() - start;
		log.info("addCallbackEvent: "  + elapsedTime);
		auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "CallbackHelper.notifica");
		// Marcar per actualitzar
		notificacioTableHelper.actualitzar(NotTableUpdate.builder().id(notificacio.getId()).build());
		return notificacio;
	}

	public String notificaCanvi(@NonNull NotificacioEnviamentEntity enviament, @NonNull AplicacioEntity aplicacio) throws Exception {

		long start = System.currentTimeMillis();
		try {
			var notificacioCanvi = new NotificacioCanviClient(enviament.getNotificacio().getReferencia(), enviament.getNotificaReferencia());
			// Completa la URL al mètode
			var urlBase = aplicacio.getCallbackUrl();
			var headerCsrf = aplicacio.isHeaderCsrf();
			var urlCallback = urlBase + (urlBase.endsWith("/") ? "" : "/") + NOTIFICACIO_CANVI;
			var response = requestsHelper.callbackAplicacioNotificaCanvi(urlCallback, notificacioCanvi, headerCsrf);
			SubsistemesHelper.addSuccessOperation(CBK, System.currentTimeMillis() - start);
			// Comprova que la resposta sigui 200 OK
			if (ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
				log.error("Error al enviar callback per l'enviament " + enviament.getUuid() + " a la url " + urlBase);
				throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
			}
			return response.getEntity(String.class);
		} catch (Exception e) {
			SubsistemesHelper.addErrorOperation(CBK, System.currentTimeMillis() - start);
			throw e;
		}
	}

	private AplicacioEntity getAplicacio(CallbackEntity callback, @NonNull NotificacioEnviamentEntity enviament) throws Exception {

		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		var usuari = enviament.getCreatedBy().orElse(enviament.getNotificacio().getCreatedBy().orElse(null));
		String errorMessage = null;
		AplicacioEntity aplicacio = null;
		var activa = true;
		if (usuari == null) {
			errorMessage ="L'enviament i la notifacio no tenen assignat cap createdBy";
		} else {
			aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
			if (aplicacio == null) {
				errorMessage = String.format("No s'ha trobat l'aplicació: codi usuari: %s, EntitatId: %d", usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
			} else if (aplicacio.getCallbackUrl() == null) {
				errorMessage = "La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada";
			} else if (!aplicacio.isActiva()) {
				errorMessage = "La aplicació " + aplicacio.getUsuariCodi() + " no està activa";
				activa = false;
			}
		}
		if (errorMessage != null) {
			var info = new IntegracioInfo(IntegracioCodi.CALLBACK, "Enviament d'avís de canvi d'estat", IntegracioAccioTipusEnumDto.ENVIAMENT,
					new AccioParam("Identificador del callback", String.valueOf(callback.getId())),
					new AccioParam("Codi aplicació", aplicacio != null ? aplicacio.getUsuariCodi() : ""),
					new AccioParam("Identificador de la notificacio", String.valueOf(enviament.getNotificacio().getId()))
			);
			info.setNotificacioId(enviament.getNotificacio().getId());
			var msg = "Error notificant el callback al client: " + errorMessage;
			info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");

			var intents = callback.getIntents() + 1;
			var maxIntents = this.getEventsIntentsMaxProperty();
			var estatNou = maxIntents == null || intents < maxIntents ? CallbackEstatEnumDto.PENDENT : CallbackEstatEnumDto.ERROR;
			var errorMaxReintents = intents >= maxIntents;
			callback.update(estatNou, intents, msg, getIntentsPeriodeProperty());
			integracioHelper.addAccioError(info, msg);
			notificacioEventHelper.addCallbackEnviamentEvent(enviament, true, msg, errorMaxReintents);
			if (activa) {
				throw new Exception(errorMessage);
			}
		}
		return aplicacio;
	}

	@Transactional
	public void marcarEventNoProcessable(@NonNull Long enviamentId, String errorDescripcio, String longErrorMessage){

		var enviament = enviamentRepository.findById(enviamentId).orElse(null);
		if (enviament == null) {
			log.info(String.format("[Callback] Enviament [Id: %d] no trobat a la base de dades. Error: %s", enviamentId, errorDescripcio));
			return;
		}
		errorDescripcio = errorDescripcio == null ? "" : errorDescripcio;
		longErrorMessage = longErrorMessage == null ? "" : longErrorMessage;
		var callback = callbackRepository.findByEnviamentId(enviamentId);
		callback.update(CallbackEstatEnumDto.ERROR, getEventsIntentsMaxProperty(), "Error fatal: " + errorDescripcio + "\n" + longErrorMessage, getIntentsPeriodeProperty());
		log.info(String.format("[Callback] Enviament [Id: %d] eliminat de la coa de callback per error fatal. Error: %s", enviamentId, errorDescripcio));
	}

	public boolean isAllEnviamentsEstatFinal(NotificacioEntity notificacio) {

		if (notificacio == null) {
			return true;
		}
		for (var enviament: notificacio.getEnviaments()) {
			if (!enviament.isNotificaEstatFinal()) {
				return false;
			}
		}
		return true;
	}

	/** Propietat que assenayala el màxim de reintents. Si la propietat és null llavors no hi ha un màxim. */
	private Integer getEventsIntentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
	}

	public int getIntentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.periode");
	}

}
