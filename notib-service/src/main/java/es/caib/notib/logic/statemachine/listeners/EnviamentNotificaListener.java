package es.caib.notib.logic.statemachine.listeners;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.service.NotificaService;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
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
public class EnviamentNotificaListener {

    private final NotificaService notificaService;
    private Semaphore semaphore = new Semaphore(5);

    @JmsListener(destination = SmConstants.CUA_NOTIFICA, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentNotifica(@Payload EnviamentNotificaRequest enviamentNotificaRequest, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var enviament = enviamentNotificaRequest.getEnviamentNotificaDto();
        if (enviament == null || Strings.isNullOrEmpty(enviament.getUuid())) {
            log.error("[SM] Rebut enviament notifica sense Enviament");
            return;
        }
        NotibLogger.getInstance().info("[SM] Rebut enviament a notifica <" + enviament.getUuid() + ">", log, LoggingTipus.STATE_MACHINE);
        if (enviament.isDeleted()) {
            NotibLogger.getInstance().info("[SM] Petició de notificació NO enviada. Enviament marcat com a deleted - UUID " + enviament.getUuid(), log, LoggingTipus.STATE_MACHINE);
            return;
        }

        semaphore.acquire();
        try {
            notificaService.enviarNotifica(enviament.getUuid(), enviamentNotificaRequest);
            notificaService.enviarEvents(enviament.getUuid(), enviamentNotificaRequest.getCodiUsuari());
        } finally {
            semaphore.release();
        }
    }

}
