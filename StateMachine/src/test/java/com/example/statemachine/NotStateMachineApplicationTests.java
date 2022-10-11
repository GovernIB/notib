package com.example.statemachine;

import com.example.statemachine.enums.EstatsNot;
import com.example.statemachine.enums.EventsNot;
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
public class NotStateMachineApplicationTests {

    @Autowired
    private StateMachineFactory<EstatsNot, EventsNot> stateMachineFactory;
    private StateMachine<EstatsNot, EventsNot> stateMachine;

    @BeforeEach
    public void setUp() {
        stateMachine = stateMachineFactory.getStateMachine(NomStateMachine.NOT.name());
    }

    @Test
    void contextLoads() {
    }


    @Test
    void stateMachine_init_ok() {
        assertNotNull(stateMachine);
        assertEquals(stateMachine.getState().getId(), EstatsNot.INICI);
    }

    @Test
    public void testEstatInici() throws Exception {

        var plan = getBuilder().step().expectState(EstatsNot.INICI).and().build();
        plan.test();
    }

    @Test
    public void testEstatIniciat() throws Exception {

        var plan = getPendentBuilder(getBuilder()).and().build();
        plan.test();
    }

    @Test
    public void testRegistrat() throws Exception {

        var plan = StateMachineTestPlanBuilder.<EstatsNot, EventsNot>builder().defaultAwaitTime(2).stateMachine(stateMachine)
                .step().sendEvent(EventsNot.ALTA).expectState(EstatsNot.PENDENT)
                .and().step().sendEvent(EventsNot.REGISTRAR).expectTransition(1).expectState(EstatsNot.REGISTRAT)
                .and().build();
        plan.test();
    }

    private StateMachineTestPlanBuilder<EstatsNot, EventsNot> getBuilder () {
        return StateMachineTestPlanBuilder.<EstatsNot, EventsNot>builder().defaultAwaitTime(2).stateMachine(stateMachine);
    }

    private StateMachineTestPlanBuilder.StateMachineTestPlanStepBuilder getPendentBuilder(StateMachineTestPlanBuilder<EstatsNot, EventsNot>  builder) {
        return builder.step().sendEvent(EventsNot.ALTA).expectTransition(1).expectState(EstatsNot.PENDENT);
    }
}
