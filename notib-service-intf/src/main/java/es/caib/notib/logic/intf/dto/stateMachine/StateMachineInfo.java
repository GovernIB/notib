package es.caib.notib.logic.intf.dto.stateMachine;

import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StateMachineInfo {

    private boolean mostrar;
    private EnviamentSmEstat estat;
    private List<CodiValorDto> estats = new ArrayList<>();
    private List<CodiValorDto> events = new ArrayList<>();


    public StateMachineInfo() {

        var states = EnviamentSmEstat.values();
        for (var state : states) {
            estats.add(CodiValorDto.builder().codi(state.name()).valor(state.name()).build());
        }
        var evs = EnviamentSmEvent.values();
        for (var ev : evs) {
            events.add(CodiValorDto.builder().codi(ev.name()).valor(ev.name()).build());
        }
    }

}
