/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.service.GestioDocumentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class GestioDocumentalServiceBean implements GestioDocumentalService {

	@Autowired
	GestioDocumentalService delegate;

	@Override
	@RolesAllowed({"tothom"})
	public String guardarArxiuTemporal(String nom){
		return delegate.guardarArxiuTemporal(nom);
	}

	@Override
	@RolesAllowed({"tothom"})
	public byte[] obtenirArxiuTemporal(String arxiuGestdocId) {
		return delegate.obtenirArxiuTemporal(arxiuGestdocId);
	}
	@Override
	@RolesAllowed({"tothom"})
	public byte[] obtenirArxiuNotificacio(String arxiuGestdocId) {
		return delegate.obtenirArxiuNotificacio(arxiuGestdocId);
	}

}