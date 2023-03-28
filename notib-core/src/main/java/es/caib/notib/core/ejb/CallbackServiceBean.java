package es.caib.notib.core.ejb;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.service.CallbackService;

/**
 * Implementació de CallbackService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.

 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class CallbackServiceBean implements CallbackService {
	
	@Autowired
	CallbackService delegate;

	@Override
	public void processarPendents() {
		delegate.processarPendents();
	}

	@Override
	public boolean reintentarCallback(Long notId) {
		return false;
	}

	@Override
	public boolean findByNotificacio(Long notId) {
		return false;
	}

}
