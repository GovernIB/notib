package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsultaNotificaListener {

//    private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;
    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final NotificacioService notificacioService;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_CONSULTA_ESTAT, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentRegistre(@Payload ConsultaNotificaRequest consultaNotificaRequest,
                                         @Headers MessageHeaders headers,
                                         Message message) throws JMSException {
        var enviament = consultaNotificaRequest.getConsultaNotificaDto();
        log.debug("[SM] Rebut consulta estat a notifica <" + enviament + ">");

        // Consultar enviament a notifica
        notificacioService.enviamentRefrescarEstat(enviament.getId());
        var enviamentEntity = notificacioEnviamentRepository.findByUuid(enviament.getUuid()).orElseThrow();
        boolean consultaSuccess = enviamentEntity.getNotificaIntentNum() == 0;

        if (consultaSuccess) {
            enviamentSmService.consultaSuccess(enviament.getUuid());
        } else {
            enviamentSmService.consultaFailed(enviament.getUuid());
        }
        message.acknowledge();

    }

}
