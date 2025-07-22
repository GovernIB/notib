package es.caib.notib.logic.callbacks;


import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.CallbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.ws.rs.NotFoundException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CallbackListener {

    private final CallbackHelper callbackHelper;
    private final JmsTemplate jmsTemplate;
    private final CallbackRepository callbackRepository;
    private final ConfigHelper configHelper;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_CALLBACKS, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentCallback(@Payload Long enviamentId, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var callback = callbackRepository.findByEnviamentId(enviamentId);
        if (callback == null) {
            log.error("[CallbackListener] Error no existeix cap callback per l'enviament " + enviamentId);
            return;
        }
        var maxIntents = configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
        if (callback.getIntents() > maxIntents) {
            return;
        }
        var delay = SmConstants.delay(callback.getIntents());
        try {
            var error = callbackHelper.notifica(enviamentId, null);
            if (error) {
               enviarCua(enviamentId, delay);
            }
        } catch (Exception ex) {
            log.error("[CallbackListener] Error no controlat al enviar el callback per l'enviament amb id " + enviamentId, ex);
            enviarCua(enviamentId, delay);
        }
    }

    private void enviarCua(Long enviamentId, Long delay) {

        jmsTemplate.convertAndSend(SmConstants.CUA_CALLBACKS, enviamentId,
            m -> {
                m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
                return m;
            });
    }
}
