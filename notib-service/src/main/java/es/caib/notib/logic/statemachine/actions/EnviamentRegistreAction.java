package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.logic.statemachine.SmConstants;
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
public class EnviamentRegistreAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

//    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
//    private final EnviamentRegistreMapper enviamentRegistreMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
//        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);

        jmsTemplate.convertAndSend(
                SmConstants.CUA_REGISTRE,
                EnviamentRegistreRequest.builder()
//                        .enviamentRegistreDto(enviamentRegistreMapper.toDto(enviament))
                        .enviamentUuid(enviamentUuid)
                        .numIntent(reintents + 1)
                        .build(),
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
                    return m;
                });

        log.debug("[SM] Enviada petició de registre per l'enviament amb UUID " + enviamentUuid);
    }
}