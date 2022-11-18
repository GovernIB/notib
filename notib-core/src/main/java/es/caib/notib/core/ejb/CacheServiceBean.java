/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.CacheDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implementació de CacheService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class CacheServiceBean implements CacheService {

	@Autowired
	CacheService delegate;

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<CacheDto> getAllCaches() {
		return delegate.getAllCaches();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void removeCache(String value) {
		delegate.removeCache(value);
	}

    @Override
	@RolesAllowed({"NOT_SUPER"})
    public void removeAllCaches() {
        delegate.removeAllCaches();
    }

}
