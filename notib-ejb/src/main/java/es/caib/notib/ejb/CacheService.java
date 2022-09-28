/**
 * 
 */
package es.caib.notib.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.dto.CacheDto;
import es.caib.notib.logic.intf.dto.PaginaDto;

/**
 * Implementació de CacheService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class CacheService extends AbstractService<es.caib.notib.logic.intf.service.CacheService> implements es.caib.notib.logic.intf.service.CacheService {

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

}
