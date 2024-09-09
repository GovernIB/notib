package es.caib.notib.logic.statemachine.listeners;

import com.google.common.base.Strings;

import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.missatges.MissatgeWs;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.logic.websocket.WebSocketJms;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
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
public class EnviamentRegistreListener {

    private final RegistreService registreService;
    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository enviamentRepository;
    private final WebSocketJms webSocketJms;
    private final MessageHelper messageHelper;

    private Semaphore semaphore = new Semaphore(5);


    @JmsListener(destination = SmConstants.CUA_REGISTRE, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentRegistre(@Payload EnviamentRegistreRequest enviamentRegistreRequest, @Headers MessageHeaders headers, Message message) throws JMSException, RegistreNotificaException, InterruptedException {

        message.acknowledge();
        var enviamentUuid = enviamentRegistreRequest.getEnviamentUuid();
        if (enviamentUuid == null) {
            log.error("[SM] Rebut enviament de registre sense Enviament");
            return;
        }
        log.debug("[SM] Rebut enviament de registre <" + enviamentUuid + ">");
        semaphore.acquire();
        try {
            var enviament = enviamentRepository.findByUuid(enviamentUuid);
            if (enviament.isEmpty()) {
                log.error("[SM] Enviament inexistent " + enviamentUuid);
                return;
            }
            var env = enviament.get();
            if (env.getNotificacio().isDeleted()) {
                NotibLogger.getInstance().info("[SM] Petició de notificació NO enviada. Enviament marcat com a deleted - UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
                return;
            }
            if (!Strings.isNullOrEmpty(env.getRegistreNumeroFormatat())) {
                log.error("[SM] L'enviament ja te numero de registre " + enviamentUuid);
                message.acknowledge();
                return;
            }
            var success = registreService.enviarRegistre(enviamentRegistreRequest);
            var msg = "";
            if (success) {
                NotibLogger.getInstance().info("[SM] Enviament de registre <" + enviamentUuid + "> success ", log, LoggingTipus.STATE_MACHINE);
                enviamentSmService.registreSuccess(enviamentUuid);
                msg = "enviament.registre.ok";
            } else {
                NotibLogger.getInstance().info("[SM] Enviament de registre <" + enviamentUuid + "> failed ", log, LoggingTipus.STATE_MACHINE);
                enviamentSmService.registreFailed(enviamentUuid);
                msg = "enviament.registre.error";
            }
            msg = messageHelper.getMessage(msg);
            //        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
            var missatge = MissatgeWs.builder().ok(success).msg(msg).codiUsuari(enviamentRegistreRequest.getCodiUsuari()).updateInfo(true).notificacioId(env.getNotificacio().getId()).build();
            webSocketJms.enviarMissatge(missatge);
        } finally {
            semaphore.release();
        }
        NotibLogger.getInstance().info("[SM] Enviament de registre <" + enviamentUuid + "> completat", log, LoggingTipus.STATE_MACHINE);
    }

}
