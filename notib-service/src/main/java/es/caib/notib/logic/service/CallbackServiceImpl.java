package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import es.caib.notib.logic.clases.CallbackProcessarPendentsThread;
import es.caib.notib.logic.helper.PropertiesConstants;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
@Slf4j
@Service
public class CallbackServiceImpl implements CallbackService {

    @Autowired
	private NotificacioEventRepository notificacioEventRepository;
    @Autowired
	private CallbackHelper callbackHelper;
    @Autowired
    private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;


	@Override
	public void processarPendents() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!isTasquesActivesProperty() || !isCallbackPendentsActiu()) {
				log.debug("[Callback] Enviament callbacks deshabilitat. ");
			}
			log.info("[Callback] Cercant notificacions pendents d'enviar al client");
			int maxPendents = getEventsProcessarMaxProperty();
			Pageable page = PageRequest.of(0, maxPendents);
			List<Long> pendentsIds = notificacioEventRepository.findEventsAmbCallbackPendentIds(page);
			if (pendentsIds.isEmpty()) {
				log.info("[Callback] No hi ha notificacions pendents d'enviar. ");
			}
			log.debug("[Callback] Inici de les notificacions pendents cap a les aplicacions.");
			int errors = 0;
			ExecutorService executorService = Executors.newFixedThreadPool(pendentsIds.size());
			Map<Long, Future<Boolean>> futurs = new HashMap<>();
			CallbackProcessarPendentsThread thread;
			Future<Boolean> futur;
			boolean multiThread = Boolean.parseBoolean(configHelper.getConfig(PropertiesConstants.SCHEDULLED_MULTITHREAD));
			for (Long eventId: pendentsIds) {
				log.debug("[Callback] >>> Enviant avís a aplicació client de canvi d'estat de l'event amb identificador: " + eventId);
				try {
					if (multiThread) {
						thread = new CallbackProcessarPendentsThread(eventId, callbackHelper);
						futur = executorService.submit(thread);
						futurs.put(eventId, futur);
						continue;
					}

					if(!callbackHelper.notifica(eventId)) {
						errors++;
					}

				} catch (Exception e) {
					errors++;
					log.error(String.format("[Callback] L'event [Id: %d] ha provocat la següent excepcio:", eventId), e);
					callbackHelper.marcarEventNoProcessable(eventId, e.getMessage(), ExceptionUtils.getStackTrace(e));
				}
			}
			Set<Long> keys = futurs.keySet();
			for (Long key : keys) {
				try {
					Boolean err = futurs.get(key).get();
					errors = err ? errors + 1 : errors;
				} catch (Exception ex) {
					errors++;
					log.error(String.format("[Callback] L'event [Id: %d] ha provocat la següent excepcio:", key), ex);
					callbackHelper.marcarEventNoProcessable(key, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			log.info("[Callback] Fi de les notificacions pendents cap a les aplicacions: " + pendentsIds.size() + ", " + errors + " errors");

		} finally {
			metricsHelper.fiMetrica(timer);
		}
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
