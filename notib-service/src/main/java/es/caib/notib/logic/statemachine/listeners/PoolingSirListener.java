package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.service.EnviamentSmService;
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
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@RequiredArgsConstructor
@Component
public class PoolingSirListener {

    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_POOLING_SIR, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload String enviamentUuid, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            if (enviament.getNotificacio().isDeleted()) {
                NotibLogger.getInstance().info("[SM] Petició de notificació NO enviada. Enviament marcat com a deleted - UUID " + enviament.getUuid(), log, LoggingTipus.STATE_MACHINE);
                return;
            }
            var notificacioRegistrada = enviament.getNotificacio().getEnviaments().stream().allMatch(e -> e.getRegistreData() != null);
            if (notificacioRegistrada) {
                enviament.getNotificacio().getEnviaments().forEach(e -> enviamentSmService.sirConsulta(e.getNotificaReferencia()));
                var msg = "[SM] Tots els enviaments de la notificació estan registrats. S'ha d'avancar la maquina d'estats - enviament amb UU ID " + enviamentUuid;
                NotibLogger.getInstance().info(msg, log, LoggingTipus.STATE_MACHINE);
            }
        } catch (Exception ex) {
            log.error("[SM] Error en el pooling Sir de l'enviament <" + enviamentUuid + ">");
        }
    }

}
