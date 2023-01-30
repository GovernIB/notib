/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioDetall;
import es.caib.notib.logic.intf.dto.IntegracioDto;
import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import org.springframework.data.domain.Page;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Implementació de MonitorIntegracioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorIntegracioService extends AbstractService<es.caib.notib.logic.intf.service.MonitorIntegracioService> implements es.caib.notib.logic.intf.service.MonitorIntegracioService {

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return getDelegateService().integracioFindAll();
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {
		return getDelegateService().integracioFindDarreresAccionsByCodi(codi, paginacio, filtre);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public Map<String, Integer> countErrors() {
		return getDelegateService().countErrors();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void netejarMonitor() {
		getDelegateService().netejarMonitor();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public IntegracioDetall detallIntegracio(Long id) {
		return getDelegateService().detallIntegracio(id);
	}
}
