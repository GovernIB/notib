package com.example.statemachine;

import com.example.statemachine.enums.EstatsUpdateComSir;
import com.example.statemachine.enums.EventsUpdateComSir;
import com.example.statemachine.enums.NomStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UpdateComSirStateMachineApplicationTests {

    @Autowired
    private StateMachineFactory<EstatsUpdateComSir, EventsUpdateComSir> stateMachineFactory;
    private StateMachine<EstatsUpdateComSir, EventsUpdateComSir> stateMachine;

    @BeforeEach
    public void setUp() {
        stateMachine = stateMachineFactory.getStateMachine(NomStateMachine.UPDATE_COM_SIR.name());
    }

    @Test
    void contextLoads() {
    }


    @Test
    void stateMachine_init_ok() {
        assertNotNull(stateMachine);
        assertEquals(stateMachine.getState().getId(), EstatsUpdateComSir.INICI);
    }

    @Test
    public void testEstatInici() throws Exception {

        var plan = getBuilder().step().expectState(EstatsUpdateComSir.INICI).and().build();
        plan.test();
    }

    @Test
    public void testEstatIniciat() throws Exception {

        var plan = getPendentBuilder(getBuilder()).and().build();
        plan.test();
    }

    @Test
    public void testEnviadaSir() throws Exception {

        var plan = StateMachineTestPlanBuilder.<EstatsUpdateComSir, EventsUpdateComSir>builder().defaultAwaitTime(2).stateMachine(stateMachine)
                .step().sendEvent(EventsUpdateComSir.ALTA).expectState(EstatsUpdateComSir.PENDENT)
                .and().step().sendEvent(EventsUpdateComSir.REGISTRAR_ENV_SIR).expectTransition(1).expectState(EstatsUpdateComSir.ENVIADA_SIR)
                .and().build();
        plan.test();
    }

    private StateMachineTestPlanBuilder<EstatsUpdateComSir, EventsUpdateComSir> getBuilder () {
        return StateMachineTestPlanBuilder.<EstatsUpdateComSir, EventsUpdateComSir>builder().defaultAwaitTime(2).stateMachine(stateMachine);
    }

    private StateMachineTestPlanBuilder.StateMachineTestPlanStepBuilder getPendentBuilder(StateMachineTestPlanBuilder<EstatsUpdateComSir, EventsUpdateComSir>  builder) {
        return builder.step().sendEvent(EventsUpdateComSir.ALTA).expectTransition(1).expectState(EstatsUpdateComSir.PENDENT);
    }
}
