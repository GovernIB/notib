package com.example.statemachine.config;

import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
@Profile("jpa")
@EnableJpaRepositories("org.springframework.statemachine.data.jpa")
@EntityScan("org.springframework.statemachine.data.jpa")
public class JpaPersisterConfig {

    @Bean
    public StateMachineRuntimePersister<EstatsComSIR, EventsComSIR, String> stateMachineRuntimePersister(JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }
}