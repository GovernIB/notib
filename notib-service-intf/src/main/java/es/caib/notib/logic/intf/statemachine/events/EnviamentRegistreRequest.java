package es.caib.notib.logic.intf.statemachine.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class EnviamentRegistreRequest extends EnviamentSm implements Serializable {

//    private EnviamentRegistreDto enviamentRegistreDto;
//    private String enviamentUuid;

}
