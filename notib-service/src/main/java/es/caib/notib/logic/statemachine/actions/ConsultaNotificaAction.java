package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.statemachine.mappers.ConsultaNotificaMapper;
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
public class ConsultaNotificaAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final ConsultaNotificaMapper consultaNotificaMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);

        jmsTemplate.convertAndSend(
                SmConstants.CUA_CONSULTA_ESTAT,
                ConsultaNotificaRequest.builder()
                        .consultaNotificaDto(consultaNotificaMapper.toDto(enviament))
                        .numIntent(reintents + 1)
                        .build(),
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
                    return m;
                });

        log.debug("[SM] Enviada petició de consulta d'estat a notifica per l'enviament amb UUID " + enviamentUuid);
    }
}