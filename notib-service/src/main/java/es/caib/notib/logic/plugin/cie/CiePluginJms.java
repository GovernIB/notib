package es.caib.notib.logic.plugin.cie;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.cie.CiePluginConstants;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CiePluginJms {

    private final JmsTemplate jmsTemplate;
    private final NotificacioRepository notificacioRepository;
    private final NotificacioEventRepository eventRepository;
    private final ConfigHelper configHelper;

    @Transactional
    public boolean enviarMissatge(String uuid, boolean retry)  {

        try {
            if (Strings.isNullOrEmpty(uuid)) {
                log.error("[CieJms] No s'ha pogut fer l'enviament CIE l'uuId de la notificacio es null");
                return false;
            }
            var foo = 1/0;
            var delay = 0L;
            if (retry) {
                var not = notificacioRepository.findByReferencia(uuid);
                if (not == null) {
                    log.error("[CieJms] No s'ha pogut fer l'enviament CIE. No existeix cap notificacio amb uuid " + uuid);
                    return false;
                }
                var events = eventRepository.findByNotificacioAndTipusAndErrorAndFiReintents(not, NotificacioEventTipusEnumDto.CIE_ENVIAMENT, true, false);
                if (events == null || events.isEmpty()) {
                    return false;
                }
                delay = configHelper.getConfigAsInteger("es.caib.notib.plugin.cie.reintents.delay");
            }
            final var d = delay;
            jmsTemplate.convertAndSend(CiePluginConstants.CUA_CIE_PLUGIN_ENVIAR, uuid,
                    m -> {
                        m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, d);
                        return m;
                    });
            return true;
        } catch (Exception ex) {
            log.error("[CieJms] Error al enviar la entrega postal per la notificacio " + uuid, ex);
            return false;
        }
    }

    public boolean cancelarEnviament(String uuid) {

        try {
            if (Strings.isNullOrEmpty(uuid)) {
                log.error("[CieJms] No s'ha pogut cancelar l'enviament CIE amb uuId null");
                return false;
            }
            jmsTemplate.convertAndSend(CiePluginConstants.CUA_CIE_PLUGIN_CANCELAR, uuid,
                    m -> {
                        m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 0L);
                        return m;
                    });
            return true;
        } catch (Exception ex) {
            log.error("[CieJms] Error al cancelar la entrega postal per la notificacio " + uuid, ex);
            return false;
        }
    }
}
