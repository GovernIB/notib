package es.caib.notib.logic.intf.statemachine.events;

import es.caib.notib.logic.intf.statemachine.dto.ConsultaNotificaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaNotificaRequest implements Serializable {

    private ConsultaNotificaDto consultaNotificaDto;
    private Integer numIntent;
}
