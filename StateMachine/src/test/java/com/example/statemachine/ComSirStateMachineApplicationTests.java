package com.example.statemachine;

import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ComSirStateMachineApplicationTests {

    @Autowired
    private StateMachineFactory<EstatsComSIR, EventsComSIR> stateMachineFactory;
    private StateMachine<EstatsComSIR, EventsComSIR> stateMachine;

    @BeforeEach
    public void setUp() {
        stateMachine = stateMachineFactory.getStateMachine(NomStateMachine.COM_SIR.name());
    }

    @Test
    void contextLoads() {
    }

    @Test
    void stateMachine_init_ok() {

        assertNotNull(stateMachine);
        assertEquals(stateMachine.getState().getId(), EstatsComSIR.INICI);
    }

    @Test
    public void testEstatInici() throws Exception {

        var plan = getBuilder().step().expectState(EstatsComSIR.INICI).and().build();
        plan.test();
    }

    @Test
    public void testEstatIniciat() throws Exception {

        var plan = getIniciatBuilder(getBuilder()).and().build();
        plan.test();
    }

    @Test
    public void testEstatEnviatSir() throws Exception {

//        var plan = getEnviatSirtBuilder(getIniciatBuilder(getBuilder())).and().build();
//        var error = (int) stateMachine.getExtendedState().getVariables().get("errorRegistre");
//        error = 4;
//        stateMachine.getExtendedState().getVariables().put("errorRegsitre", error);
        var plan = StateMachineTestPlanBuilder.<EstatsComSIR, EventsComSIR>builder().defaultAwaitTime(2).stateMachine(stateMachine)
                .step().sendEvent(EventsComSIR.ALTA).expectState(EstatsComSIR.INICIAT)
                .and().step().sendEvent(EventsComSIR.REGISTRAR).expectTransition(1).expectState(EstatsComSIR.ENVIAT_SIR)
                .and().build();
        plan.test();
    }

    private StateMachineTestPlanBuilder<EstatsComSIR, EventsComSIR> getBuilder () {
        return StateMachineTestPlanBuilder.<EstatsComSIR, EventsComSIR>builder().defaultAwaitTime(2).stateMachine(stateMachine);
    }

    private StateMachineTestPlanBuilder.StateMachineTestPlanStepBuilder getIniciatBuilder(StateMachineTestPlanBuilder<EstatsComSIR, EventsComSIR>  builder) {
        return builder.step().sendEvent(EventsComSIR.ALTA).expectTransition(1).expectState(EstatsComSIR.INICIAT);
    }

    private StateMachineTestPlanBuilder.StateMachineTestPlanStepBuilder getEnviatSirtBuilder(StateMachineTestPlanBuilder.StateMachineTestPlanStepBuilder step) {
        return step.and().step().sendEvent(EventsComSIR.REGISTRAR).expectTransition(1).expectState(EstatsComSIR.ENVIAT_SIR);
    }

}
