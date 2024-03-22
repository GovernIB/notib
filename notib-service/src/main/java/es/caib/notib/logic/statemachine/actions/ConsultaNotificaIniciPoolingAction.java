package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.service.EnviamentSmServiceImpl;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaNotificaIniciPoolingAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final JmsTemplate jmsTemplate;
    private final ConfigHelper configHelper;
    private final ApplicationContext applicationContext;

    private static final Long DELAY_DEFECTE = 1800000L;

    // No es pot injectar degut a error cíclic
    private EnviamentSmService enviamentSmService;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 30000, multiplier = 10, maxDelay = 3600000))
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        if (isAdviserActiu()) {
            return;
        }
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        NotibLogger.getInstance().info("[SM] ConsultaNotificaPoolingAction enviament " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
        var delay = configHelper.getConfigAsLong("es.caib.notib.pooling.delay", DELAY_DEFECTE);
        jmsTemplate.convertAndSend(SmConstants.CUA_POOLING_ESTAT, enviamentUuid, m -> {
            m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
            return m;
        });
        NotibLogger.getInstance().info("[SM] Inici pooling consulta a Notifica, si no està actiu l'adviser", log, LoggingTipus.STATE_MACHINE);
    }

    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        log.error("[SM] Recover ConsultaNotificaIniciPoolingAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover ConsultaNotificaIniciPoolingAction de enviament amb uuid=" + enviamentUuid);

        // Enviar event a la màquina d'estats
        if (enviamentSmService == null) {
            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
        }
        if (!isAdviserActiu()) {
            enviamentSmService.enviamentConsulta(enviamentUuid);
            NotibLogger.getInstance().info("[SM] Iniciat pooling de consulta d'estat a Notifica de l'enviament amb UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
        }

    }

    public boolean isAdviserActiu() {
        return configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu");
    }


}