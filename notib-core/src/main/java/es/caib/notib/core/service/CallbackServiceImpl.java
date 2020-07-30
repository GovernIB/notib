package es.caib.notib.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.service.CallbackService;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.NotificacioEventRepository;

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
    
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.callback.pendents.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.callback.pendents.retard.inicial}")
	public void processarPendents() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (isTasquesActivesProperty() && isCallbackPendentsActiu()) {
				logger.debug("Cercant notificacions pendents d'enviar al client");
				int maxPendents = getEventsProcessarMaxProperty(); 
				Pageable page = new PageRequest(
						0,
						maxPendents);
				List<Long> pendentsIds = notificacioEventRepository.findEventsPendentsIds(page);
				if (pendentsIds.size() > 0) {
					logger.debug("Inici de les notificacions pendents cap a les aplicacions.");
					int errors = 0;
					for (Long pendentsId: pendentsIds) {
						logger.debug(">>> Enviant avís a aplicació client de canvi d'estat de la notificació amb identificador: " + pendentsId);
						if (!callbackHelper.notifica(pendentsId)) {
							errors++;
						}
					}
					logger.debug("Fi de les notificacions pendents cap a les aplicacions: " + pendentsIds.size() + ", " + errors + " errors");
				}
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}



	private boolean isTasquesActivesProperty() {
		String actives = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasques.actives");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private boolean isCallbackPendentsActiu() {
		String actives = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasca.callback.pendents.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getEventsProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.callback.pendents.processar.max",
				50);
	}

	private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

}
