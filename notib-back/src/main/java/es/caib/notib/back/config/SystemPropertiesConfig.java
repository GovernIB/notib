/**
 * 
 */
package es.caib.notib.back.config;

import es.caib.notib.logic.intf.service.ConfigService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuració de les propietats de l'aplicació a partir de la configuració
 * de les propietats de sistema (System.getProperty).
 * 
 * @author Limit Tecnologies
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
	"file://${" + ConfigService.APP_PROPERTIES + "}",
	"file://${" + ConfigService.APP_SYSTEM_PROPERTIES + "}"})
public class SystemPropertiesConfig {

}
