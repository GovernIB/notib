package es.caib.notib.persist.config;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
@EnableJpaRepositories({"es.caib.notib.persist", "org.springframework.statemachine.data.jpa"})
@EntityScan({"es.caib.notib.persist", "org.springframework.statemachine.data.jpa"})
public class JpaPersisterConfig {

    @Bean
    public StateMachineRuntimePersister<EnviamentSmEstat, EnviamentSmEvent, String> stateMachineRuntimePersister(JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }
}