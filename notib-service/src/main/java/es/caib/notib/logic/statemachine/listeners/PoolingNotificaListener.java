package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.helper.ConfigHelper;
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
public class PoolingNotificaListener {

    private final EnviamentSmService enviamentSmService;
    private final ConfigHelper configHelper;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;

    @Transactional
    @JmsListener(destination = SmConstants.CUA_POOLING_ESTAT, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveConsultaSir(@Payload String enviamentUuid, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        if (enviament.getNotificacio().isDeleted()) {
            NotibLogger.getInstance().info("[SM] Petició de notificació NO enviada. Enviament marcat com a deleted - UUID " + enviament.getUuid(), log, LoggingTipus.STATE_MACHINE);
            return;
        }
        NotibLogger.getInstance().info("[SM] PoolingNotificaListener enviament " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
        enviamentSmService.enviamentConsulta(enviamentUuid);
        NotibLogger.getInstance().info("[SM] Iniciat pooling de consulta d'estat a Notifica de l'enviament amb UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
    }

    public boolean isAdviserActiu() {
        return configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu");
    }

}
