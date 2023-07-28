package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.logic.service.EnviamentSmServiceImpl;
import es.caib.notib.logic.statemachine.SmConstants;
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
public class EnviamentRegistreAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
//    private final EnviamentRegistreMapper enviamentRegistreMapper;
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
//        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);

        jmsTemplate.convertAndSend(SmConstants.CUA_REGISTRE,
                EnviamentRegistreRequest.builder()
                        .enviamentUuid(enviamentUuid)
                        .numIntent(reintents + 1)
//                        .enviamentRegistreDto(enviamentRegistreMapper.toDto(enviament))
                        .build(),
                m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
                    return m;
                });

        log.debug("[SM] Enviada petició de registre per l'enviament amb UUID " + enviamentUuid);
    }

    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        log.error("[SM] Recover EnviamentRegistreAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover EnviamentRegistreAction de enviament amb uuid=" + enviamentUuid);

        // Modificar enviament
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var reintents = (int) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1;
            var fiReintents = reintents >= getMaxReintents();
            var errorDescripcio = StringUtils.truncate("Error al enviar l'event de creació de registre de sortida. " + t.getMessage() + "\n" + ExceptionUtils.getStackTrace(t), 2000);

            notificacioEventHelper.addRegistreEnviamentEvent(enviament, true, errorDescripcio, fiReintents);
            callbackHelper.crearCallback(enviament.getNotificacio(), enviament, true, errorDescripcio);
            enviamentTableHelper.actualitzarRegistre(enviament);
        } catch (Exception ex) {
            log.error("[SM] Error al modificar enviament després d'una recuperació a EnviamentRegistreAction.", ex);
        }

        // Enviar event d'error a la màquina d'estats
        if (enviamentSmService == null) {
            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
        }
        enviamentSmService.registreFailed(enviamentUuid);
    }

    private Integer getMaxReintents() {
        return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.reintents.maxim");
    }
}