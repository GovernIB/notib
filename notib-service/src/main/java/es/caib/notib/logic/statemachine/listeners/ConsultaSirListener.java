package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsultaSirListener {

    private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_CONSULTA_SIR, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload ConsultaSirRequest consultaSirRequest,
                                   @Headers MessageHeaders headers,
                                   Message message) throws JMSException {
        var enviament = consultaSirRequest.getConsultaSirDto();
        log.debug("[SM] Rebut consulta d'estat a Sir <" + enviament + ">");

        // TODO: Consulta estat a Sir de enviament
        // ...
        boolean consultaSirSuccess = true;

        var sm = stateMachineService.acquireStateMachine(enviament.getUuid(), true);
        sm.sendEvent(MessageBuilder.withPayload(consultaSirSuccess ? EnviamentSmEvent.SR_SUCCESS : EnviamentSmEvent.SR_ERROR)
                .setHeader(SmConstants.ENVIAMENT_UUID_HEADER, enviament.getUuid())
                .build());
        message.acknowledge();

    }

}
