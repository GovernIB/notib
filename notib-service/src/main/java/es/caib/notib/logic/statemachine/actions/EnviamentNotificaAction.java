package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.service.EnviamentSmServiceImpl;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.statemachine.mappers.EnviamentNotificaMapper;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class EnviamentNotificaAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final EnviamentNotificaMapper enviamentNotificaMapper;
    private final ConfigHelper configHelper;
    private final NotificacioEventHelper notificacioEventHelper;
    private final CallbackHelper callbackHelper;
    private final NotificacioTableHelper notificacioTableHelper;
    private final JmsTemplate jmsTemplate;
    private final ApplicationContext applicationContext;

    // No es pot injectar degut a error cíclic
    private EnviamentSmService enviamentSmService;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 30000, multiplier = 10, maxDelay = 3600000))
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var variables = stateContext.getExtendedState().getVariables();
        var reintents = (int) variables.getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
        var notificacioRegistrada = enviament.getNotificacio().getEnviaments().stream().allMatch(e -> e.getRegistreData() != null);
        if (!notificacioRegistrada) {
            log.debug("[SM] Petició de notificació NO enviada degut a que no tots els enviaments estan registrats - enviament amb UUID " + enviamentUuid);
            return;
        }
        var env = EnviamentNotificaRequest.builder().enviamentNotificaDto(enviamentNotificaMapper.toDto(enviament)).numIntent(reintents + 1).build();
        var retry = (boolean) variables.getOrDefault(SmConstants.NT_RETRY, false);
        var isRetry = EnviamentSmEvent.NT_RETRY.equals(stateContext.getMessage().getPayload()) || retry;
        variables.put(SmConstants.RG_RETRY, false);
        jmsTemplate.convertAndSend(SmConstants.CUA_NOTIFICA, env,
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, !isRetry ? SmConstants.delay(reintents) : 0L);
                    return m;
                });
        log.debug("[SM] Enviada petició de notificació per l'enviament amb UUID " + enviamentUuid);
    }

    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        log.error("[SM] Recover EnviamentNotificaAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover EnviamentNotificaAction de enviament amb uuid=" + enviamentUuid);

        // Modificar enviament amb error...
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var notificacio = enviament.getNotificacio();
            var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1;
            var fiReintents = reintents >= getMaxReintents();
            var errorDescripcio = StringUtils.truncate("Error al enviar l'event de notificació. " + t.getMessage() + "\n" + ExceptionUtils.getStackTrace(t), 2000);

            // Controlar si hi ha enviaments per email
            notificacio.getEnviaments().stream().filter(e -> e.isPerEmail()).forEach(e -> notificacioEventHelper.addEmailEnviamentEvent(e, true, errorDescripcio, fiReintents));
            if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat()))) {
                notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS);
            }
            notificacioEventHelper.addNotificaEnviamentEvent(notificacio, true, errorDescripcio, fiReintents);
            callbackHelper.updateCallbacks(notificacio, true, errorDescripcio);
            notificacioTableHelper.actualitzarRegistre(notificacio);
        } catch (Exception ex) {
            log.error("[SM] Error al modificar enviament després d'una recuperació a EnviamentNotificaAction.", ex);
        }

        // Enviar event d'error a la màquina d'estats
        if (enviamentSmService == null) {
            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
        }
        enviamentSmService.notificaFailed(enviamentUuid);
    }

    private Integer getMaxReintents() {
        return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim");
    }
}