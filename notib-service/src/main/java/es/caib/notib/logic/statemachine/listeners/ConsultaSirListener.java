package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.Semaphore;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsultaSirListener {


    private final RegistreService registreService;
    private Semaphore semaphore = new Semaphore(5);

    @JmsListener(destination = SmConstants.CUA_CONSULTA_SIR, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload ConsultaSirRequest consultaSirRequest, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        var enviament = consultaSirRequest.getConsultaSirDto();
        if (enviament != null && enviament.getUuid() != null) {
            log.debug("[SM] Rebut consulta d'estat a Sir <" + enviament.getUuid() + ">");
        } else {
            log.error("[SM] Rebuda consulta d'estat a Sir sense Enviament");
        }
        semaphore.acquire();
        try {
            registreService.consultaSir(enviament);
        } finally {
            semaphore.release();
        }
        message.acknowledge();

    }

}
