package es.caib.notib.logic.intf.statemachine.events;

import es.caib.notib.logic.intf.statemachine.dto.EnviamentEmailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviamentEmailRequest implements Serializable {

    private EnviamentEmailDto enviamentEmailDto;
    private Integer numIntent;
}
