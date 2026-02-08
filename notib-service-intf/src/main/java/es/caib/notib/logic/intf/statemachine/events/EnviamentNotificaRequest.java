package es.caib.notib.logic.intf.statemachine.events;

import lombok.AllArgsConstructor;
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

    private Long id;
}
