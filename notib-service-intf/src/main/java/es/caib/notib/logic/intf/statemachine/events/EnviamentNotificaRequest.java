package es.caib.notib.logic.intf.statemachine.events;

import es.caib.notib.logic.intf.statemachine.dto.EnviamentNotificaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviamentNotificaRequest implements Serializable {

    private EnviamentNotificaDto enviamentNotificaDto;
    private Integer numIntent;
}
