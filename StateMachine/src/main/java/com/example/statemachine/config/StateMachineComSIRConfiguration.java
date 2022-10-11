package com.example.statemachine.config;

import com.example.statemachine.InMemoryPersist;
import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import java.util.EnumSet;
import java.util.UUID;

@Configuration
//@EnableStateMachine
@EnableStateMachineFactory(name = "COM_SIR")
public class StateMachineComSIRConfiguration extends StateMachineConfigurerAdapter<EstatsComSIR, EventsComSIR> {

    @Override
    public void configure(StateMachineStateConfigurer<EstatsComSIR, EventsComSIR> states) throws Exception {

        states.withStates().initial(EstatsComSIR.INICI)
                .state(EstatsComSIR.INICIAT)
                .choice(EstatsComSIR.REGISTRAR)
                .state(EstatsComSIR.ERROR_REGISTRE)
                .choice(EstatsComSIR.REINTENTAR_REGISTRE)
                .state(EstatsComSIR.ENVIAT_SIR)
                .choice(EstatsComSIR.CONSULTA_ESTAT_SIR)
                .state(EstatsComSIR.CONSULTA_ESTAT_SIR_ERROR)
                .choice(EstatsComSIR.ERROR_ACTUALITZAR)
                .state(EstatsComSIR.CONSULTA_ESTAT_SIR_OK)
                .state(EstatsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR)
                .choice(EstatsComSIR.CHECK_ESTAT_SIR)
                .end(EstatsComSIR.FI)
                .states(EnumSet.allOf(EstatsComSIR.class));
    }

    @Autowired
    private StateMachineRuntimePersister<EstatsComSIR, EventsComSIR, String> stateMachineRuntimePersister;

    @Override
    public void configure(StateMachineConfigurationConfigurer<EstatsComSIR, EventsComSIR> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(new StateMachineListener());
        config.withPersistence().runtimePersister(stateMachineRuntimePersister);
    }

    @Bean
    public StateMachineService<EstatsComSIR, EventsComSIR> stateMachineService(StateMachineFactory<EstatsComSIR, EventsComSIR> stateMachineFactory, StateMachineRuntimePersister<EstatsComSIR, EventsComSIR, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }
//
//    @Bean
//    public StateMachineService<EstatsComSIR, EventsComSIR> stateMachineService(
//            final StateMachineFactory<EstatsComSIR, EventsComSIR> stateMachineFactory,
//            final StateMachinePersist<EstatsComSIR, EventsComSIR, String> stateMachinePersist) {
//        return new DefaultStateMachineService<>(stateMachineFactory, stateMachinePersist);
//    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EstatsComSIR, EventsComSIR> transitions) throws Exception {

        transitions.withExternal().source(EstatsComSIR.INICI).target(EstatsComSIR.INICIAT).event(EventsComSIR.ALTA)
                .and().withExternal().source(EstatsComSIR.INICIAT).target(EstatsComSIR.REGISTRAR).event(EventsComSIR.REGISTRAR)
                .and().withChoice().source(EstatsComSIR.REGISTRAR)
                        .first(EstatsComSIR.ENVIAT_SIR, totsRegistratsSIR())
                        .then(EstatsComSIR.ERROR_REGISTRE, errorRegistreSIR())
                        .last(EstatsComSIR.FI);
        transitions.withExternal().source(EstatsComSIR.ERROR_REGISTRE).target(EstatsComSIR.REINTENTAR_REGISTRE).event(EventsComSIR.REINTENTAR_REGISTRE);
        transitions.withChoice().source(EstatsComSIR.REINTENTAR_REGISTRE).first(EstatsComSIR.REGISTRAR, reintentarRegistre()).last(EstatsComSIR.FI);

        transitions.withExternal().source(EstatsComSIR.ENVIAT_SIR).target(EstatsComSIR.CONSULTA_ESTAT_SIR).event(EventsComSIR.CONSULTA_ESTAT_SIR);
        transitions.withChoice().source(EstatsComSIR.CONSULTA_ESTAT_SIR)
                .first(EstatsComSIR.CONSULTA_ESTAT_SIR_OK, okConsultaEstatSIR())
                .last(EstatsComSIR.CONSULTA_ESTAT_SIR_ERROR);
        transitions.withExternal().source(EstatsComSIR.CONSULTA_ESTAT_SIR_ERROR).target(EstatsComSIR.ERROR_ACTUALITZAR);
        transitions.withChoice().source(EstatsComSIR.ERROR_ACTUALITZAR)
                    .first(EstatsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR, reintentarConsultaEstatSIR())
                    .last(EstatsComSIR.FI);
        transitions.withExternal().source(EstatsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR).target(EstatsComSIR.CONSULTA_ESTAT_SIR).event(EventsComSIR.REINTENTAR_CONSULTA_ESTAT_SIR);
        transitions.withExternal().source(EstatsComSIR.CONSULTA_ESTAT_SIR_OK).target(EstatsComSIR.CHECK_ESTAT_SIR).event(EventsComSIR.CHECK_ESTAT_SIR);
        transitions.withChoice().source(EstatsComSIR.CHECK_ESTAT_SIR)
                    .first(EstatsComSIR.OFICI_ACCEPTAT, isOficiAcceptat())
                    .then(EstatsComSIR.REBUTJAT, isRebutjat())
                    .then(EstatsComSIR.OFICI_SIR, isOficiSir())
                    .then(EstatsComSIR.REENVIAR_CONSULTA_ESTAT_SIR, isReenviarConsultaEstatSir())
                    .last(EstatsComSIR.FI);
        transitions.withExternal().source(EstatsComSIR.OFICI_ACCEPTAT).target(EstatsComSIR.FI);
        transitions.withExternal().source(EstatsComSIR.REBUTJAT).target(EstatsComSIR.FI);
        transitions.withExternal().source(EstatsComSIR.OFICI_SIR).target(EstatsComSIR.CONSULTA_ESTAT_SIR);
        transitions.withExternal().source(EstatsComSIR.REENVIAR_CONSULTA_ESTAT_SIR).target(EstatsComSIR.CONSULTA_ESTAT_SIR).event(EventsComSIR.REENVIAR_CONSULTA_ESTAT_SIR);
    }
