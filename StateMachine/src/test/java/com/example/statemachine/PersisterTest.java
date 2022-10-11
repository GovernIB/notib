package com.example.statemachine;

import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.service.StateMachineService;

import java.util.UUID;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PersisterTest {

    @Autowired
    private StateMachineFactory<EstatsComSIR, EventsComSIR> stateMachineFactory;
//    @Autowired
//    StateMachine<EstatsComSIR, EventsComSIR> firstStateMachine;
//    @Autowired
//    StateMachine<EstatsComSIR, EventsComSIR> secondStateMachine;

    @Autowired
    private StateMachinePersister<EstatsComSIR, EventsComSIR, UUID> persister;
    @Autowired
    private StateMachineService<EstatsComSIR, EventsComSIR> stateMachineService;

    @Test
    public void jpaPersist() throws Exception {

        stateMachineService.releaseStateMachine("foo");
    }

    @Test
    public void testPersist() throws Exception {

//        // Arrange
        StateMachine<EstatsComSIR, EventsComSIR> firstStateMachine = stateMachineFactory.getStateMachine();
        StateMachine<EstatsComSIR, EventsComSIR> secondStateMachine = stateMachineFactory.getStateMachine();

        firstStateMachine.sendEvent(EventsComSIR.ALTA);
        firstStateMachine.sendEvent(EventsComSIR.REGISTRAR);

        // Precondition
        Assertions.assertThat(secondStateMachine.getState().getId()).isEqualTo(EstatsComSIR.INICI);

        // Act
        persister.persist(firstStateMachine, firstStateMachine.getUuid());
        persister.restore(secondStateMachine, firstStateMachine.getUuid());

        // Asserts
        Assertions.assertThat(secondStateMachine.getState().getId()).isEqualTo(EstatsComSIR.ENVIAT_SIR);
    }
}
