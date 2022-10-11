package com.example.statemachine.config;

import com.example.statemachine.enums.EstatsUpdateNot;
import com.example.statemachine.enums.EventsUpdateNot;
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
@EnableStateMachineFactory(name = "updateNot")
public class StateMachineUpdateNotConfiguration  extends StateMachineConfigurerAdapter<EstatsUpdateNot, EventsUpdateNot> {

    @Override
    public void configure(StateMachineStateConfigurer<EstatsUpdateNot, EventsUpdateNot> states) throws Exception {
        states.withStates().initial(EstatsUpdateNot.INICI)
                .state(EstatsUpdateNot.PENDENT)
                .choice(EstatsUpdateNot.CHECK_REGISTRAT)
                .state(EstatsUpdateNot.REGISTRADA)
                .state(EstatsUpdateNot.ERROR_REGISTRE)
                .choice(EstatsUpdateNot.PARCIALMENT_REGISTRADA)
                .choice(EstatsUpdateNot.CHECK_NIF_DESTINATARIS)
                .state(EstatsUpdateNot.ENVIAR_TOTS_EMAIL)
                .state(EstatsUpdateNot.ENVIAMENT_MIXTE)
                .state(EstatsUpdateNot.ENVIAR_NOTIFICA)
                .choice(EstatsUpdateNot.TOTS_EMAIL_CHECK_TOTS_ENVIATS)
                .choice(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_CHECK_TOT_ERRORS)
                .choice(EstatsUpdateNot.TOTS_EMAIL_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL)
                .state(EstatsUpdateNot.ERROR_EMAIL)
                .choice(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_ERROR_CHECK_ENV_PARCIAL)
                .state(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_EMAIL)
                .choice(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_ERROR_CHECK_ESTAT_ENV)
                .state(EstatsUpdateNot.ENVIADA_AMB_ERRORS_PER_EMAIL)
                .choice(EstatsUpdateNot.TOTS_EMAIL_NO_TOTS_ENVIATS_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .state(EstatsUpdateNot.ENVIADA_PER_EMAIL)
                .choice(EstatsUpdateNot.MIXTE_CHECK_TOTS_ENVIATS)
                .choice(EstatsUpdateNot.MIXTE_TOTS_ENVIATS_CHECK_TOTS_ERROR)
                .choice(EstatsUpdateNot.MIXTE_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL)
                .state(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_MIXTE)
                .choice(EstatsUpdateNot.MIXTE_NO_TOTS_ENVIATS_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .state(EstatsUpdateNot.ENVIADA_ENVIAT_MIXTE)
                .state(EstatsUpdateNot.ERROR_MIXTE)
                .choice(EstatsUpdateNot.MIXTE_CHECK_ENVIADA_NOTIFICA)
                .state(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_MIXTE)
                .state(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA)
                .choice(EstatsUpdateNot.MITXTE_CHECK_TOTS_ACTUALITZATS)
                .choice(EstatsUpdateNot.MIXTE_TOTS_ACT_CHECK_TOT_ERRORS)
                .choice(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_CHECK_ENV_PARCIAL_O_ERROR_MAIL)
                .choice(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_ENV_PARCIAL_O_ERROR_MAIL_CHECK_ESTAT_ENV)
                .choice(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_NO_ENV_PARCIAL_O_ERROR_MAIL_CHECK_ESTAT_ENV)
                .state(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ERROR)
                .state(EstatsUpdateNot.ENVIADA_ENVIAMENT_MIXTE)
                .state(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAMENT_MIXTE)
                .choice(EstatsUpdateNot.MIXTE_TOTS_ACT_CHECK_TOT_ERRORS)
                .choice(EstatsUpdateNot.MIXTE_TOTS_ACT_TOTS_ERROR_CHECK_ENV_EMAIL_OK)
                .choice(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_CHECK_ENV_PARCIAL_O_EMAIL_OK)
                .state(EstatsUpdateNot.FINALITZADA_ENVIAMENT_MIXTE)
                .state(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAMENT_MIXTE)
                .choice(EstatsUpdateNot.NOTIFICA_CHECK_ENVIAT)
                .state(EstatsUpdateNot.ERROR_NOTIFICA)
                .state(EstatsUpdateNot.ENVIADA_A_NOTIFICA)
                .choice(EstatsUpdateNot.NOTIFICA_CHECK_REINTENTS)
                .choice(EstatsUpdateNot.NOTIFICA_CHECK_TOTS_ACTUALITZATS)
                .choice(EstatsUpdateNot.NOTIFICA_TOTS_ACT_CHECK_TOTS_ERROR)
                .choice(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_CHECK_ENV_PARCIAL)
                .choice(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .choice(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .state(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ERROR)
                .state(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA)
                .state(EstatsUpdateNot.ENVIADA_ENVIAT_PER_NOTIFICA)
                .choice(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_CHECK_ENV_PARCIAL)
                .choice(EstatsUpdateNot.ERROR_NOTIFICA_FINAL)
                .choice(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .choice(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .state(EstatsUpdateNot.FINALITZADA_ENVIAT_PER_NOTIFICA)
                .state(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA)
                .end(EstatsUpdateNot.FI)
                .states(EnumSet.allOf(EstatsUpdateNot.class));
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EstatsUpdateNot, EventsUpdateNot> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(new StateMachineListener());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EstatsUpdateNot, EventsUpdateNot> transitions) throws Exception {

        transitions.withExternal().source(EstatsUpdateNot.INICI).target(EstatsUpdateNot.PENDENT).event(EventsUpdateNot.ALTA)
                .and().withExternal().source(EstatsUpdateNot.PENDENT).target(EstatsUpdateNot.CHECK_REGISTRAT).event(EventsUpdateNot.REGISTRAR_ENVIAMENTS)
                .and().withChoice().source(EstatsUpdateNot.CHECK_REGISTRAT)
                    .first(EstatsUpdateNot.REGISTRADA, totsRegistrats())
                    .then(EstatsUpdateNot.ERROR_REGISTRE, capRegistrat())
                    .last(EstatsUpdateNot.PARCIALMENT_REGISTRADA);
        transitions.withExternal().source(EstatsUpdateNot.ERROR_REGISTRE).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);
        transitions.withChoice().source(EstatsUpdateNot.PARCIALMENT_REGISTRADA)
                    .first(EstatsUpdateNot.CHECK_REGISTRAT, reintentarEnvError())
                    .last(EstatsUpdateNot.CHECK_NIF_DESTINATARIS);

        transitions.withChoice().source(EstatsUpdateNot.CHECK_NIF_DESTINATARIS)
                    .first(EstatsUpdateNot.ENVIAR_TOTS_EMAIL, isTotsEnvMail())
                    .then(EstatsUpdateNot.ENVIAMENT_MIXTE, isEnviamentsMixte())
                    .last(EstatsUpdateNot.ENVIAR_NOTIFICA);

        // TOTS ELS ENVIAMENTS PER EMAIL
        transitions.withExternal().source(EstatsUpdateNot.ENVIAR_TOTS_EMAIL).target(EstatsUpdateNot.TOTS_EMAIL_CHECK_TOTS_ENVIATS).event(EventsUpdateNot.TOTS_EMAIL_CHECK_TOTS_ENVIATS)
                .and().withChoice().source(EstatsUpdateNot.TOTS_EMAIL_CHECK_TOTS_ENVIATS)
                    .first(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_CHECK_TOT_ERRORS, totsEmailIsTotEnviats())
                    .last(EstatsUpdateNot.TOTS_EMAIL_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL);

        // TOTS ENVIATS
        transitions.withChoice().source(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_CHECK_TOT_ERRORS)
                    .first(EstatsUpdateNot.ERROR_EMAIL, totsEmailTotsEnviatsIsTotsError())
                    .last(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_ERROR_CHECK_ENV_PARCIAL);
        transitions.withExternal().source(EstatsUpdateNot.ERROR_EMAIL).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);

        transitions.withChoice().source(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_ERROR_CHECK_ENV_PARCIAL)
                    .first(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_EMAIL, totsEmailTotsEnviatsErrorsIsEnviamentParcial())
                    .last(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_ERROR_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateNot.TOTS_EMAIL_TOTS_ENVIATS_ERROR_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.FINALITZADA_ENVIAT_PER_EMAIL, totsEmailTotsEnviatsErrorsnNoEnvParcialIsTotsEnviats())
                    .last(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_EMAIL);

        // NO TOTS ENVIATS
        transitions.withChoice().source(EstatsUpdateNot.TOTS_EMAIL_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL)
                    .first(EstatsUpdateNot.ENVIADA_AMB_ERRORS_PER_EMAIL, totsEmailNoTotsEnviatsIsEnvParcial())
                    .last(EstatsUpdateNot.TOTS_EMAIL_NO_TOTS_ENVIATS_NO_ENV_PARCIAL_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateNot.TOTS_EMAIL_NO_TOTS_ENVIATS_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.ENVIADA_PER_EMAIL, totsEmailNoTotsEnviatsNoEnvParcialIsTotsEnviats())
                    .last(EstatsUpdateNot.ENVIADA_AMB_ERRORS_PER_EMAIL);

        // ENVIAMENT MIXTE
        transitions.withExternal().source(EstatsUpdateNot.ENVIAMENT_MIXTE).target(EstatsUpdateNot.MIXTE_CHECK_TOTS_ENVIATS).event(EventsUpdateNot.MIXTE_CHECK_TOTS_ENVIATS);
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_CHECK_TOTS_ENVIATS)
                    .first(EstatsUpdateNot.MIXTE_TOTS_ENVIATS_CHECK_TOTS_ERROR, mixteIsTotsEnviats())
                    .last(EstatsUpdateNot.MIXTE_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL);

        // NO TOTS ENVIATS
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL)
                    .first(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_MIXTE, mixteNoTotsEnviatsIsEnvParcial())
                    .last(EstatsUpdateNot.MIXTE_NO_TOTS_ENVIATS_CHECK_ENV_PARCIAL);

        transitions.withChoice().source(EstatsUpdateNot.MIXTE_NO_TOTS_ENVIATS_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.ENVIADA_ENVIAT_MIXTE, mixteNoTotsEnviatsEnvParcialIsTotsEnviats())
                    .last(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_MIXTE);

        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_ENVIAT_MIXTE).target(EstatsUpdateNot.ENVIAMENT_MIXTE).event(EventsUpdateNot.ENVIAR_MIXTE);
        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_MIXTE).target(EstatsUpdateNot.ENVIAMENT_MIXTE).event(EventsUpdateNot.ENVIAR_MIXTE);

        // TOTS ENVIATS
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_TOTS_ENVIATS_CHECK_TOTS_ERROR)
                    .first(EstatsUpdateNot.ERROR_MIXTE, mixteTotsEnviatsIsTotsError())
                    .last(EstatsUpdateNot.MIXTE_CHECK_ENVIADA_NOTIFICA);
        transitions.withExternal().source(EstatsUpdateNot.ERROR_MIXTE).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);

        transitions.withChoice().source(EstatsUpdateNot.MIXTE_CHECK_ENVIADA_NOTIFICA)
                    .first(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_MIXTE, mixteNoEnviadaNotifica())
                    .last(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withExternal().source(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA).target(EstatsUpdateNot.MITXTE_CHECK_TOTS_ACTUALITZATS).event(EventsUpdateNot.MIXTA_ACTUALITZAR_ESTAT_NOTIFICA);

        // ACTUALITZAR ESTAT NOTIFICA
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA)
                    .first(EstatsUpdateNot.MIXTE_TOTS_ACT_CHECK_TOT_ERRORS, mixteIsTotsActualitzats())
                    .last(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_CHECK_ENV_PARCIAL_O_ERROR_MAIL);

        // NO TOTS ACTUALITZATS
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_CHECK_ENV_PARCIAL_O_ERROR_MAIL)
                    .first(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_ENV_PARCIAL_O_ERROR_MAIL_CHECK_ESTAT_ENV, mixteTotsActIsEnvParcialOErrorMail())
                    .last(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_NO_ENV_PARCIAL_O_ERROR_MAIL_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_ENV_PARCIAL_O_ERROR_MAIL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ERROR, mixteNoTotsActEnvParcialOErrorMailIsTotErrors())
                    .last(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAMENT_MIXTE);
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_NO_TOTS_ACT_NO_ENV_PARCIAL_O_ERROR_MAIL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.ENVIADA_ENVIAMENT_MIXTE, mixteNoTotsActNoEnvParcialOErrorMailIsTotsEstatsFinalsCorrectes())
                    .last(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAMENT_MIXTE);

        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ERROR).target(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA).event(EventsUpdateNot.MIXTA_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_ENVIAMENT_MIXTE).target(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA).event(EventsUpdateNot.MIXTA_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAMENT_MIXTE).target(EstatsUpdateNot.MIXTE_ACTUALITZAR_ESTAT_NOTIFICA).event(EventsUpdateNot.MIXTA_ACTUALITZAR_ESTAT_NOTIFICA);

        // TOTS ACTUALITZATS
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_TOTS_ACT_CHECK_TOT_ERRORS)
                    .first(EstatsUpdateNot.MIXTE_TOTS_ACT_TOTS_ERROR_CHECK_ENV_EMAIL_OK, mixteTotsActIsTotError())
                    .last(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_CHECK_ENV_PARCIAL_O_EMAIL_OK);

        transitions.withChoice().source(EstatsUpdateNot.MIXTE_TOTS_ACT_TOTS_ERROR_CHECK_ENV_EMAIL_OK)
                    .first(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_MIXTE , mixteTotsActTotsErrorIsEnviamentsMailOk())
                    .last(EstatsUpdateNot.ERROR_MIXTE);

        transitions.withChoice().source(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_CHECK_ENV_PARCIAL_O_EMAIL_OK)
                    .first(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_ENV_PARCIAL_O_ERRORS_MAIL_CHECK_ESTAT_ENV, mixteTotsActNoTotsErrorIsEnviamentParcialOErrorsEmail())
                    .last(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_NO_ENV_PARCIAL_O_ERRORS_MAIL_CHECK_ESTAT_ENV);

        // TODO AL DIAGRAMA SEMBLA QUE FALTA UNA DE LES BRANQUES DE LA CONDICIO
        transitions.withChoice().source(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_ENV_PARCIAL_O_ERRORS_MAIL_CHECK_ESTAT_ENV)
