package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.service.EnviamentSmServiceImpl;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.statemachine.mappers.ConsultaSirMapper;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaSirAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final ConsultaSirMapper enviamentSirMapper;
    private final ConfigHelper configHelper;
    private final NotificacioEventHelper notificacioEventHelper;
    private final CallbackHelper callbackHelper;
    private final EnviamentTableHelper enviamentTableHelper;
    private final JmsTemplate jmsTemplate;
    private final ApplicationContext applicationContext;

    // No es pot injectar degut a error cíclic
    private EnviamentSmService enviamentSmService;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 30000, multiplier = 10, maxDelay = 3600000))
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        NotibLogger.getInstance().info("[SM] EnviamentNotificaAction enviament " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
        var env = ConsultaSirRequest.builder().consultaSirDto(enviamentSirMapper.toDto(enviament)).numIntent(reintents + 1).build();
        var isRetry = EnviamentSmEvent.SR_RETRY.equals(stateContext.getMessage().getPayload());
        jmsTemplate.convertAndSend(SmConstants.CUA_CONSULTA_SIR, env,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, !isRetry ? SmConstants.delay(reintents) : 0L);
                    return m;
                });
        NotibLogger.getInstance().info("[SM] Enviada consulta d'estat SIR per l'enviament amb UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
    }

    @Transactional
    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        log.error("[SM] Recover ConsultaSirAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover ConsultaSirAction de enviament amb uuid=" + enviamentUuid);

        // Modificar enviament amb error...
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1;
            var maxReintents = getMaxReintents();
            var fiReintents = reintents >= maxReintents;
            var errorDescripcio = StringUtils.truncate("Error al enviar l'event de consulta SIR. " + t.getMessage() + "\n" + ExceptionUtils.getStackTrace(t), 2000);
            var errorUltimaConsulta = enviament.getSirConsultaIntent() > 0;

            enviament.updateSirNovaConsulta(maxReintents);
            notificacioEventHelper.addSirConsultaEvent(enviament, true, errorDescripcio, fiReintents);
            if (!errorUltimaConsulta) {
                callbackHelper.updateCallback(enviament, true, errorDescripcio);
            }
            enviamentTableHelper.actualitzarRegistre(enviament);
        } catch (Exception ex) {
            log.error("[SM] Error al modificar enviament després d'una recuperació a ConsultaSirAction.", ex);
        }

        // Enviar event d'error a la màquina d'estats
        if (enviamentSmService == null) {
            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
        }
        enviamentSmService.sirFailed(enviamentUuid);

    }

    private Integer getMaxReintents() {
        return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim");
    }
}