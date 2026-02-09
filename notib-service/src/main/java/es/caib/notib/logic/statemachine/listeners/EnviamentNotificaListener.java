package es.caib.notib.logic.statemachine.listeners;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.service.NotificaService;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
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
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private Semaphore semaphore = new Semaphore(5);

    @JmsListener(destination = SmConstants.CUA_NOTIFICA, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentNotifica(@Payload EnviamentNotificaRequest enviamentNotificaRequest, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var enviamentUuid = enviamentNotificaRequest.getEnviamentUuid();
        if (Strings.isNullOrEmpty(enviamentUuid)) {
            log.error("[SM] Rebut enviament notifica sense Enviament UUID");
            return;
        }
        NotibLogger.getInstance().info("[SM] Rebut enviament a notifica <" + enviamentUuid + ">", log, LoggingTipus.STATE_MACHINE);
        var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElse(null);
        if (enviamentEntity == null) {
            log.error("[SM] No s'ha trobat l'enviament amb UUID " + enviamentUuid);
            return;
        }
        if (enviamentEntity.getNotificacio().isDeleted()) {
            NotibLogger.getInstance().info("[SM] Petició de notificació NO enviada. Enviament marcat com a deleted - UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
            return;
        }

        semaphore.acquire();
        try {
            notificaService.enviarNotifica(enviamentUuid, enviamentNotificaRequest);
            notificaService.enviarEvents(enviamentUuid, enviamentNotificaRequest.getCodiUsuari());
        } finally {
            semaphore.release();
        }
    }

}
