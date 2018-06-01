package es.caib.notib.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.service.CallbackService;
import es.caib.notib.core.helper.CallbackHelper;
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



	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.callback.pendents.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.callback.pendents.retard.inicial}")
	public void processarPendents() {
		logger.debug("Cercant notificacions pendents d'enviar al client");
		int maxPendents = getEventsProcessarMaxProperty(); 
		Pageable page = new PageRequest(
				0,
				maxPendents,
				new Sort(new Order(Direction.ASC, "data")));
		List<Long> pendentsIds = notificacioEventRepository.findEventsPendentsIds(page);
		if (pendentsIds.size() > 0) {
			logger.debug("Inici de les notificacions pendents cap a les aplicacions.");
			int errors = 0;
			for (Long pendentsId: pendentsIds) {
				if (!callbackHelper.notifica(pendentsId)) {
					errors++;
				}
			}
			logger.debug("Fi de les notificacions pendents cap a les aplicacions: " + pendentsIds.size() + ", " + errors + " errors");
		}
	}



	/* Màxim d'events a processar en cada període */
	private int getEventsProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.callback.events.processar.max",
				50);
	}

	private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

}
