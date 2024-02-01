package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.threads.CallbackProcessarPendentsThread;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Classe que implementa el servei de callback cap a les aplicacions clients de Notib
 * que estan configurades per rebre actualitzacions dels events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class CallbackServiceImpl implements CallbackService {

 	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
    @Autowired
	private CallbackHelper callbackHelper;
    @Autowired
    private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;

	@Override
	public void processarPendents() {

		var timer = metricsHelper.iniciMetrica();
		try {
			if (!isTasquesActivesProperty() || !isCallbackPendentsActiu()) {
				log.info("[Callback] Enviament callbacks deshabilitat. ");
			}
			log.info("[Callback] Cercant notificacions pendents d'enviar al client");
			var maxPendents = getEventsProcessarMaxProperty();
			var page = PageRequest.of(0, maxPendents);
			var pendents = callbackRepository.findEnviamentIdPendents(page);
			if (pendents.isEmpty()) {
				log.info("[Callback] No hi ha notificacions pendents d'enviar. ");
				return;
			}
			log.info("[Callback] Inici de les notificacions pendents cap a les aplicacions.");
			var errors = 0;
			var executorService = Executors.newFixedThreadPool(pendents.size());
			Map<Long, Future<Boolean>> futurs = new HashMap<>();
			CallbackProcessarPendentsThread thread;
			Future<Boolean> futur;
			var multiThread = Boolean.parseBoolean(configHelper.getConfig(PropertiesConstants.SCHEDULLED_MULTITHREAD));
			for (var id: pendents) {
				log.info("[Callback] >>> Enviant avís a aplicació client de canvi d'estat de l'event amb identificador: " + id);
				try {
					if (multiThread) {
						thread = new CallbackProcessarPendentsThread(id, callbackHelper);
						futur = executorService.submit(thread);
						futurs.put(id, futur);
						continue;
					}
					if(!callbackHelper.notifica(id)) {
						errors++;
					}
				} catch (Exception ex) {
					errors++;
					log.error("[Callback] L'enviament [Id: " + id + "] ha provocat la següent excepcio:", ex);
					callbackHelper.marcarEventNoProcessable(id, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			var keys = futurs.keySet();
			Boolean err;
			for (var key : keys) {
				try {
					err = futurs.get(key).get();
					errors = Boolean.TRUE.equals(err) ? errors + 1 : errors;
				} catch (Exception ex) {
					errors++;
					log.error("[Callback] L'enviament [Id: " + key + "] ha provocat la següent excepcio:", ex);
					callbackHelper.marcarEventNoProcessable(key, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			log.info("[Callback] Fi de les notificacions pendents cap a les aplicacions: " + pendents.size() + ", " + errors + " errors");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public boolean reintentarCallback(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("Notificant canvi al client...");
			// Recupera l'event
			var isError = false;
			var not = notificacioRepository.findById(notificacioId).orElseThrow();
			NotificacioEntity notificacio;
			for (var env : not.getEnviaments()) {
				try {
					notificacio = callbackHelper.notifica(env);
					isError = (notificacio != null && !notificacio.isErrorLastCallback());
				} catch (Exception e) {
					log.error(String.format("[Callback]L'enviament [Id: %d] ha provocat la següent excepcio:", env.getId()), e);
					isError = true;
				}
			}
			return isError;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean findByNotificacio(Long notId) {
		return false;
	}

	private boolean isTasquesActivesProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasques.actives");
	}

	private boolean isCallbackPendentsActiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasca.callback.pendents.actiu");
	}

	private int getEventsProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.processar.max");
	}

}
