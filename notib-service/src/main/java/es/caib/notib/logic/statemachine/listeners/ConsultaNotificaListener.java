package es.caib.notib.logic.statemachine.listeners;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificaService;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
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
public class ConsultaNotificaListener {


    private final NotificaService notificaService;
    private final EnviamentSmService enviamentSmService;
    private Semaphore semaphore = new Semaphore(5);


    @JmsListener(destination = SmConstants.CUA_CONSULTA_ESTAT, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentRegistre(@Payload ConsultaNotificaRequest consultaNotificaRequest, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        var enviament = consultaNotificaRequest.getConsultaNotificaDto();
        if (enviament != null && Strings.isNullOrEmpty(enviament.getUuid())) {
            log.error("[SM] Rebuda consulta d'estat a notifica sense Enviament");
            message.acknowledge();
            return;
        }
        NotibLogger.getInstance().info("[SM] Rebut consulta d'estat a notifica <" + enviament.getUuid() + ">", log, LoggingTipus.STATE_MACHINE);
        semaphore.acquire();
        try {
            var success = notificaService.consultaEstatEnviament(enviament);
            if (success) {
                enviamentSmService.consultaSuccess(enviament.getUuid());
            } else {
                enviamentSmService.consultaFailed(enviament.getUuid());
            }
        } finally {
            semaphore.release();
        }
        message.acknowledge();

    }

}
