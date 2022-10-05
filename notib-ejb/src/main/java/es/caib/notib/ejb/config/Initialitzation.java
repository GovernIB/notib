/**
 * 
 */
package es.caib.notib.ejb.config;

import es.caib.notib.ejb.config.EjbContextConfig;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Singleton utilitzat per a forçar la inicialització del context de Spring.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Startup
@Singleton
public class Initialitzation {

	@PostConstruct
	private void startup() {
		EjbContextConfig.getApplicationContext();
	}

}
