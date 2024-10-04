package es.caib.notib.logic.plugin.cie;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
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
    public void enviarMissatge(String uuid, boolean retry)  {

        if (Strings.isNullOrEmpty(uuid)) {
            log.error("No s'ha pogut fer l'enviament CIE uuId de la notificacio null");
            return;
        }
        var delay = 0L;
        if (retry) {
            var not = notificacioRepository.findByReferencia(uuid);
            var events = eventRepository.findByNotificacioAndTipusAndErrorAndFiReintents(not, NotificacioEventTipusEnumDto.CIE_ENVIAMENT, true, false);
            if (events == null || events.isEmpty()) {
                 return;
            }
            delay = configHelper.getConfigAsInteger("es.caib.notib.plugin.cie.reintents.delay");
        }
        final var d = delay;
        jmsTemplate.convertAndSend(CiePluginConstants.CUA_CIE_PLUGIN_ENVIAR, uuid,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,  d);
                    return m;
                });
    }

    public void cancelarEnviament(String uuid) {

        if (Strings.isNullOrEmpty(uuid)) {
            log.error("No s'ha pogut cancelar l'enviament CIE amb uuId null");
            return;
        }
        jmsTemplate.convertAndSend(CiePluginConstants.CUA_CIE_PLUGIN_CANCELAR, uuid,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,  0L);
                    return m;
                });
    }
}
