package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
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
import java.util.Date;
import java.util.concurrent.Semaphore;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnviamentRegistreListener {

    private final RegistreService registreService;
    private final EnviamentSmService enviamentSmService;

    private Semaphore semaphore = new Semaphore(5);


    @JmsListener(destination = SmConstants.CUA_REGISTRE, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentRegistre(@Payload EnviamentRegistreRequest enviamentRegistreRequest, @Headers MessageHeaders headers, Message message) throws JMSException, RegistreNotificaException, InterruptedException {

        var enviamentUuid = enviamentRegistreRequest.getEnviamentUuid();
        if (enviamentUuid != null) {
            log.debug("[SM] Rebut enviament de registre <" + enviamentUuid + ">");
        } else {
            log.error("[SM] Rebut enviament de registre sense Enviament");
        }

        semaphore.acquire();
        try {
            var success = registreService.enviarRegistre(enviamentRegistreRequest);
            if (success) {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> success ");
                enviamentSmService.registreSuccess(enviamentUuid);
            } else {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> failed ");
                enviamentSmService.registreFailed(enviamentUuid);
            }
        } finally {
            semaphore.release();
        }
        log.debug("[SM] Enviament de registre <" + enviamentUuid + "> completat");
        message.acknowledge();
    }

}
