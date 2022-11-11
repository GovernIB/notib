/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.service.PermisosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PermisosServiceBean implements PermisosService {

	@Autowired
	PermisosService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisNotificacio(Long entitatId, String usuariCodi) {
		return delegate.hasPermisNotificacio(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisComunicacio(Long entitatId, String usuariCodi) {
		return delegate.hasPermisComunicacio(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi) {
		return delegate.hasPermisComunicacioSir(entitatId, usuariCodi);
	}

}
