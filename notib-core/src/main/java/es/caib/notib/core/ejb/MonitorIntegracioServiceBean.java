/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.MonitorIntegracioService;

/**
 * Implementació de MonitorIntegracioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MonitorIntegracioServiceBean implements MonitorIntegracioService {

	@Autowired
	MonitorIntegracioService delegate;

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio) {
		return delegate.integracioFindDarreresAccionsByCodi(codi, paginacio);
	}
	
	@Override
	@RolesAllowed({"DIS_SUPER"})
	public Map<String, Integer> countErrors() {
		return delegate.countErrors();
	}
	

}
