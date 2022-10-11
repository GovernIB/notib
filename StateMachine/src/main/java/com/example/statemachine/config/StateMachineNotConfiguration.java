package com.example.statemachine.config;

import com.example.statemachine.enums.EstatsNot;
import com.example.statemachine.enums.EventsNot;
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
//@EnableStateMachine
@EnableStateMachineFactory(name = "NOT")
public class StateMachineNotConfiguration extends StateMachineConfigurerAdapter<EstatsNot, EventsNot> {

    @Override
    public void configure(StateMachineStateConfigurer<EstatsNot, EventsNot> states) throws Exception {

        states.withStates().initial(EstatsNot.INICI)
                .state(EstatsNot.PENDENT)
                .choice(EstatsNot.REGISTRAR)
                .state(EstatsNot.ERROR_REGISTRE)
                .state(EstatsNot.REGISTRAT)
                .choice(EstatsNot.ENVIAR)
                .state(EstatsNot.ENVIAR_EMAIL)
                .choice(EstatsNot.CHECK_ENVIAT_EMAIL)
                .state(EstatsNot.ENVIAR_EMAIL)
                .state(EstatsNot.ERROR_ENVIAR_EMAIL)
                .state(EstatsNot.REINTENTAR_REGISTRE)
                .state(EstatsNot.ENVIAR_NOTIFICA)
                .choice(EstatsNot.CHECK_ENVIAT_NOTIFICA)
                .state(EstatsNot.PENDENT_COMPAREIXENCA)
                .state(EstatsNot.ERROR_ENVIAR_NOTIFICA)
                .state(EstatsNot.REINTENTAR_ENVIAR_NOTIFICA)
                .choice(EstatsNot.OBTENIR_ESTAT_NOTIFICA)
                .choice(EstatsNot.CHECK_ESTAT_FINAL)
                .state(EstatsNot.ERROR_ACTUALITZAR_ESTAT_NOTIFICA)
                .choice(EstatsNot.CHECK_ADVISER)
                .choice(EstatsNot.REINTENTAR_OBTENIR_ESTAT_NOTIFICA)
                .end(EstatsNot.FI)
                .states(EnumSet.allOf(EstatsNot.class));

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EstatsNot, EventsNot> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(new StateMachineListener());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EstatsNot, EventsNot> transitions) throws Exception {

        transitions.withExternal().source(EstatsNot.INICI).target(EstatsNot.PENDENT).event(EventsNot.ALTA)
                .and().withExternal().source(EstatsNot.PENDENT).target(EstatsNot.REGISTRAR).event(EventsNot.REGISTRAR)
                .and().withChoice().source(EstatsNot.REGISTRAR)
                    .first(EstatsNot.REGISTRAT, totsRegistratsNot())
                    .then(EstatsNot.ERROR_REGISTRE, errorRegistreNot())
                    .last(EstatsNot.FI);

        transitions.withExternal().source(EstatsNot.REGISTRAT).target(EstatsNot.ENVIAR).event(EventsNot.ENVIAR);

        transitions.withChoice().source(EstatsNot.ENVIAR).first(EstatsNot.ENVIAR_EMAIL, interessatSenseNif()).last(EstatsNot.ENVIAR_NOTIFICA);

        transitions.withExternal().source(EstatsNot.ENVIAR_EMAIL).target(EstatsNot.CHECK_ENVIAT_EMAIL).event(EventsNot.CHECK_ENVIAT_EMAIL);
        transitions.withChoice().source(EstatsNot.CHECK_ENVIAT_EMAIL).first(EstatsNot.ENVIAT_EMAIL, enviatMailOk()).last(EstatsNot.ERROR_ENVIAR_EMAIL);
        transitions.withExternal().source(EstatsNot.ENVIAT_EMAIL).target(EstatsNot.FI).event(EventsNot.FI);
        transitions.withExternal().source(EstatsNot.ERROR_ENVIAR_EMAIL).target(EstatsNot.REINTENTAR_REGISTRE).event(EventsNot.REINTENTAR_REGISTRE);
        transitions.withChoice().source(EstatsNot.REINTENTAR_REGISTRE).first(EstatsNot.ENVIAR_EMAIL, reinentarEnviarEmail()).last(EstatsNot.FI);

        //TODO -> COMENTARI EN L'ANALISIS S'ENVIEN TOTES LES  COMUNICACIONS ALHORA.
        transitions.withExternal().source(EstatsNot.ENVIAR_NOTIFICA).target(EstatsNot.CHECK_ENVIAT_NOTIFICA).event(EventsNot.CHECK_ENVIAT_NOTIFICA);
        transitions.withChoice().source(EstatsNot.CHECK_ENVIAT_NOTIFICA).first(EstatsNot.PENDENT_COMPAREIXENCA, isEnviatNotifica()).last(EstatsNot.ERROR_ENVIAR_NOTIFICA);
        transitions.withExternal().source(EstatsNot.ERROR_ENVIAR_NOTIFICA).target(EstatsNot.REINTENTAR_ENVIAR_NOTIFICA).event(EventsNot.REINTENTAR_ENVIAR_NOTIFICA);
        transitions.withChoice().source(EstatsNot.REINTENTAR_ENVIAR_NOTIFICA).first(EstatsNot.ENVIAR_NOTIFICA, reinentarEnviarEmail()).last(EstatsNot.FI);

        // TODO CERCLE NOTIFICA ADVISER?¿
        transitions.withExternal().source(EstatsNot.PENDENT_COMPAREIXENCA).target(EstatsNot.OBTENIR_ESTAT_NOTIFICA).event(EventsNot.OBTENIR_ESTAT_NOTIFICA);
        transitions.withChoice().source(EstatsNot.OBTENIR_ESTAT_NOTIFICA).first(EstatsNot.CHECK_ESTAT_FINAL, isEstatFinal()).last(EstatsNot.ERROR_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withExternal().source(EstatsNot.ERROR_ACTUALITZAR_ESTAT_NOTIFICA).target(EstatsNot.CHECK_ADVISER).event(EventsNot.CHECK_ADVISER);
        transitions.withChoice().source(EstatsNot.CHECK_ADVISER).first(EstatsNot.OBTENIR_ESTAT_NOTIFICA, isAdviser()).last(EstatsNot.REINTENTAR_OBTENIR_ESTAT_NOTIFICA);
//        transitions.withExternal().source(EstatsNot.REINTENTAR_OBTENIR_ESTAT_NOTIFICA).target(EstatsNot.CHECK_ADVISER).event(EventsNot.CHECK_ADVISER);
        transitions.withChoice().source(EstatsNot.REINTENTAR_OBTENIR_ESTAT_NOTIFICA).first(EstatsNot.OBTENIR_ESTAT_NOTIFICA, reintentarObtenirEstatNotifica()).last(EstatsNot.FI);


        transitions.withChoice().source(EstatsNot.CHECK_ESTAT_FINAL).first(EstatsNot.OBTENIR_ESTAT_NOTIFICA, noEstatFinal())
                .then(EstatsNot.LLEGIDA, checkEstat()) // Nomes comunicacions
                .then(EstatsNot.NOTIFICADA, checkEstat())
                .then(EstatsNot.REBUTJADA,checkEstat())
                .then(EstatsNot.EXPIRADA, checkEstat())
                .then(EstatsNot.ABSENT, checkEstat())
                .then(EstatsNot.DESCONEGUT, checkEstat())
                .then(EstatsNot.ADRECA_INCORRETA, checkEstat())
                .then(EstatsNot.DIFUNT, checkEstat())
                .then(EstatsNot.EXTRAVIADA, checkEstat())
                .then(EstatsNot.SENSE_INFORMACIO, checkEstat())
                .last(EstatsNot.ERROR_AGENT);

        // TODO DESPRES D'AQUESTS ESTATS S'HA DE PASSAR A ESTAT FI?
    }

    @Bean
    public Guard<EstatsNot, EventsNot> checkEstat() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> noEstatFinal() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> reintentarObtenirEstatNotifica() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> isAdviser() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> isEstatFinal() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> isEnviatNotifica() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> reinentarEnviarEmail() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> interessatSenseNif() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> enviatMailOk() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> errorRegistreNot() {
        return ctx -> {
            var errorRegistre = (int) ctx.getExtendedState().getVariables().getOrDefault("errorRegistreNot", 0);
            errorRegistre++;
            System.out.println("errorRegistreNot: " + errorRegistre);
            ctx.getExtendedState().getVariables().put("errorRegistreNot", errorRegistre);
            return errorRegistre <= 4;
        };
    }

    @Bean
    public Guard<EstatsNot, EventsNot> totsRegistratsNot() {
        return ctx -> {
            var errorRegistre = (int) ctx.getExtendedState().getVariables().getOrDefault("errorRegistreNot", 0);
            return true;
//            return errorRegistre > 3;
        };
    }
}
