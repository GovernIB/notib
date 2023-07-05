package es.caib.notib.logic.statemachine;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.service.StateMachineService;

import java.util.UUID;

import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.RG_ENVIAR;
import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.RG_SUCCESS;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<EnviamentSmEstat, EnviamentSmEvent> factory;
//    @Autowired
//    private StateMachinePersister<EnviamentSmEstat, EnviamentSmEvent, UUID> persister;
    @Autowired
    private StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

    @Test
    void testNewStateMachine() throws Exception {

//        StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm = factory.getStateMachine(UUID.randomUUID());
//        sm.start();

        StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm = stateMachineService.acquireStateMachine(UUID.randomUUID().toString(), true);

        System.out.println(sm.getState().toString());
        Assert.assertEquals(EnviamentSmEstat.NOU, sm.getState().getId());

        sm.sendEvent(RG_ENVIAR);
        System.out.println(sm.getState().toString());
        Assert.assertEquals(EnviamentSmEstat.REGISTRE_PENDENT, sm.getState().getId());

//        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EV_REGISTRAR).build()))
//                .doOnComplete(() -> { System.out.println(sm.getState().toString()); })
//                .subscribe();
        sm.sendEvent(RG_SUCCESS);
        System.out.println(sm.getState().toString());

        stateMachineService.releaseStateMachine(sm.getId());
//        persister.persist(sm, UUID.randomUUID());

        Assert.assertEquals(EnviamentSmEstat.REGISTRAT, sm.getState().getId());

        sm.stop();
    }
}
