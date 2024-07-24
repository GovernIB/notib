package es.caib.notib.logic.intf.statemachine.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EnviamentRegistreRequest extends EnviamentSm implements Serializable {

//    private EnviamentRegistreDto enviamentRegistreDto;
    private String enviamentUuid;

}
