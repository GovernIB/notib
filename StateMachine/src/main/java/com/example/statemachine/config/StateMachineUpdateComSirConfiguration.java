package com.example.statemachine.config;

import com.example.statemachine.enums.EstatsUpdateComSir;
import com.example.statemachine.enums.EventsUpdateComSir;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "UPDATE_COM_SIR")
public class StateMachineUpdateComSirConfiguration extends StateMachineConfigurerAdapter<EstatsUpdateComSir, EventsUpdateComSir> {


    @Override
    public void configure(StateMachineStateConfigurer<EstatsUpdateComSir, EventsUpdateComSir> states) throws Exception {

        states.withStates().initial(EstatsUpdateComSir.INICI)
            .state(EstatsUpdateComSir.PENDENT)
            .choice(EstatsUpdateComSir.CHECK_REGISTRAT)
            .state(EstatsUpdateComSir.ERROR_REGISTRE)
            .choice(EstatsUpdateComSir.PARCIAL_ENVIADA_SIR)
            .state(EstatsUpdateComSir.ENVIADA_SIR)
            .choice(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS)
            .state(EstatsUpdateComSir.ERROR_SIR)
            .choice(EstatsUpdateComSir.CHECK_TOTS_ERROR)
            .state(EstatsUpdateComSir.CHECK_ENV_PARCIALS)
            .choice(EstatsUpdateComSir.TOTS_ACT_CHECK_ENV_PARCIAL)
            .choice(EstatsUpdateComSir.ENV_PARCIAL_CHECK_ESTAT_ENV)
            .choice(EstatsUpdateComSir.TOTS_ENVIATS_CHECK_ESTAT_ENV)
            .state(EstatsUpdateComSir.FINALITZADA_ACCEPTADA)
            .state(EstatsUpdateComSir.FINALITZADA_REBUTJADA)
            .state(EstatsUpdateComSir.FINALITZADA_PARCIALMENT_ACCPETADA)
            .state(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_ACCEPTADA)
            .state(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_REBUTJADA)
            .state(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_PARCIALMENT_ACCPETADA)
            .choice(EstatsUpdateComSir.NO_TOTS_ACT_CHECK_ENV_PARCIAL)
            .choice(EstatsUpdateComSir.NO_TOTS_ACT_ENV_PARCIAL_CHECK_ESTAT_ENV)
            .choice(EstatsUpdateComSir.NO_TOTS_ACT_TOTS_ENVIATS_CHECK_ESTAT_ENV)
            .state(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_ACCEPTADA)
            .state(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_REBUTJADA)
            .state(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_PARCIALMENT_ACCPETADA)
            .state(EstatsUpdateComSir.ENVIADA_SIR_ACCEPTADA)
            .state(EstatsUpdateComSir.ENVIADA_SIR_REBUTJADA)
            .state(EstatsUpdateComSir.ENVIADA_SIR_PARCIALMENT_ACCPETADA)
            .end(EstatsUpdateComSir.FI)
            .states(EnumSet.allOf(EstatsUpdateComSir.class));
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EstatsUpdateComSir, EventsUpdateComSir> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(new StateMachineListener());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EstatsUpdateComSir, EventsUpdateComSir> transitions) throws Exception {

        transitions.withExternal().source(EstatsUpdateComSir.INICI).target((EstatsUpdateComSir.PENDENT)).event(EventsUpdateComSir.ALTA)
                .and().withExternal().source(EstatsUpdateComSir.PENDENT).target(EstatsUpdateComSir.CHECK_REGISTRAT).event(EventsUpdateComSir.REGISTRAR_ENV_SIR)
                .and().withChoice().source(EstatsUpdateComSir.CHECK_REGISTRAT)
                    .first(EstatsUpdateComSir.ENVIADA_SIR, totRegistrat())
                    .then(EstatsUpdateComSir.PARCIAL_ENVIADA_SIR, isRegistreParcial())
                    .last(EstatsUpdateComSir.ERROR_REGISTRE);

        transitions.withChoice().source(EstatsUpdateComSir.PARCIAL_ENVIADA_SIR)
                    .first(EstatsUpdateComSir.CHECK_REGISTRAT, reintetarEnvError())
                    .last(EstatsUpdateComSir.PARCIAL_ENVIADA_SIR );
        transitions.withExternal().source(EstatsUpdateComSir.ERROR_REGISTRE).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);

