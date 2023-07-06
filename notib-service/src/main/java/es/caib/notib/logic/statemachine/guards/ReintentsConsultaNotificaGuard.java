package es.caib.notib.logic.statemachine.guards;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.statemachine.SmConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReintentsConsultaNotificaGuard implements Guard<EnviamentSmEstat, EnviamentSmEvent> {

    private final ConfigHelper configHelper;

    @Override
    public boolean evaluate(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var numReintentsActuals = (Integer) stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
        var maxReintents = configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim", 3);
        log.debug("[SM] Reintent per error de consulta de notifica. Intent actual=" + numReintentsActuals + ", Max intents=" + maxReintents);
        return numReintentsActuals < maxReintents;
    }
}