//                    .first(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAMENT_MIXTE, ).last()
                    ;

        transitions.withChoice().source(EstatsUpdateNot.MIXTE_TOTS_ACT_NO_TOTS_ERROR_NO_ENV_PARCIAL_O_ERRORS_MAIL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.FINALITZADA_ENVIAMENT_MIXTE,mixteTotsActNoTotsErrorNoEnvParcialOErrorsMailIsTotsEstatsFinalsCorrectes())
                    .last(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAMENT_MIXTE);

        transitions.withExternal().source(EstatsUpdateNot.FINALITZADA_ENVIAMENT_MIXTE).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAMENT_MIXTE).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);


        // TOTS ELS ENVIAMENTS PER NOTIFICA
        transitions.withExternal().source(EstatsUpdateNot.ENVIAR_NOTIFICA).target(EstatsUpdateNot.NOTIFICA_CHECK_ENVIAT).event(EventsUpdateNot.NOTIFICA_CHECK_ENVIAT);
        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_CHECK_ENVIAT)
                    .first(EstatsUpdateNot.ERROR_NOTIFICA, isErrorNotifica())
                    .last(EstatsUpdateNot.ENVIADA_A_NOTIFICA);

        transitions.withExternal().source(EstatsUpdateNot.ERROR_NOTIFICA).target(EstatsUpdateNot.NOTIFICA_CHECK_REINTENTS).event(EventsUpdateNot.NOTIFICA_CHECK_REINTENTS);
        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_CHECK_REINTENTS)
                    .first(EstatsUpdateNot.NOTIFICA_CHECK_REINTENTS, notificaErrorIsReintentar())
                    .last(EstatsUpdateNot.FI);

        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_A_NOTIFICA).target(EstatsUpdateNot.NOTIFICA_CHECK_TOTS_ACTUALITZATS).event(EventsUpdateNot.NOTIFICA_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_CHECK_TOTS_ACTUALITZATS)
                    .first(EstatsUpdateNot.NOTIFICA_TOTS_ACT_CHECK_TOTS_ERROR, enviadaNotificaIsTotsActualitzats())
                    .last(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_CHECK_ENV_PARCIAL);

        // NO TOTS ACTUALITZATS
        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_CHECK_ENV_PARCIAL)
                    .first(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_ENV_PARCIAL_CHECK_ESTAT_ENV, notificaNoTotsActIsEnvParcial())
                    .last(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_NO_ENV_PARCIAL_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_ENV_PARCIAL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ERROR, notificaNoTotsActualitzatsEnvParcialIsEnviadaAmbErrors())
                    .last(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA);

        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_NO_TOTS_ACT_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
                .first(EstatsUpdateNot.ENVIADA_ENVIAT_PER_NOTIFICA, notificaNoTotsActualitzatsNoEnvParcialIsTotsEstatsFinalsCorrectes())
                .last(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA);

        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ERROR).target(EstatsUpdateNot.NOTIFICA_CHECK_TOTS_ACTUALITZATS).event(EventsUpdateNot.NOTIFICA_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA).target(EstatsUpdateNot.NOTIFICA_CHECK_TOTS_ACTUALITZATS).event(EventsUpdateNot.NOTIFICA_ACTUALITZAR_ESTAT_NOTIFICA);
        transitions.withExternal().source(EstatsUpdateNot.ENVIADA_ENVIAT_PER_NOTIFICA).target(EstatsUpdateNot.NOTIFICA_CHECK_TOTS_ACTUALITZATS).event(EventsUpdateNot.NOTIFICA_ACTUALITZAR_ESTAT_NOTIFICA);

        // TOTS ACTUALITZATS

        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_TOTS_ACT_CHECK_TOTS_ERROR)
                    .first(EstatsUpdateNot.ERROR_NOTIFICA_FINAL, notificaTotsActualitzatsIsTotsError())
                    .last(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_CHECK_ENV_PARCIAL);
        transitions.withExternal().source(EstatsUpdateNot.ERROR_NOTIFICA_FINAL).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);

        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_CHECK_ENV_PARCIAL)
                    .first(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_ENV_PARCIAL_CHECK_ESTAT_ENV, notificaTotsActualitzatsNoTotsErrorIsEnvParcial())
                    .last(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_NO_ENV_PARCIAL_CHECK_ESTAT_ENV);

        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_ENV_PARCIAL_CHECK_ESTAT_ENV)
                    .first(EstatsUpdateNot.FINALITZADA_ENVIAT_PER_NOTIFICA, notificaTotsActualitzatsNoTotsErrorEnvParcialIsTotsEstatsCorrectes())
                    .last(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA);

        // TODO AL DIAGRAMA SEMBLA QUE FALTA UNA DE LES BRANQUES DE LA CONDICIO
        transitions.withChoice().source(EstatsUpdateNot.NOTIFICA_TOTS_ACT_NO_TOTS_ERROR_NO_ENV_PARCIAL_CHECK_ESTAT_ENV)
