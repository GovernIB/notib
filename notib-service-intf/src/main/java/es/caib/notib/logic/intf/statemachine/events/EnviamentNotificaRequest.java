package es.caib.notib.logic.intf.statemachine.events;

import es.caib.notib.logic.intf.statemachine.dto.EnviamentNotificaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class EnviamentNotificaRequest extends EnviamentSm implements Serializable {

    private EnviamentNotificaDto enviamentNotificaDto;
}
