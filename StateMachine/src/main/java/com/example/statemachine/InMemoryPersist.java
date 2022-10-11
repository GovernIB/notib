package com.example.statemachine;


import com.example.statemachine.enums.EstatsComSIR;
import com.example.statemachine.enums.EventsComSIR;
import com.example.statemachine.repositoris.MongoStateMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;
import java.util.UUID;

public class InMemoryPersist implements StateMachinePersist<EstatsComSIR, EventsComSIR, UUID> {

    private HashMap<UUID, StateMachineContext<EstatsComSIR, EventsComSIR>> storage = new HashMap<>();
    @Autowired
    private MongoStateMachineRepository mongo;

    @Override
    public void write(StateMachineContext<EstatsComSIR, EventsComSIR> context, UUID contextObj) throws Exception {
        storage.put(contextObj, context);
//        mongo.save(context);
    }

    @Override
    public StateMachineContext<EstatsComSIR, EventsComSIR> read(UUID contextObj) throws Exception {
        return storage.get(contextObj);
    }
}