//                .first(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA)
//                .last()
                ;

        transitions.withExternal().source(EstatsUpdateNot.FINALITZADA_ENVIAT_PER_NOTIFICA).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);
        transitions.withExternal().source(EstatsUpdateNot.FINALITZADA_AMB_ERRORS_ENVIAT_PER_NOTIFICA).target(EstatsUpdateNot.FI).event(EventsUpdateNot.FINALITZAR);

    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaTotsActualitzatsNoTotsErrorEnvParcialIsTotsEstatsCorrectes() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaTotsActualitzatsNoTotsErrorIsEnvParcial() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaTotsActualitzatsIsTotsError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaNoTotsActualitzatsNoEnvParcialIsTotsEstatsFinalsCorrectes() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaNoTotsActualitzatsEnvParcialIsEnviadaAmbErrors() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaNoTotsActIsEnvParcial() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> enviadaNotificaIsTotsActualitzats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> notificaErrorIsReintentar() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> isErrorNotifica() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteTotsActNoTotsErrorNoEnvParcialOErrorsMailIsTotsEstatsFinalsCorrectes() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteTotsActNoTotsErrorIsEnviamentParcialOErrorsEmail() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteTotsActTotsErrorIsEnviamentsMailOk() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteTotsActIsTotError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteNoTotsActNoEnvParcialOErrorMailIsTotsEstatsFinalsCorrectes() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteNoTotsActEnvParcialOErrorMailIsTotErrors() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteTotsActIsEnvParcialOErrorMail() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteIsTotsActualitzats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteNoEnviadaNotifica() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteTotsEnviatsIsTotsError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteNoTotsEnviatsEnvParcialIsTotsEnviats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteNoTotsEnviatsIsEnvParcial() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> mixteIsTotsEnviats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsEmailNoTotsEnviatsNoEnvParcialIsTotsEnviats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsEmailNoTotsEnviatsIsEnvParcial() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsEmailTotsEnviatsErrorsnNoEnvParcialIsTotsEnviats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsEmailTotsEnviatsErrorsIsEnviamentParcial() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsEmailTotsEnviatsIsTotsError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsEmailIsTotEnviats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> isTotsEnvMail() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> isEnviamentsMixte() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> reintentarEnvError() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> totsRegistrats() {
        return ctx -> {
            return true;
        };
    }

    @Bean
    public Guard<EstatsUpdateNot, EventsUpdateNot> capRegistrat() {
        return ctx -> {
            return true;
        };
    }
}
