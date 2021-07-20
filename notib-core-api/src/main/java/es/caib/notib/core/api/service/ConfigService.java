package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.config.ConfigDto;
import es.caib.notib.core.api.dto.config.ConfigGroupDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió dels paràmetres de configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigService {

//	<T> T findProperty (String key);

	/**
	 * Actualitza el valor d'una propietat de configuració.
	 *
	 * @param property Informació que es vol actualitzar.
	 * @return El DTO amb les dades modificades.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	ConfigDto updateProperty(ConfigDto property) throws Exception;

	@PreAuthorize("hasRole('NOT_SUPER')")
	List<ConfigGroupDto> findAll();

}

