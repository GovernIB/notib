package es.caib.notib.logic.config;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.statemachine.SmConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static es.caib.notib.logic.intf.statemachine.EnviamentSmEstat.*;
import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
@EnableRetry
public class StateMachineConfig extends StateMachineConfigurerAdapter<EnviamentSmEstat, EnviamentSmEvent> {

//    @Qualifier("smTaskScheduller")
    private final TaskScheduler smTaskScheduler;
    private final ConfigHelper configHelper;
    private final StateMachineRuntimePersister<EnviamentSmEstat, EnviamentSmEvent, String> stateMachineRuntimePersister;
    // Actions
    private final Action<EnviamentSmEstat, EnviamentSmEvent> enviamentRegistreAction;
//    private final Action<EnviamentSmEstat, EnviamentSmEvent> notificaAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> enviamentNotificaAction;
//    private final Action<EnviamentSmEstat, EnviamentSmEvent> enviamentEmailAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> consultaNotificaIniciPoolingAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> consultaNotificaPoolingAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> consultaNotificaAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> consultaSirIniciPoolingAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> consultaSirAction;
    private final Action<EnviamentSmEstat, EnviamentSmEvent> consultaSirPoolingAction;
    // Guards
    private final Guard<EnviamentSmEstat, EnviamentSmEvent> reintentsRegistreGuard;
    private final Guard<EnviamentSmEstat, EnviamentSmEvent> reintentsNotificaGuard;
//    private final Guard<EnviamentSmEstat, EnviamentSmEvent> reintentsEmailGuard;
    private final Guard<EnviamentSmEstat, EnviamentSmEvent> reintentsConsultaNotificaGuard;
    private final Guard<EnviamentSmEstat, EnviamentSmEvent> reintentsConsultaSirGuard;



