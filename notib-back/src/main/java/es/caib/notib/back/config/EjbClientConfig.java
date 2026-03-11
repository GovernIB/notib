/**
 *
 */
package es.caib.notib.back.config;

import es.caib.notib.logic.intf.base.service.PermissionEvaluatorService;
import es.caib.notib.logic.intf.base.service.ResourceApiService;
import es.caib.notib.logic.intf.resourceservice.*;
import es.caib.notib.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
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
@ConditionalOnWarDeployment
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/notib-ejb/";
	private static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	public LocalStatelessSessionProxyFactoryBean aclEntryResourceService() {
		return getLocalEjbFactoyBean(AclEntryResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioResourceService() {
		return getLocalEjbFactoyBean(AplicacioResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean avisResourceService() {
		return getLocalEjbFactoyBean(AvisResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configGroupResourceService() {
		return getLocalEjbFactoyBean(ConfigGroupResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configResourceService() {
		return getLocalEjbFactoyBean(ConfigResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configTypeResourceService() {
		return getLocalEjbFactoyBean(ConfigTypeResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean dir3ResourceService() {
		return getLocalEjbFactoyBean(Dir3ResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean documentResourceService() {
		return getLocalEjbFactoyBean(DocumentResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatResourceService() {
		return getLocalEjbFactoyBean(EntitatResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatTipusDocumentResourceService() {
		return getLocalEjbFactoyBean(EntitatTipusDocumentResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean grupResourceService() {
		return getLocalEjbFactoyBean(GrupResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorIntegracioParamResourceService() {
		return getLocalEjbFactoyBean(MonitorIntegracioParamResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorIntegracioResourceService() {
		return getLocalEjbFactoyBean(MonitorIntegracioResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioEnviamentResourceService() {
		return getLocalEjbFactoyBean(NotificacioEnviamentResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioResourceService() {
		return getLocalEjbFactoyBean(NotificacioResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioTableResourceService() {
		return getLocalEjbFactoyBean(NotificacioTableResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorResourceService() {
		return getLocalEjbFactoyBean(OrganGestorResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieFormatFullaResourceService() {
		return getLocalEjbFactoyBean(PagadorCieFormatFullaResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieFormatSobreResourceService() {
		return getLocalEjbFactoyBean(PagadorCieFormatSobreResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieResourceService() {
		return getLocalEjbFactoyBean(PagadorCieResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entregaCieResourceService() {
		return getLocalEjbFactoyBean(EntregaCieResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorPostalResourceService() {
		return getLocalEjbFactoyBean(PagadorPostalResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean personaResourceService() {
		return getLocalEjbFactoyBean(PersonaResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentGrupResourceService() {
		return getLocalEjbFactoyBean(ProcedimentGrupResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentResourceService() {
		return getLocalEjbFactoyBean(ProcedimentResourceService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean sseEventService() {
		return getLocalEjbFactoyBean(SseEventService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariResourceService() {
		return getLocalEjbFactoyBean(UsuariResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserService() {
		return getLocalEjbFactoyBean(AdviserService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean adviserServiceWs() {
		return getLocalEjbFactoyBean(AdviserServiceWs.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean cacheService() {
		return getLocalEjbFactoyBean(CacheService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean callbackService() {
		return getLocalEjbFactoyBean(CallbackService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean enviamentService() {
		return getLocalEjbFactoyBean(EnviamentService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean enviamentSmService() {
		return getLocalEjbFactoyBean(EnviamentSmService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean gestioDocumentalService() {
		return getLocalEjbFactoyBean(GestioDocumentalService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean grupService() {
		return getLocalEjbFactoyBean(GrupService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean justificantService() {
		return getLocalEjbFactoyBean(JustificantService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorIntegracioService() {
		return getLocalEjbFactoyBean(MonitorIntegracioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorTasquesService() {
		return getLocalEjbFactoyBean(MonitorTasquesService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioMassivaService() {
		return getLocalEjbFactoyBean(NotificacioMassivaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioService() {
		return getLocalEjbFactoyBean(NotificacioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean notificacioServiceWs() {
		return getLocalEjbFactoyBean(NotificacioServiceWs.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean operadorPostalService() {
		return getLocalEjbFactoyBean(OperadorPostalService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorService() {
		return getLocalEjbFactoyBean(OrganGestorService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieFormatFullaService() {
		return getLocalEjbFactoyBean(PagadorCieFormatFullaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieFormatSobreService() {
		return getLocalEjbFactoyBean(PagadorCieFormatSobreService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean pagadorCieService() {
		return getLocalEjbFactoyBean(PagadorCieService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean permisosService() {
		return getLocalEjbFactoyBean(PermisosService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentService() {
		return getLocalEjbFactoyBean(ProcedimentService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiService() {
		return getLocalEjbFactoyBean(ServeiService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariAplicacioService() {
		return getLocalEjbFactoyBean(UsuariAplicacioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean columnesService() {
		return getLocalEjbFactoyBean(ColumnesService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean cieAdviserService() {
		return getLocalEjbFactoyBean(CieAdviserService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean activeMqService() {
		return getLocalEjbFactoyBean(ActiveMqService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariService() {
		return getLocalEjbFactoyBean(UsuariService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean accioMassivaService() {
		return getLocalEjbFactoyBean(AccioMassivaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean digitalitzacioService() {
		return getLocalEjbFactoyBean(DigitalitzacioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean permissionEvaluatorService() {
		return getLocalEjbFactoyBean(PermissionEvaluatorService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean resourceApiService() {
		return getLocalEjbFactoyBean(ResourceApiService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean logService() {
		return getLocalEjbFactoyBean(LogService.class, false);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(
		Class<?> serviceClass,
		boolean withSuffix) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + (withSuffix ? EJB_JNDI_SUFFIX : "");
		log.info("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
