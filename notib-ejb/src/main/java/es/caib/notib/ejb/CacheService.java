/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.CacheDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de CacheService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class CacheService extends AbstractService<es.caib.notib.logic.intf.service.CacheService> implements es.caib.notib.logic.intf.service.CacheService {

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<CacheDto> getAllCaches() {
		return getDelegateService().getAllCaches();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void removeCache(String value) {
		getDelegateService().removeCache(value);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void removeAllCaches() {
		getDelegateService().removeAllCaches();
	}

}
