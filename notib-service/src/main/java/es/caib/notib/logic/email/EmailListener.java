package es.caib.notib.logic.email;

import es.caib.notib.logic.helper.EmailNotificacioHelper;
import es.caib.notib.logic.intf.dto.EmailAgrupat;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

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
            emailNotificacioHelper.prepararEnvioEmailNotificacio(enviament, false);
        } catch (Exception ex) {
            log.error("Error enviant els emails per l'enviament " + enviamentId, ex);
        }
    }

    @Transactional
    @JmsListener(destination = EmailConstants.CUA_EMAIL_NOTIFICACIO_AGRUPATS, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveMessageAgrupats(@Payload EmailAgrupat enviamentMail, @Headers MessageHeaders headers, Message message) throws JMSException {

        message.acknowledge();
        try {
            List<NotificacioEnviamentEntity> enviaments = new ArrayList<>();
            for (var enviament : enviamentMail.getEnviaments()) {
                enviaments.add(notificacioEnviamentRepository.findById(enviament).orElseThrow());
            }
            emailNotificacioHelper.prepararEmailsAgrupats(enviamentMail.getEmail(), enviaments, null);
        } catch (Exception ex) {
            log.error("Error enviant els emails per l'enviament agrupats per dia. Email desti: " + enviamentMail.getEmail(), ex);
        }
    }
}
