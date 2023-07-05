package es.caib.notib.logic.intf.statemachine.events;

import es.caib.notib.logic.intf.statemachine.dto.EnviamentRegistreDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviamentRegistreRequest implements Serializable {

//    private EnviamentRegistreDto enviamentRegistreDto;
    private String enviamentUuid;
    private Integer numIntent;
}
