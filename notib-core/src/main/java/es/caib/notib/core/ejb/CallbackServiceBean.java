package es.caib.notib.core.ejb;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.core.api.ws.callback.CallbackService;

/**
 * Implementació de CallbackService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.

 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class CallbackServiceBean implements CallbackService {
	
	@Autowired
	CallbackService delegate;

	@Override
	public void notificarEventsPendens() {
		delegate.notificarEventsPendens();
	}
	
	@Override
	public boolean notifica(Long eventId) {
		return delegate.notifica(eventId);
	}
}
