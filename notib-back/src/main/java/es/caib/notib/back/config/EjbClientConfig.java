/**
 * 
 */
package es.caib.notib.back.config;

import es.caib.notib.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/notib-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserService() {
		return getLocalEjbFactoyBean(AdviserService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserServiceWs() {
		return getLocalEjbFactoyBean(AdviserServiceWs.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean cacheService() {
		return getLocalEjbFactoyBean(CacheService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean callbackService() {
		return getLocalEjbFactoyBean(CallbackService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean enviamentService() {
		return getLocalEjbFactoyBean(EnviamentService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean enviamentSmService() {
		return getLocalEjbFactoyBean(EnviamentSmService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean gestioDocumentalService() {
		return getLocalEjbFactoyBean(GestioDocumentalService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean grupService() {
		return getLocalEjbFactoyBean(GrupService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean justificantService() {
		return getLocalEjbFactoyBean(JustificantService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorIntegracioService() {
		return getLocalEjbFactoyBean(MonitorIntegracioService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorTasquesService() {
		return getLocalEjbFactoyBean(MonitorTasquesService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioMassivaService() {
		return getLocalEjbFactoyBean(NotificacioMassivaService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioService() {
		return getLocalEjbFactoyBean(NotificacioService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioServiceWs() {
		return getLocalEjbFactoyBean(NotificacioServiceWs.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean operadorPostalService() {
		return getLocalEjbFactoyBean(OperadorPostalService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorService() {
		return getLocalEjbFactoyBean(OrganGestorService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieFormatFullaService() {
		return getLocalEjbFactoyBean(PagadorCieFormatFullaService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieFormatSobreService() {
		return getLocalEjbFactoyBean(PagadorCieFormatSobreService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieService() {
		return getLocalEjbFactoyBean(PagadorCieService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean permisosService() {
		return getLocalEjbFactoyBean(PermisosService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentService() {
		return getLocalEjbFactoyBean(ProcedimentService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiService() {
		return getLocalEjbFactoyBean(ServeiService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariAplicacioService() {
		return getLocalEjbFactoyBean(UsuariAplicacioService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean columnesService() {
		return getLocalEjbFactoyBean(ColumnesService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean cieAdviserService() {
		return getLocalEjbFactoyBean(CieAdviserService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean activeMqService() {
		return getLocalEjbFactoyBean(ActiveMqService.class);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariService() {
		return getLocalEjbFactoyBean(UsuariService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.info("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
