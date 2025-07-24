package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.service.EnviamentSmServiceImpl;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.statemachine.mappers.ConsultaNotificaMapper;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaNotificaAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final ConsultaNotificaMapper consultaNotificaMapper;
    private final ConfigHelper configHelper;
    private final NotificacioEventHelper notificacioEventHelper;
    private final EnviamentTableHelper enviamentTableHelper;
    private final JmsTemplate jmsTemplate;
    private final ApplicationContext applicationContext;

    // No es pot injectar degut a error cíclic
    private EnviamentSmService enviamentSmService;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 30000, multiplier = 10, maxDelay = 3600000))
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        NotibLogger.getInstance().info("[SM] ConsultaNotificaAction enviament " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
        var accioMassivaElementId = (Long) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ACCIO_MASSIVA_ID, null);
        var consulta = ConsultaNotificaRequest.builder().enviamentUuid(enviamentUuid).accioMassivaId(accioMassivaElementId).consultaNotificaDto(consultaNotificaMapper.toDto(enviament)).numIntent(reintents + 1).build();
        jmsTemplate.convertAndSend(SmConstants.CUA_CONSULTA_ESTAT, consulta,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
                    return m;
                });

        NotibLogger.getInstance().info("[SM] Enviada petició de consulta d'estat a notifica per l'enviament amb UUID " + enviamentUuid, log, LoggingTipus.STATE_MACHINE);
    }

    @Transactional
    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        log.error("[SM] Recover ConsultaNotificaAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover ConsultaNotificaAction de enviament amb uuid=" + enviamentUuid);

        // Modificar enviament amb error...
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1;
            var fiReintents = reintents >= getMaxReintents();
            var errorDescripcio = StringUtils.truncate("Error al enviar l'event de consulta. " + t.getMessage() + "\n" + ExceptionUtils.getStackTrace(t), 2000);

    		notificacioEventHelper.addNotificaConsultaEvent(enviament, true, errorDescripcio, fiReintents);
		    enviamentTableHelper.actualitzarRegistre(enviament);
        } catch (Exception ex) {
            log.error("[SM] Error al modificar enviament després d'una recuperació a ConsultaSirAction.", ex);
        }

        // Enviar event d'error a la màquina d'estats
        if (enviamentSmService == null) {
            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
        }
        enviamentSmService.consultaFailed(enviamentUuid);
    }

    private Integer getMaxReintents() {
        return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim");
    }
}