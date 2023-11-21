/**
 * 
 */
package es.caib.notib.api.interna.config;

import es.caib.notib.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Profile("!boot")
@Configuration
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/notib-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserServiceWs() {
		return getLocalEjbFactoyBean(AdviserServiceWs.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserService() {
		return getLocalEjbFactoyBean(AdviserService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean enviamentService() {
		return getLocalEjbFactoyBean(EnviamentService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioService() {
		return getLocalEjbFactoyBean(NotificacioService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioServiceWs() {
		return getLocalEjbFactoyBean(NotificacioServiceWs.class);
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
