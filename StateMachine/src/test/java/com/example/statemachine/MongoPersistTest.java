package com.example.statemachine;


import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.UUID;

//@Ignore
public class MongoPersistTest extends BaseMongoIT {

    @Autowired
    private StateMachinePersister<EstatsComSIR, EventsComSIR, UUID> persister;
    @Autowired
    private StateMachineFactory<EstatsComSIR, EventsComSIR> stateMachineFactory;

//    @Ignore
    @Test
    public void testMongoPersist() throws Exception {
        // Arrange
        StateMachine<EstatsComSIR, EventsComSIR> firstStateMachine = stateMachineFactory.getStateMachine();
        StateMachine<EstatsComSIR, EventsComSIR> secondStateMachine = stateMachineFactory.getStateMachine();

        firstStateMachine.sendEvent(EventsComSIR.ALTA);
        firstStateMachine.sendEvent(EventsComSIR.REGISTRAR);

        // Act
        persister.persist(firstStateMachine, firstStateMachine.getUuid());
        persister.persist(secondStateMachine, secondStateMachine.getUuid());
        persister.restore(secondStateMachine, firstStateMachine.getUuid());

        // Asserts
        Assertions.assertThat(secondStateMachine.getState().getId()).isEqualTo(EstatsComSIR.INICI);

        var deployed = (boolean) secondStateMachine.getExtendedState().getVariables().get("deployed");

        Assertions.assertThat(deployed).isEqualTo(true);

        // Mongo specific asserts:
        Assertions.assertThat(mongoTemplate.getCollectionNames()).isNotEmpty();

        var documents = mongoTemplate.findAll(Document.class, "StateContext");

        Assertions.assertThat(documents).hasSize(2);
        Assertions.assertThat(documents).flatExtracting(Document::value).contains(firstStateMachine.getUuid().toString(), secondStateMachine.getUuid().toString())
                .contains(firstStateMachine.getState().getId().toString(), secondStateMachine.getState().getId().toString());
    }
}

