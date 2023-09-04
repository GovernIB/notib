package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
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

//    private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;
    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final NotificacioService notificacioService;

    private Semaphore semaphore = new Semaphore(5);

    @Transactional
    @JmsListener(destination = SmConstants.CUA_CONSULTA_SIR, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload ConsultaSirRequest consultaSirRequest, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        var enviament = consultaSirRequest.getConsultaSirDto();
        log.debug("[SM] Rebut consulta d'estat a Sir <" + enviament + ">");
        semaphore.acquire();
        try {
            // Consultar enviament a SIR
            notificacioService.enviamentRefrescarEstatRegistre(enviament.getId());
            var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviament.getUuid()).orElseThrow();
            boolean consultaSuccess = enviamentEntity.getSirConsultaIntent() == 0;
//            TEST
//            var consultaSuccess = new Random().nextBoolean();

            if (consultaSuccess) {
                enviamentSmService.sirSuccess(enviament.getUuid());
            } else {
                enviamentSmService.sirFailed(enviament.getUuid());
            }
        } catch (Exception ex) {
            enviamentSmService.sirFailed(enviament.getUuid());
        } finally {
            semaphore.release();
        }
        message.acknowledge();

    }

}
