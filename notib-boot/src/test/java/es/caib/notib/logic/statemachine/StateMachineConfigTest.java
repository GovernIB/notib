package es.caib.notib.logic.statemachine;

import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.RG_ENVIAR;
import static es.caib.notib.logic.intf.statemachine.EnviamentSmEvent.RG_SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@Disabled
@SpringBootTest
@ActiveProfiles({"test"})
@Transactional
class StateMachineConfigTest {

//    @Autowired
//    StateMachineFactory<EnviamentSmEstat, EnviamentSmEvent> factory;
    @Autowired
    private StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;

    @MockBean
    private RegistreSmHelper registreSmHelper;

    @Test
    void testNewStateMachine() throws Exception {

        Mockito.when(registreSmHelper.registrarEnviament(any(NotificacioEnviamentEntity.class), anyInt())).thenAnswer( input -> {
            var enviament = (NotificacioEnviamentEntity) input.getArgument(0);
            enviament.updateRegistreEstat(NotificacioRegistreEstatEnumDto.VALID, new Date(), null, null, "0000/2023", "Motiu test");
            return enviament;
        });
        
//        StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm = factory.getStateMachine(UUID.randomUUID());
//        sm.start();

        String uuid = UUID.randomUUID().toString();
        StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm = stateMachineService.acquireStateMachine(uuid, true);

        System.out.println(sm.getState().toString());
        Assert.assertEquals(EnviamentSmEstat.NOU, sm.getState().getId());

        sm.sendEvent(MessageBuilder.withPayload(RG_ENVIAR).setHeader(SmConstants.ENVIAMENT_UUID_HEADER, uuid).build());
        System.out.println(sm.getState().toString());
        Assert.assertEquals(EnviamentSmEstat.REGISTRE_PENDENT, sm.getState().getId());

//        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EV_REGISTRAR).build()))
//                .doOnComplete(() -> { System.out.println(sm.getState().toString()); })
//                .subscribe();
        sm.sendEvent(MessageBuilder.withPayload(RG_SUCCESS).setHeader(SmConstants.ENVIAMENT_UUID_HEADER, uuid).build());
        System.out.println(sm.getState().toString());

        stateMachineService.releaseStateMachine(sm.getId());
//        persister.persist(sm, UUID.randomUUID());

        Assert.assertEquals(EnviamentSmEstat.REGISTRAT, sm.getState().getId());

        sm.stop();
    }
}
