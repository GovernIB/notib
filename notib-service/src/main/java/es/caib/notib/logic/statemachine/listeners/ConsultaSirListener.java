package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.Semaphore;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsultaSirListener {


    private final RegistreService registreService;
    private final EnviamentSmService enviamentSmService;

    private Semaphore semaphore = new Semaphore(5);

    @JmsListener(destination = SmConstants.CUA_CONSULTA_SIR, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload ConsultaSirRequest consultaSirRequest, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var enviament = consultaSirRequest.getConsultaSirDto();
        if (enviament == null || enviament.getUuid() == null) {
            log.error("[SM] Rebuda consulta d'estat a Sir sense Enviament");
            return;
        }
        NotibLogger.getInstance().info("[SM] Rebut consulta d'estat a Sir <" + enviament.getUuid() + ">", log, LoggingTipus.STATE_MACHINE);
        if (enviament.isDeleted()) {
            NotibLogger.getInstance().info("[SM] Petició de notificació NO enviada. Enviament marcat com a deleted - UUID " + enviament.getUuid(), log, LoggingTipus.STATE_MACHINE);
            return;
        }
        semaphore.acquire();
        try {
            var success = registreService.consultaSir(enviament);
            if (success) {
                enviamentSmService.sirSuccess(enviament.getUuid());
            } else {
                enviamentSmService.sirFailed(enviament.getUuid());
            }
        } finally {
            semaphore.release();
        }

    }

}
