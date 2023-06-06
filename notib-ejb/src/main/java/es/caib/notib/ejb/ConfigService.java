/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class ConfigService extends AbstractService<es.caib.notib.logic.intf.service.ConfigService> implements es.caib.notib.logic.intf.service.ConfigService {

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return getDelegateService().updateProperty(property);
	}
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ConfigGroupDto> findAll(){
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<String> syncFromJBossProperties(){
		return getDelegateService().syncFromJBossProperties();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		return getDelegateService().findEntitatsConfigByKey(key);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void crearPropietatsConfigPerEntitats() {
		getDelegateService().crearPropietatsConfigPerEntitats();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void actualitzarPropietatsJBossBdd() {
		getDelegateService().actualitzarPropietatsJBossBdd();
	}

	@Override
	@PermitAll
	public String getPropertyValue(String key) {
		return getDelegateService().getPropertyValue(key);
	}
}
