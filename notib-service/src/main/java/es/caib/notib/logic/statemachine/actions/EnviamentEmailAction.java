package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.statemachine.mappers.EnviamentNotificaMapper;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnviamentEmailAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
//    private final EnviamentEmailMapper enviamentEmailMapper;
    private final EnviamentNotificaMapper enviamentNotificaMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);

//        jmsTemplate.convertAndSend(
//                SmConstants.CUA_EMAIL,
//                EnviamentEmailRequest.builder()
//                        .enviamentEmailDto(enviamentEmailMapper.toDto(enviament))
//                        .numIntent(reintents + 1)
//                        .build(),
//                m -> {
//                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
//                    return m;
//                });
        // Per ara els emails s'envien utilitzant la funcionalitat d'enviament de notificació
        jmsTemplate.convertAndSend(
                SmConstants.CUA_NOTIFICA,
                EnviamentNotificaRequest.builder()
                        .enviamentNotificaDto(enviamentNotificaMapper.toDto(enviament))
                        .numIntent(reintents + 1)
                        .build(),
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
                    return m;
                });

        log.debug("[SM] Enviada petició d'avís per email per l'enviament amb UUID " + enviamentUuid);
    }
}