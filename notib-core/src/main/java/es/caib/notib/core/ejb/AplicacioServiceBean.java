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

import es.caib.notib.core.api.dto.ExcepcioLogDto;
import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;



	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public String getVersioActual() {
		return delegate.getVersioActual();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public void processarAutenticacioUsuari() {
		delegate.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public UsuariDto getUsuariActual() {
		return delegate.getUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegate.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		return delegate.findUsuariAmbText(text);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		return delegate.integracioFindDarreresAccionsByCodi(codi);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		delegate.excepcioSave(exception);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegate.excepcioFindOne(index);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegate.excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return delegate.permisosFindRolsDistinctAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public String propertyGet(String property) {
		return delegate.propertyGet(property);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public Map<String, String> propertyFindByPrefix(String prefix) {
		return delegate.propertyFindByPrefix(prefix);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean pluginSeuDisponible() {
		return delegate.pluginSeuDisponible();
	}

}
