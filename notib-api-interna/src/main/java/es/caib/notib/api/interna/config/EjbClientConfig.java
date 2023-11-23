/**
 * 
 */
package es.caib.notib.api.interna.config;

import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.AdviserServiceWs;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.NotificacioServiceWs;
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
//@Profile("!boot")
@ConditionalOnWarDeployment
//@Configuration("apiInternaEjbClientConfig")
@Configuration
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/notib-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
//	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean adviserServiceWs() {
		return getLocalEjbFactoyBean(AdviserServiceWs.class);
	}
	@Bean
//	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean adviserService() {
		return getLocalEjbFactoyBean(AdviserService.class);
	}
	@Bean
//	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}
	@Bean
//	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean enviamentService() {
		return getLocalEjbFactoyBean(EnviamentService.class);
	}
	@Bean
//	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean notificacioService() {
		return getLocalEjbFactoyBean(NotificacioService.class);
	}
	@Bean
//	@ConditionalOnWarDeployment
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
