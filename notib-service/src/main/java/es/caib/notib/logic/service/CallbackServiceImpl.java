package es.caib.notib.logic.service;

import es.caib.notib.logic.threads.CallbackProcessarPendentsThread;
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
	private NotificacioEventRepository notificacioEventRepository;
    @Autowired
	private CallbackHelper callbackHelper;
    @Autowired
    private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;


	@Override
//	@Transactional(readOnly = true)
	public void processarPendents() {

		var timer = metricsHelper.iniciMetrica();
		try {
			if (!isTasquesActivesProperty() || !isCallbackPendentsActiu()) {
				log.info("[Callback] Enviament callbacks deshabilitat. ");
			}
			log.info("[Callback] Cercant notificacions pendents d'enviar al client");
			int maxPendents = getEventsProcessarMaxProperty();
			var page = PageRequest.of(0, maxPendents);
			var pendentsIds = notificacioEventRepository.findEventsAmbCallbackPendent(page);
			if (pendentsIds.isEmpty()) {
				log.info("[Callback] No hi ha notificacions pendents d'enviar. ");
				return;
			}
			log.info("[Callback] Inici de les notificacions pendents cap a les aplicacions.");
			int errors = 0;
			var executorService = Executors.newFixedThreadPool(pendentsIds.size());
			Map<Long, Future<Boolean>> futurs = new HashMap<>();
			CallbackProcessarPendentsThread thread;
			Future<Boolean> futur;
			Map<Long, Long> nots = new HashMap<>();
			var multiThread = Boolean.parseBoolean(configHelper.getConfig(PropertiesConstants.SCHEDULLED_MULTITHREAD));
			Long notificacioId;
			for (var e: pendentsIds) {
				// TODO: Això necessita la transacció, però la transacció espatlla la resta!
				notificacioId = notificacioEventRepository.findNotificacioIdByEventId(e.getId());
				if (nots.get(notificacioId) != null) {
					continue;
				}
				log.info("[Callback] >>> Enviant avís a aplicació client de canvi d'estat de l'event amb identificador: " + e.getId());
				nots.put(notificacioId, e.getId());
				try {
					if (multiThread) {
						thread = new CallbackProcessarPendentsThread(e.getId(), callbackHelper);
						futur = executorService.submit(thread);
						futurs.put(e.getId(), futur);
						continue;
					}

					if(!callbackHelper.notifica(e.getId())) {
						errors++;
					}

				} catch (Exception ex) {
					errors++;
					log.error(String.format("[Callback] L'event [Id: %d] ha provocat la següent excepcio:", e.getId()), e);
					callbackHelper.marcarEventNoProcessable(e.getId(), ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			var keys = futurs.keySet();
			boolean err;
			for (var key : keys) {
				try {
					err = futurs.get(key).get();
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
