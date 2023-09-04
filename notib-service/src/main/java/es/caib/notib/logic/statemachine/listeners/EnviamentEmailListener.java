package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentEmailRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.Semaphore;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnviamentEmailListener {

    private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

    private Semaphore semaphore = new Semaphore(5);

    @Transactional
    @JmsListener(destination = SmConstants.CUA_EMAIL, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentEmail(@Payload EnviamentEmailRequest enviamentEmailRequest,
                                         @Headers MessageHeaders headers,
                                         Message message) throws JMSException, InterruptedException {
        // Actualment els enviaments de avisos de notificacions per Email es realitzen des de la funcionalitat de norificar
        // per tant no s'utilitza aquest listener

        var enviament = enviamentEmailRequest.getEnviamentEmailDto();
        log.error("[SM] Rebut enviament per email <" + enviament + ">");

        semaphore.acquire();
        try {

//        boolean emailSuccess = true;
//
//        var sm = stateMachineService.acquireStateMachine(enviament.getUuid(), true);
//        sm.sendEvent(MessageBuilder.withPayload(emailSuccess ? EnviamentSmEvent.EM_SUCCESS : EnviamentSmEvent.EM_ERROR)
//                .setHeader(SmConstants.ENVIAMENT_UUID_HEADER, enviament.getUuid())
//                .build());
        } finally {
            semaphore.release();
        }
        message.acknowledge();

    }

}