        transitions.withChoice().source(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS)
                    .first(EstatsUpdateComSir.CHECK_TOTS_ERROR, totsActualitzats())
                    .last(EstatsUpdateComSir.NO_TOTS_ACT_CHECK_ENV_PARCIAL);

        transitions.withChoice().source(EstatsUpdateComSir.CHECK_TOTS_ERROR)
                    .first(EstatsUpdateComSir.ERROR_SIR, totsError())
                    .last(EstatsUpdateComSir.TOTS_ACT_CHECK_ENV_PARCIAL);
        transitions.withExternal().source(EstatsUpdateComSir.ERROR_SIR).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);

        // TOTS ACTUALITZATS
        transitions.withChoice().source(EstatsUpdateComSir.TOTS_ACT_CHECK_ENV_PARCIAL)
                .first(EstatsUpdateComSir.ENV_PARCIAL_CHECK_ESTAT_ENV, isEnviamentParcial())
                .last(EstatsUpdateComSir.TOTS_ENVIATS_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateComSir.TOTS_ENVIATS_CHECK_ESTAT_ENV)
                .first(EstatsUpdateComSir.FINALITZADA_ACCEPTADA, totsAcceptats())
                .then(EstatsUpdateComSir.FINALITZADA_REBUTJADA, totsRebutjats())
                .last(EstatsUpdateComSir.FINALITZADA_PARCIALMENT_ACCPETADA);
        transitions.withExternal().source(EstatsUpdateComSir.FINALITZADA_ACCEPTADA).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateComSir.FINALITZADA_REBUTJADA).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateComSir.FINALITZADA_PARCIALMENT_ACCPETADA).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);

        transitions.withChoice().source(EstatsUpdateComSir.ENV_PARCIAL_CHECK_ESTAT_ENV)
                .first(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_ACCEPTADA, totsAcceptats())
                .then(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_REBUTJADA, totsRebutjats())
                .last(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_PARCIALMENT_ACCPETADA);
        transitions.withExternal().source(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_ACCEPTADA).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_REBUTJADA).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateComSir.FINALITZADA_AMB_ERRORS_PARCIALMENT_ACCPETADA).target(EstatsUpdateComSir.FI).event(EventsUpdateComSir.FINALITZAR);

        // NO TOTS ACTUALITZATS
        transitions.withChoice().source(EstatsUpdateComSir.NO_TOTS_ACT_CHECK_ENV_PARCIAL)
                .first(EstatsUpdateComSir.NO_TOTS_ACT_ENV_PARCIAL_CHECK_ESTAT_ENV, isEnviamentParcial())
                .last(EstatsUpdateComSir.NO_TOTS_ACT_TOTS_ENVIATS_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateComSir.NO_TOTS_ACT_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .first(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_ACCEPTADA, totsAcceptats())
                .first(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_REBUTJADA, totsRebutjats())
                .last(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_PARCIALMENT_ACCPETADA);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_ACCEPTADA).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_REBUTJADA).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR_AMB_ERRORS_PARCIALMENT_ACCPETADA).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);

        transitions.withChoice().source(EstatsUpdateComSir.NO_TOTS_ACT_TOTS_ENVIATS_CHECK_ESTAT_ENV)
                .first(EstatsUpdateComSir.ENVIADA_SIR_ACCEPTADA, totsAcceptats())
                .first(EstatsUpdateComSir.ENVIADA_SIR_REBUTJADA, totsRebutjats())
                .last(EstatsUpdateComSir.ENVIADA_SIR_PARCIALMENT_ACCPETADA);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR_ACCEPTADA).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR_REBUTJADA).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);
        transitions.withExternal().source(EstatsUpdateComSir.ENVIADA_SIR_PARCIALMENT_ACCPETADA).target(EstatsUpdateComSir.CHECK_TOTS_ACTUALITZATS).event(EventsUpdateComSir.ACTUALITZAR_ENV_SIR);

    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> totsRebutjats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> totsAcceptats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> isEnviamentParcial() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> totsError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> totsActualitzats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> reintetarEnvError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> totRegistrat() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateComSir, EventsUpdateComSir> isRegistreParcial() {
        return ctx -> {
            return true;
        };
    }
}
