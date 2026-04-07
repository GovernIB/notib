package es.caib.notib.logic.email;

import es.caib.notib.logic.helper.EmailNotificacioHelper;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
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
public class EmailListener {

    private final EmailNotificacioHelper emailNotificacioHelper;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;

    @Transactional
    @JmsListener(destination = EmailConstants.CUA_EMAIL_NOTIFICACIO, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveMessage(@Payload Long enviamentId, @Headers MessageHeaders headers, Message message) throws JMSException {

        message.acknowledge();
        try {
            var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
            emailNotificacioHelper.prepararEnvioEmailNotificacio(enviament);
        } catch (Exception ex) {
            log.error("Error enviant els emails per l'enviament " + enviamentId, ex);
        }
    }
}