    @Override
    public void configure(StateMachineStateConfigurer<EnviamentSmEstat, EnviamentSmEvent> states) throws Exception {
        states.withStates()
                .initial(NOU)
                .states(EnumSet.allOf(EnviamentSmEstat.class))
                .choice(REGISTRE_RETRY)
                .choice(REGISTRAT)
                .end(FI);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EnviamentSmEstat, EnviamentSmEvent> transitions) throws Exception {
        transitions
                // Registre
                .withExternal().source(NOU).target(REGISTRE_PENDENT).event(RG_ENVIAR).guard(uuidGuard()).action(enviamentRegistreAction).and()
                .withExternal().source(REGISTRE_PENDENT).target(REGISTRE_PENDENT).event(RG_ENVIAR).guard(uuidGuard()).action(enviamentRegistreAction).and()
                .withExternal().source(REGISTRE_PENDENT).target(REGISTRAT).event(RG_SUCCESS).guard(uuidGuard()).and()
                .withExternal().source(REGISTRE_PENDENT).target(REGISTRE_RETRY).event(RG_ERROR).guard(uuidGuard()).and()
                .withChoice().source(REGISTRE_RETRY)
                    .first(REGISTRE_PENDENT, reintentsRegistreGuard, enviamentRegistreAction)
                    .last(REGISTRE_ERROR).and()
                .withExternal().source(REGISTRE_ERROR).target(REGISTRE_PENDENT).event(RG_RETRY).guard(uuidGuard()).action(enviamentRegistreAction).and()
                .withChoice().source(REGISTRAT)
                    .first(SIR_PENDENT, isSir(), consultaSirIniciPoolingAction)
                    // .then(EMAIL_PENDENT, senseNif(), emailAction) --> Els emails s'envien en la notificació
                    //.last(NOTIFICA_PENDENT, notificaAction).and()
                    .last(NOTIFICA_PENDENT, enviamentNotificaAction).and()
                .withExternal().source(REGISTRE_PENDENT).target(REGISTRAT).event(RG_FORWARD).and()
                .withExternal().source(REGISTRE_ERROR).target(REGISTRAT).event(RG_FORWARD).and()
                // Enviament notifica
                .withExternal().source(NOTIFICA_PENDENT).target(NOTIFICA_PENDENT).event(NT_ENVIAR).guard(uuidGuard()).action(enviamentNotificaAction).and()
                .withExternal().source(NOTIFICA_PENDENT).target(NOTIFICA_SENT).event(NT_SUCCESS).guard(uuidGuard()).action(consultaNotificaIniciPoolingAction).and()
                .withExternal().source(NOTIFICA_PENDENT).target(NOTIFICA_RETRY).event(NT_ERROR).guard(uuidGuard()).and()
                .withChoice().source(NOTIFICA_RETRY)
                    .first(NOTIFICA_PENDENT, reintentsNotificaGuard, enviamentNotificaAction)
                    .last(NOTIFICA_ERROR).and()
                .withExternal().source(NOTIFICA_ERROR).target(NOTIFICA_PENDENT).event(NT_RETRY).guard(uuidGuard()).action(enviamentNotificaAction).and()
                .withExternal().source(NOTIFICA_PENDENT).target(NOTIFICA_SENT).event(NT_FORWARD).and()
                .withExternal().source(NOTIFICA_ERROR).target(NOTIFICA_SENT).event(NT_FORWARD).and()
                // Consulta estat
                .withExternal().source(NOTIFICA_SENT).target(NOTIFICA_SENT).event(CN_CONSULTAR).guard(uuidGuard()).action(consultaNotificaAction).and()
                .withExternal().source(NOTIFICA_SENT).target(CONSULTA_ESTAT).event(CN_SUCCESS).guard(uuidGuard()).and()
                .withChoice().source(CONSULTA_ESTAT)
                    .first(FI, isEstatFinal())
                    .last(NOTIFICA_SENT, consultaNotificaPoolingAction).and()
                .withExternal().source(NOTIFICA_SENT).target(CONSULTA_RETRY).event(CN_ERROR).guard(uuidGuard()).and()
                .withChoice().source(CONSULTA_RETRY)
                    .first(NOTIFICA_SENT, isAdviser())
                    .then(NOTIFICA_SENT, reintentsConsultaNotificaGuard, consultaNotificaAction)
                    .last(CONSULTA_ERROR).and()
                .withExternal().source(CONSULTA_ERROR).target(NOTIFICA_SENT).event(CN_RETRY).guard(uuidGuard()).action(consultaNotificaAction).and()
                .withExternal().source(NOTIFICA_SENT).target(FI).event(CN_FORWARD).and()
                .withExternal().source(CONSULTA_ERROR).target(FI).event(CN_FORWARD).and()
                // Consulta SIR
                .withExternal().source(SIR_PENDENT).target(SIR_PENDENT).event(SR_CONSULTAR).guard(uuidGuard()).action(consultaSirAction).and()
                .withExternal().source(SIR_PENDENT).target(SIR_ESTAT).event(SR_SUCCESS).guard(uuidGuard()).and()
                .withChoice().source(SIR_ESTAT)
                    .first(FI, isEstatFinal())
                    .last(SIR_PENDENT, consultaSirPoolingAction).and()
                .withExternal().source(SIR_PENDENT).target(SIR_RETRY).event(SR_ERROR).guard(uuidGuard()).and()
                .withChoice().source(SIR_RETRY)
                    .first(SIR_PENDENT, isSirCallback())
                    .then(SIR_PENDENT, reintentsConsultaSirGuard, consultaSirPoolingAction)
                    .last(SIR_ERROR).and()
                .withExternal().source(SIR_ERROR).target(SIR_PENDENT).event(SR_RETRY).guard(uuidGuard()).action(consultaSirAction).and()
                .withExternal().source(SIR_PENDENT).target(FI).event(SR_FORWARD).and()
                .withExternal().source(SIR_ERROR).target(FI).event(SR_FORWARD);

//                // Enviament email
//                .withExternal().source(EMAIL_PENDENT)   .target(EMAIL_PENDENT)          .event(EM_ENVIAR)       .guard(uuidGuard())             .action(enviamentEmailAction)         .and()
//                .withExternal().source(EMAIL_PENDENT)   .target(FI)                     .event(EM_SUCCESS)      .guard(uuidGuard())                                                   .and()
//                .withExternal().source(EMAIL_PENDENT)   .target(EMAIL_RETRY)            .event(EM_ERROR)        .guard(uuidGuard())                                                   .and()
//                .withChoice()  .source(EMAIL_RETRY)     .first(EMAIL_PENDENT,                                    reintentsEmailGuard)
//                                                        .last(EMAIL_ERROR)                                                                                                            .and()
//                .withExternal().source(EMAIL_ERROR)     .target(EMAIL_PENDENT)          .event(EM_RETRY)        .guard(uuidGuard())             .action(enviamentEmailAction)         .and()
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<EnviamentSmEstat, EnviamentSmEvent> config) throws Exception {

        StateMachineListenerAdapter<EnviamentSmEstat, EnviamentSmEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<EnviamentSmEstat, EnviamentSmEvent> from, State<EnviamentSmEstat, EnviamentSmEvent> to) {
                log.debug(String.format("[SM] Transició de %s a %s%n", from == null ? "cap" : from.getId(), to.getId()));
            }
        };

        config.withConfiguration().listener(adapter).taskScheduler(smTaskScheduler).and().withPersistence().runtimePersister(stateMachineRuntimePersister);
    }

    @Bean
    public StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService(
            StateMachineFactory<EnviamentSmEstat, EnviamentSmEvent> stateMachineFactory,
            StateMachineRuntimePersister<EnviamentSmEstat, EnviamentSmEvent, String> stateMachineRuntimePersister) {

        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }


    // Guards
    public Guard<EnviamentSmEstat, EnviamentSmEvent> isSir() {
        return ctx -> "SIR".equals(ctx.getExtendedState().getVariables().get(SmConstants.ENVIAMENT_TIPUS));
    }
//    public Guard<EnviamentSmEstat, EnviamentSmEvent> senseNif() {
//        return ctx -> (boolean) ctx.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_SENSE_NIF, false);
//    }
    public Guard<EnviamentSmEstat, EnviamentSmEvent> isEstatFinal() {
        return ctx -> (boolean) ctx.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_ESTAT_FINAL, false);
    }
    public Guard<EnviamentSmEstat, EnviamentSmEvent> isAdviser() {
        return ctx -> configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu", false);
    }
    public Guard<EnviamentSmEstat, EnviamentSmEvent> isSirCallback() {
        // TODO: Modificar si s'afegeix un callback de SIR
//        return ctx -> configHelper.getConfigAsBoolean("es.caib.notib.callback.sir.actiu", false);
        return ctx -> false;
    }

    public Guard<EnviamentSmEstat, EnviamentSmEvent> uuidGuard() {
        return ctx -> ctx.getMessageHeader(SmConstants.ENVIAMENT_UUID_HEADER) != null;
    }

}
