/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ConfigService extends AbstractService<es.caib.notib.logic.intf.service.ConfigService> implements es.caib.notib.logic.intf.service.ConfigService {

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
