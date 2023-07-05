package es.caib.notib.logic.statemachine.actions;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.statemachine.SmConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaNotificaIniciPoolingAction implements Action<EnviamentSmEstat, EnviamentSmEvent> {

//    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final EnviamentSmService enviamentSmService;
    private final ConfigHelper configHelper;

    @Override
    public void execute(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var enviamentUuid = (String) stateContext.getMessage().getHeaders().get(SmConstants.ENVIAMENT_UUID_HEADER);

        if (!isAdviserActiu()) {
            enviamentSmService.enviamentConsulta(enviamentUuid);
            log.debug("[SM] Iniciat pooling de consulta d'estat a Notifica de l'enviament amb UUID " + enviamentUuid);
        }
    }

    public boolean isAdviserActiu() {
        return configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu");
    }
}