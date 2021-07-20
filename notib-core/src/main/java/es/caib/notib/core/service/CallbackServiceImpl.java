package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.service.CallbackService;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.repository.NotificacioEventRepository;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Classe que implementa el servei de callback cap a les aplicacions clients de Notib
 * que estan configurades per rebre actualitzacions dels events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.callback.pendents.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.callback.pendents.retard.inicial}")
	public void processarPendents() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (isTasquesActivesProperty() && isCallbackPendentsActiu()) {
				logger.info("[Callback] Cercant notificacions pendents d'enviar al client");
				int maxPendents = getEventsProcessarMaxProperty(); 
				Pageable page = new PageRequest(
						0,
						maxPendents);
				List<Long> pendentsIds = notificacioEventRepository.findEventsAmbCallbackPendentIds(page);
				if (pendentsIds.size() > 0) {
					logger.debug("[Callback] Inici de les notificacions pendents cap a les aplicacions.");
					int errors = 0;
					for (Long eventId: pendentsIds) {
						logger.debug("[Callback] >>> Enviant avís a aplicació client de canvi d'estat de l'event amb identificador: " + eventId);
						try {
							if(!callbackHelper.notifica(eventId)) {
								errors++;
							}
						} catch (Exception e) {
							errors++;
							logger.error(String.format("[Callback] L'event [Id: %d] ha provocat la següent excepcio:", eventId), e);
							callbackHelper.marcarEventNoProcessable(eventId,
									e.getMessage(),
									ExceptionUtils.getStackTrace(e));
						}
					}
					logger.info("[Callback] Fi de les notificacions pendents cap a les aplicacions: " + pendentsIds.size() + ", " + errors + " errors");
				} else {
					logger.info("[Callback] No hi ha notificacions pendents d'enviar. ");
				}
			} else {
				logger.debug("[Callback] Enviament callbacks deshabilitat. ");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}



	private boolean isTasquesActivesProperty() {
		return configHelper.getAsBoolean("es.caib.notib.tasques.actives");
	}
	private boolean isCallbackPendentsActiu() {
		return configHelper.getAsBoolean("es.caib.notib.tasca.callback.pendents.actiu");
	}
	private int getEventsProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.callback.pendents.processar.max");
	}

	private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

}
