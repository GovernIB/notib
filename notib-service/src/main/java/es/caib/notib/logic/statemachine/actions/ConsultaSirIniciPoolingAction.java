package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.service.EnviamentSmServiceImpl;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ConsultaSirIniciPoolingAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final JmsTemplate jmsTemplate;
    private final ApplicationContext applicationContext;

    // No es pot injectar degut a error cíclic
    private EnviamentSmService enviamentSmService;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 30000, multiplier = 10, maxDelay = 3600000))
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        jmsTemplate.convertAndSend(SmConstants.CUA_POOLING_SIR, enviamentUuid);
        log.debug("[SM] Inici pooling consulta a SIR");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Recover
    public void recover(Throwable t, StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {

        log.error("[SM] Recover ConsultaSirIniciPoolingAction", t);
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        log.error("[SM] Recover ConsultaSirIniciPoolingAction de enviament amb uuid=" + enviamentUuid);

        // Enviar event a la màquina d'estats
        if (enviamentSmService == null) {
            enviamentSmService = applicationContext.getBean(EnviamentSmServiceImpl.class);
        }
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
        var notificacioRegistrada = enviament.getNotificacio().getEnviaments().stream().allMatch(e -> e.getRegistreData() != null);
        if (notificacioRegistrada) {
            enviament.getNotificacio().getEnviaments().forEach(e -> enviamentSmService.sirConsulta(e.getNotificaReferencia()));
            log.debug("[SM] Tots els enviaments de la notificació estan registrats. S'ha d'avançar la màquina d'estats - enviament amb UUID " + enviamentUuid);
        }
    }
}