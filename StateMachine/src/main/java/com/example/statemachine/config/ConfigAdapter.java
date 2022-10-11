package com.example.statemachine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;

public class ConfigAdapter {

}
//
//@Configuration
//@EnableStateMachineFactory
//public static class Config extends StateMachineConfigurerAdapter<String, String> {
//
//    @Autowired
//    private StateRepository<? extends RepositoryState> stateRepository;
//
//    @Autowired
//    private TransitionRepository<? extends RepositoryTransition> transitionRepository;
//
//    @Override
//    public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
//        model
//                .withModel()
//                .factory(modelFactory());
//    }
//
//    @Bean
//    public StateMachineModelFactory<String, String> modelFactory() {
//        return new RepositoryStateMachineModelFactory(stateRepository, transitionRepository);
//    }
//}