//
//    @Bean
//    public StateMachineRuntimePersister<EstatsComSIR, EventsComSIR, UUID> mongoPersist(MongoDbStateMachineRepository mongoRepository) {
//
//        return new MongoDbPersistingStateMachineInterceptor<>(mongoRepository);
//    }




//    @Bean
//    public StateMachinePersist<EstatsComSIR, EventsComSIR, UUID> inMemoryPersist() {
//        return new InMemoryPersist();
//    }

//    @Bean
//    public StateMachinePersister<EstatsComSIR, EventsComSIR, UUID> persister(StateMachinePersist<EstatsComSIR, EventsComSIR, UUID> defaultPersist) {
//        return new DefaultStateMachinePersister<>(defaultPersist);
//    }

    @Bean
    public Action<String, String> initAction() {
        return ctx -> System.out.println("Init " + ctx.getTarget().getId());
    }

    @Bean
    public Action<String, String> entryAction() {
        return ctx -> System.out.println("Entry " + ctx.getTarget().getId());
    }

    @Bean
    public Action<String, String> executeAction() {
        return ctx -> {
            System.out.println("Do " + ctx.getTarget().getId());
            var approvals = (int) ctx.getExtendedState().getVariables().getOrDefault("approvalCount", 0);
            approvals++;
            System.out.println("approvals: " + approvals);
            ctx.getExtendedState().getVariables().put("approvalCount", approvals);
        };
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> reintentarRegistre() {
        return ctx -> true;
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> errorRegistreSIR() {
        return ctx -> {
            var errorRegistre = (int) ctx.getExtendedState().getVariables().getOrDefault("errorRegistre", 0);
            errorRegistre++;
            System.out.println("errorRegistre: " + errorRegistre);
            ctx.getExtendedState().getVariables().put("errorRegistre", errorRegistre);
            return errorRegistre <= 4;
        };
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> totsRegistratsSIR() {
        return ctx -> {
            var errorRegistre = (int) ctx.getExtendedState().getVariables().getOrDefault("errorRegistre", 0);
            return true;
//            return errorRegistre > 3;
        };
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> reintentarConsultaEstatSIR() {
        return ctx -> {
            var ok = (int) ctx.getExtendedState().getVariables().getOrDefault("ok", 0);
            System.out.println("reintentar consulta estat sir: " + ok);
            return ok < 5;
        };
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> okConsultaEstatSIR() {
        return ctx -> {
            var ok = (int) ctx.getExtendedState().getVariables().getOrDefault("ok", 0);
            ok++;
            System.out.println("ok: " + ok);
            var consultaOk = ok == 4;
            ok = !consultaOk ? ok : 0;
            ctx.getExtendedState().getVariables().put("ok", ok);
            return consultaOk;
        };
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> isOficiAcceptat() {
        return ctx -> false;
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> isRebutjat() {
        return ctx -> false;
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> isOficiSir() {
        return ctx -> {
            var ofici = (int)ctx.getExtendedState().getVariables().getOrDefault("ofici", 0);
            ofici++;
            ctx.getExtendedState().getVariables().put("ofici", 0);
            return ofici <= 1;
        };
    }

    @Bean
    public Guard<EstatsComSIR, EventsComSIR> isReenviarConsultaEstatSir() {
        return ctx -> {
            var reenviat = (int)ctx.getExtendedState().getVariables().getOrDefault("reenviat", 0);
            reenviat++;
            ctx.getExtendedState().getVariables().put("reenviat", 0);
            return reenviat <= 1;
        };
    }

    @Bean
    public Action<String, String> errorAction() {
        return ctx -> System.out.println("Error " + ctx.getSource().getId() + " - " + ctx.getException());
    }

    @Bean
    public Action<String, String> exitAction() {
        return ctx -> System.out.println("Exit " + ctx.getSource().getId() + " -> " + ctx.getTarget().getId());
    }


}
