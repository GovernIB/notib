package es.caib.notib.core.helper;

import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotTableUpdate;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.service.AuditService;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.CallbackEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.CallbackRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
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
	private NotificacioEnviamentRepository enviamentRepository;
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private RequestsHelper requestsHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;

	@Transactional
	public void crearCallback(NotificacioEntity not, NotificacioEnviamentEntity env, boolean isError, String errorDesc) {

		try {
			if (not.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB) {
				return;
			}
			log.debug("[CALLBACK_CLIENT] Afegint callback per l'enviament " + env.getId());
			CallbackEntity c = callbackRepository.findByEnviamentId(env.getId());
			if (c == null) {
				c = CallbackEntity.builder().usuariCodi(env.getCreatedBy().getCodi()).notificacioId(not.getId()).enviamentId(env.getId()).build();
			}
			c.setData(new Date());
			c.setError(isError);
			c.setErrorDesc(errorDesc);
			c.setEstat(CallbackEstatEnumDto.PENDENT);
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
	public boolean notifica(@NonNull Long enviamentId) throws Exception {

		NotificacioEnviamentEntity env = enviamentRepository.findOne(enviamentId);
		if (env == null) {
			return false;
		}
		NotificacioEntity notificacioProcessada = notifica(env);
		return notificacioProcessada == null || notificacioProcessada.isErrorLastCallback();
	}

	@Transactional (rollbackFor = RuntimeException.class)
	public NotificacioEntity notifica(@NonNull NotificacioEnviamentEntity env) throws Exception{

		CallbackEntity callback = callbackRepository.findByEnviamentId(env.getId());
		NotificacioEntity notificacio = env.getNotificacio();
		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_CLIENT, String.format("Enviament d'avís de canvi d'estat"), IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'enviament", String.valueOf(env.getId())),
				new AccioParam("Identificador de la notificació", String.valueOf(notificacio.getId())));

		if (callback == null) {
			integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat. No existeix un callback per l'enviament " + env.getId());
			return notificacio;
		}
		log.trace("[Callback] Consultant aplicació de l'event. ");
		AplicacioEntity aplicacio = getAplicacio(callback, env);
		info.addParam("Codi aplicació", aplicacio.getUsuariCodi());
		info.addParam("Callback id", callback.getId() + "");
		info.addParam("Callback URL", aplicacio.getCallbackUrl());
		info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
		info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
		int intents = callback.getIntents() + 1;
		log.info(String.format("[Callback] Intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, callback.getId(), notificacio.getId()));
		boolean isError = false;
		String errorDescripcio = null;
		try {

			// Avisa al client que hi ha hagut una modificació a l'enviament
//			NotificacioEnviamentEntity enviament = event.getEnviament();
			long start = System.nanoTime();
			notificaCanvi(env, aplicacio.getCallbackUrl());
			long elapsedTime = System.nanoTime() - start;
			log.info("notificaCanvi "  + elapsedTime);

			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.APLICACIO && isAllEnviamentsEstatFinal(notificacio)) {
				log.info("[Callback] Marcant notificació com processada per ser usuari aplicació...");
				start = System.nanoTime();
				notificacio.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
				notificacio.updateEstatProcessatDate(new Date());
				notificacio.updateMotiu("Notificació processada de forma automàtica. Estat final: " + env.getNotificaEstat());
				notificacioTableHelper.actualitzar(NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.PROCESSADA).estatProcessatDate(new Date()).build());
				elapsedTime = System.nanoTime() - start;
				log.info("marca processada: "  + elapsedTime);
			}

			// TODO: Això no hauria d'estar abans de la crida a notificaCanvi?
			if (!aplicacio.isActiva()) {
				// No s'ha d'enviar. El callback està inactiu
				log.info(String.format("[Callback] No s'ha enviat el callback [Id: %d], el callback està inactiu.", callback.getId()));
				start = System.nanoTime();
				callback.update(CallbackEstatEnumDto.PROCESSAT, intents, null, getIntentsPeriodeProperty());
				notificacio.updateLastCallbackError(false);
				elapsedTime = System.nanoTime() - start;
				log.info("el callback esta inactiu: "  + elapsedTime);
				return notificacio;
			}

			// Marca l'event com a notificat
			start = System.nanoTime();
			callback.update(CallbackEstatEnumDto.NOTIFICAT, intents, null, getIntentsPeriodeProperty());
			notificacio.updateLastCallbackError(false);
			integracioHelper.addAccioOk(info);
			log.info(String.format("[Callback] Enviament del callback [Id: %d] de la notificacio [Id: %d] exitós", callback.getId(), notificacio.getId()));
			elapsedTime = System.nanoTime() - start;
			log.info("marcar com a notificat: "  + elapsedTime);
		} catch (Exception ex) {
			long start = System.nanoTime();
			isError = true;
			log.info(String.format("[Callback] Excepció notificant el callback [Id: %d]: %s", callback.getId(), ex.getMessage()));
			ex.printStackTrace();
			// Marca un error a l'event
			Integer maxIntents = this.getEventsIntentsMaxProperty();
			CallbackEstatEnumDto estatNou = maxIntents == null || intents < maxIntents ? CallbackEstatEnumDto.PENDENT : CallbackEstatEnumDto.ERROR;
			log.info(String.format("[Callback] Actualitzam la base de dades amb l'error de l'event [Id: %d]", callback.getId()));
			errorDescripcio = "Error notificant canvis al client: " + ex.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex);
			callback.update(estatNou, intents, "Error notificant canvis al client: " + ex.getMessage(), getIntentsPeriodeProperty());
			notificacio.updateLastCallbackError(true);
			integracioHelper.addAccioError(info, "Error enviant l'avís de canvi d'estat", ex);
			long elapsedTime = System.nanoTime() - start;
			log.info("excepcio: "  + elapsedTime);
		}
		long start = System.nanoTime();
		notificacioEventHelper.addCallbackEnviamentEvent(env, isError, errorDescripcio);
		log.info(String.format("[Callback] Fi intent %d de l'enviament del callback [Id: %d] de la notificacio [Id: %d]", intents, callback.getId(), notificacio.getId()));
		long elapsedTime = System.nanoTime() - start;
		log.info("addCallbackEvent: "  + elapsedTime);

		notificacioHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "CallbackHelper.notifica");
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

	private AplicacioEntity getAplicacio(CallbackEntity callback, @NonNull NotificacioEnviamentEntity enviament) throws Exception {

		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		UsuariEntity usuari = enviament.getCreatedBy();
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		String errorMessage = null;
		if (aplicacio == null) {
			errorMessage = String.format("No s'ha trobat l'aplicació: codi usuari: %s, EntitatId: %d", usuari.getCodi(), enviament.getNotificacio().getEntitat().getId());
		} else if (aplicacio.getCallbackUrl() == null) {
			errorMessage = "La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada";
		}
		if (errorMessage != null) {
			IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_CLIENT, "Enviament d'avís de canvi d'estat", IntegracioAccioTipusEnumDto.ENVIAMENT,
					new AccioParam("Identificador del callback", String.valueOf(callback.getId())),
					new AccioParam("Codi aplicació", aplicacio != null ? aplicacio.getUsuariCodi() : ""),
					new AccioParam("Identificador de la notificacio", String.valueOf(enviament.getNotificacio().getId()))
			);
			String msg = "Error notificant el callback al client: " + errorMessage;
			info.setAplicacio(aplicacio != null ? aplicacio.getUsuariCodi() : "Sense aplicació");
			callback.update(CallbackEstatEnumDto.ERROR, getEventsIntentsMaxProperty(), msg, getIntentsPeriodeProperty());
			integracioHelper.addAccioError(info, msg);
//			notificacioEventHelper.addCallbackEvent(enviament.getNotificacio(), event, true);
			throw new Exception(errorMessage);
		}
		return aplicacio;
	}

	// TODO CALLBACK:
	@Transactional
	public void marcarEventNoProcessable(@NonNull Long enviamentId,
										 String errorDescripcio,
										 String longErrorMessage){
		NotificacioEnviamentEntity enviament = enviamentRepository.findOne(enviamentId);
		if (enviament == null) {
			log.info(String.format("[Callback] Enviament [Id: %d] no trobat a la base de dades. Error: %s", enviamentId, errorDescripcio));
			return;
		}
		errorDescripcio = errorDescripcio == null ? "" : errorDescripcio;
		longErrorMessage = longErrorMessage == null ? "" : longErrorMessage;

		CallbackEntity callback = callbackRepository.findByEnviamentId(enviamentId);
		callback.update(CallbackEstatEnumDto.ERROR, getEventsIntentsMaxProperty(), "Error fatal: " + errorDescripcio + "\n" + longErrorMessage, getIntentsPeriodeProperty());
		log.info(String.format("[Callback] Enviament [Id: %d] eliminat de la coa de callback per error fatal. Error: %s", enviamentId, errorDescripcio));
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
