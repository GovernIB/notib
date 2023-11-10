package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.statemachine.SmConstants;
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
public class PoolingNotificaListener {

    private final EnviamentSmService enviamentSmService;
    private final ConfigHelper configHelper;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_POOLING_ESTAT, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload String enviamentUuid, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        enviamentSmService.enviamentConsulta(enviamentUuid);
        log.debug("[SM] Iniciat pooling de consulta d'estat a Notifica de l'enviament amb UUID " + enviamentUuid);
        message.acknowledge();

    }

    public boolean isAdviserActiu() {
        return configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu");
    }

}
