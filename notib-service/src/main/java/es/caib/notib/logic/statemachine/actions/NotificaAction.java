package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificaAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final EnviamentSmService enviamentSmService;

    @Override
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();

        var notificacioRegistrada = enviament.getNotificacio().getEnviaments().stream().allMatch(e -> e.getRegistreData() != null);
        if (notificacioRegistrada) {
//            enviamentSmService.notificaEnviament(enviamentUuid);
            enviament.getNotificacio().getEnviaments().forEach(e -> enviamentSmService.notificaEnviament(e.getNotificaReferencia()));
            log.debug("[SM] Tots els enviaments de la notificació estan registrats. S'ha d'avançar la màquina d'estats - enviament amb UUID " + enviamentUuid);
        }
    }
}