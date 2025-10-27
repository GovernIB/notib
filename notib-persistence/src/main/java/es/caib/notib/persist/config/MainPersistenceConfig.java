package es.caib.notib.persist.config;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.persist.base.config.BasePersistenceConfig;
import es.caib.notib.persist.base.repository.BaseRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

/**
 * Configuració dels components de persistència.
 * 
 * @author Límit Tecnologies
 */
@Configuration
@EnableJpaRepositories(
		basePackages = {
				BaseConfig.BASE_PACKAGE + ".persist.repository",
				BaseConfig.BASE_PACKAGE + ".persist.resourcerepository",
				"org.springframework.statemachine.data.jpa"
		},
		entityManagerFactoryRef = "mainEntityManager",
		transactionManagerRef = "mainTransactionManager",
		repositoryBaseClass = BaseRepositoryImpl.class
)
public class MainPersistenceConfig extends BasePersistenceConfig {

	@Bean
	public StateMachineRuntimePersister<EnviamentSmEstat, EnviamentSmEvent, String> stateMachineRuntimePersister(
			JpaStateMachineRepository jpaStateMachineRepository) {
		return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}

	protected String[] getEntityPackages() {
		return new String[] {
				BaseConfig.BASE_PACKAGE + ".persist.entity",
				"org.springframework.statemachine.data.jpa"
		};
	}

}
