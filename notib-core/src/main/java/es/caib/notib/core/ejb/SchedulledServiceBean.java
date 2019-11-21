/**
 * 
 */
package es.caib.notib.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.service.SchedulledService;

/**
 * Implementació de Schedulled Service com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class SchedulledServiceBean implements SchedulledService {

	@Autowired
	SchedulledService delegate;
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public void notificaEnviamentsRegistrats() {
		delegate.notificaEnviamentsRegistrats();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public void registrarEnviamentsPendents() {
		delegate.registrarEnviamentsPendents();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public void enviamentRefrescarEstatPendents() {
		delegate.enviamentRefrescarEstatPendents();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public void enviamentRefrescarEstatEnviatSir() {
		delegate.enviamentRefrescarEstatEnviatSir();
	}

}
