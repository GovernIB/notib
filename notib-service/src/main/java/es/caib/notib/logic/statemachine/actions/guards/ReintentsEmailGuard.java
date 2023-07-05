package es.caib.notib.logic.statemachine.actions.guards;

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
public class ReintentsEmailGuard implements Guard<EnviamentSmEstat, EnviamentSmEvent> {

    private final ConfigHelper configHelper;

    @Override
    public boolean evaluate(StateContext<EnviamentSmEstat, EnviamentSmEvent> stateContext) {
        var numReintentsActuals = Integer.class.cast(stateContext.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0));
        var maxReintents = configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim", 3);
        log.debug("[SM] Reintent per error de enviament per email. Intent actual=" + numReintentsActuals + ", Max intents=" + maxReintents);
        return numReintentsActuals.intValue() >= maxReintents;
    }
}
