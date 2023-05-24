package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.threads.CallbackProcessarPendentsThread;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Classe que implementa el servei de callback cap a les aplicacions clients de Notib
 * que estan configurades per rebre actualitzacions dels events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
//	@Transactional(readOnly = true)
	public void processarPendents() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!isTasquesActivesProperty() || !isCallbackPendentsActiu()) {
				logger.info("[Callback] Enviament callbacks deshabilitat. ");
			}
			logger.info("[Callback] Cercant notificacions pendents d'enviar al client");
			int maxPendents = getEventsProcessarMaxProperty();
			Pageable page = PageRequest.of(0, maxPendents);
			List<Long> pendents = callbackRepository.findEnviamentIdPendents(page);
			if (pendents.isEmpty()) {
				logger.info("[Callback] No hi ha notificacions pendents d'enviar. ");
			}
			logger.info("[Callback] Inici de les notificacions pendents cap a les aplicacions.");
			int errors = 0;
			ExecutorService executorService = Executors.newFixedThreadPool(pendents.size());
			Map<Long, Future<Boolean>> futurs = new HashMap<>();
			CallbackProcessarPendentsThread thread;
			Future<Boolean> futur;
			boolean multiThread = Boolean.parseBoolean(configHelper.getConfig(PropertiesConstants.SCHEDULLED_MULTITHREAD));
			for (Long id: pendents) {
				logger.info("[Callback] >>> Enviant avís a aplicació client de canvi d'estat de l'event amb identificador: " + id);
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
					logger.error(String.format("[Callback] L'enviament [Id: %d] ha provocat la següent excepcio:", id), ex);
					callbackHelper.marcarEventNoProcessable(id, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			Set<Long> keys = futurs.keySet();
			for (Long key : keys) {
				try {
					Boolean err = futurs.get(key).get();
					errors = err ? errors + 1 : errors;
				} catch (Exception ex) {
					errors++;
					logger.error(String.format("[Callback] L'enviament [Id: %d] ha provocat la següent excepcio:", key), ex);
					callbackHelper.marcarEventNoProcessable(key, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			logger.info("[Callback] Fi de les notificacions pendents cap a les aplicacions: " + pendents.size() + ", " + errors + " errors");

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public boolean reintentarCallback(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("Notificant canvi al client...");
			// Recupera l'event
			boolean isError = false;
			NotificacioEntity not = notificacioRepository.findById(notificacioId).orElseThrow();
			for (NotificacioEnviamentEntity env : not.getEnviaments()) {
				try {
					NotificacioEntity notificacio = callbackHelper.notifica(env);
					isError = (notificacio != null && !notificacio.isErrorLastCallback());
				} catch (Exception e) {
					logger.error(String.format("[Callback]L'enviament [Id: %d] ha provocat la següent excepcio:", env.getId()), e);
					e.printStackTrace();
					isError = true;
					// Marcam a l'event que ha causat un error no controlat  i el treiem de la cola
//				callbackHelper.marcarEventNoProcessable(eventId, e.getMessage(), ExceptionUtils.getStackTrace(e));
//					return false;
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

	private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

}
