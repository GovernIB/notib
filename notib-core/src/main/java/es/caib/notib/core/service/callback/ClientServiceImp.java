package es.caib.notib.core.service.callback;

import org.springframework.stereotype.Service;

import es.caib.notib.core.api.ws.callback.ClientService;
import es.caib.notib.core.api.ws.callback.NotificacioCertificacioClient;
import es.caib.notib.core.api.ws.callback.NotificacioEstatClient;

@Service
public class ClientServiceImp implements ClientService {

	@Override
	public void notificaEstat(NotificacioEstatClient estatNotificacio) {
		
		System.out.println(estatNotificacio.toString());	
	}

	@Override
	public void notificaCertificacio(NotificacioCertificacioClient certificacioNotificacio) {
		
		System.out.println(certificacioNotificacio.toString());
	}
	
}