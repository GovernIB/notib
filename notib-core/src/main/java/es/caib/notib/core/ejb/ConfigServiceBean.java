/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.config.ConfigDto;
import es.caib.notib.core.api.dto.config.ConfigGroupDto;
import es.caib.notib.core.api.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConfigServiceBean implements ConfigService {

	@Autowired
	ConfigService delegate;

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return delegate.updateProperty(property);
	}
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ConfigGroupDto> findAll(){
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<String> syncFromJBossProperties(){
		return delegate.syncFromJBossProperties();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		return delegate.findEntitatsConfigByKey(key);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void crearPropietatsConfigPerEntitats() {
		delegate.crearPropietatsConfigPerEntitats();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void actualitzarPropietatsJBossBdd() {
		delegate.actualitzarPropietatsJBossBdd();
	}

	@Override
	public String getPropertyValue(String key) {
		return delegate.getPropertyValue(key);
	}
}
