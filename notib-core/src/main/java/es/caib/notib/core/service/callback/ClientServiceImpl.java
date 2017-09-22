package es.caib.notib.core.service.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.ws.callback.ClientService;
import es.caib.notib.core.api.ws.callback.NotificacioCertificacioClient;
import es.caib.notib.core.api.ws.callback.NotificacioEstatClient;

@Service
public class ClientServiceImpl implements ClientService {

	@Override
	public void notificaEstat(NotificacioEstatClient estatNotificacio) {
		
		logger.debug(estatNotificacio.toString());	
	}

	@Override
	public void notificaCertificacio(NotificacioCertificacioClient certificacioNotificacio) {
		
		logger.debug(certificacioNotificacio.toString());
	}	

	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
}