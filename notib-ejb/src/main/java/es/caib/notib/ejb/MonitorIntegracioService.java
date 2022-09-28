/**
 * 
 */
package es.caib.notib.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;

/**
 * Implementació de MonitorIntegracioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorIntegracioService extends AbstractService<es.caib.notib.logic.intf.service.MonitorIntegracioService> implements es.caib.notib.logic.intf.service.MonitorIntegracioService {

	@Autowired
	MonitorIntegracioService delegate;

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {
		return delegate.integracioFindDarreresAccionsByCodi(codi, paginacio, filtre);
	}
	
	@Override
	@RolesAllowed({"DIS_SUPER"})
	public Map<String, Integer> countErrors() {
		return delegate.countErrors();
	}
	

}
