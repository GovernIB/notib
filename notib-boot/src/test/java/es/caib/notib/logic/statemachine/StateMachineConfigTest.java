package es.caib.notib.logic.statemachine;

import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;

import java.util.Date;
import java.util.UUID;

import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.RG_ENVIAR;
import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.RG_SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
class StateMachineConfigTest {

//    @Autowired
//    StateMachineFactory<EnviamentSmEstat, EnviamentSmEvent> factory;
    @Autowired
    private StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;

    @Autowired
    private RegistreSmHelper registreSmHelper;

    @Test
    void testNewStateMachine() throws Exception {

        Mockito.when(registreSmHelper.registrarEnviament(any(NotificacioEnviamentEntity.class), anyInt())).thenAnswer( i -> {
            var enviament = (NotificacioEnviamentEntity) i.getArgument(0);
            enviament.updateRegistreEstat(NotificacioRegistreEstatEnumDto.VALID, new Date(), null, null, "0000/2023");
            return enviament;
        });
        
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
