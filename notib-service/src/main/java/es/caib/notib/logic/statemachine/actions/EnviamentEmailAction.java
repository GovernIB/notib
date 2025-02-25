package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.statemachine.mappers.EnviamentNotificaMapper;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@Slf4j
//@Component
@RequiredArgsConstructor
public class EnviamentEmailAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
//    private final EnviamentEmailMapper enviamentEmailMapper;
    private final EnviamentNotificaMapper enviamentNotificaMapper;
    private final JmsTemplate jmsTemplate;
    private final ApplicationContext applicationContext;

    // No es pot injectar degut a error cíclic
    private EnviamentSmService enviamentSmService;

    @Override
//    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 30000, multiplier = 10, maxDelay = 3600000))
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        NotibLogger.getInstance().info("[SM] EnviamentEmailAction enviament " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
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
                        .enviamentUuid(enviamentUuid)
                        .enviamentNotificaDto(enviamentNotificaMapper.toDto(enviament))
                        .numIntent(reintents + 1)
                        .build(),
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
                    return m;
                });

        NotibLogger.getInstance().info("[SM] Enviada petició d'avís per email per l'enviament amb UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
    }

//    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        log.error("[SM] Recover EnviamentEmailAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover EnviamentEmailAction de enviament amb uuid=" + enviamentUuid);

        // TODO: Modificar enviament amb error...


        // Enviar event d'error a la màquina d'estats
//        if (enviamentSmService == null) {
//            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
//        }
//        enviamentSmService.emailFailed(enviamentUuid);

    }
}