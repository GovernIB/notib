/**
 * 
 */
package es.caib.notib.api.externa.config;

import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.CieAdviserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/notib-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserService() {
		return getLocalEjbFactoyBean(AdviserService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean cieAdviserService() {
		return getLocalEjbFactoyBean(CieAdviserService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {

		var jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.debug("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		var factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
