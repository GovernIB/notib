package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió dels paràmetres de configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigService {

	public static final String APP_PROPERTIES = "es.caib.notib.properties";
	public static final String APP_SYSTEM_PROPERTIES = "es.caib.notib.system.properties";

	/**
	 * Actualitza el valor d'una propietat de configuració.
	 *
	 * @param property Informació que es vol actualitzar.
	 * @return El DTO amb les dades modificades.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	ConfigDto updateProperty(ConfigDto property) throws Exception;

	/**
	 * Consulta totes les propietats de configuració de l'aplicació.
	 * Retorna les propietats estructurades en forma d'arbres segons al grup o subgrups al que pertanyen.
	 * Cada arbre està format per un grup de propietats que no pertany a cap altre grup.
	 *
	 * @return Retorna un llistat de tots els grups de propietats que no pertanyen a cap grup.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	List<ConfigGroupDto> findAll();


	/**
	 * Procediment que actualitza totes les propietats de configuració per a configurar-les amb
	 * el valor de les properties configurades a JBoss.
	 *
	 * L'execució d'aquest procés només actualitza el valor de les properties que ja existeixen a la base de dades
	 * i que a més també estan definides al fitxer JBoss.
	 *
	 * @return Llistat de les properties editades.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	List<String> syncFromJBossProperties();

	@PreAuthorize("hasRole('NOT_SUPER')")
	List<ConfigDto> findEntitatsConfigByKey(String key);

	@PreAuthorize("hasRole('NOT_SUPER')")
	void crearPropietatsConfigPerEntitats();

	@PreAuthorize("hasRole('NOT_SUPER')")
	void actualitzarPropietatsJBossBdd();

	@PreAuthorize("hasRole('NOT_SUPER')")
	String getPropertyValue(String key);

}
