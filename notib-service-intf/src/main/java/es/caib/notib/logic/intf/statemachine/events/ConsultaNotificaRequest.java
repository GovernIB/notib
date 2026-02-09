package es.caib.notib.logic.intf.statemachine.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConsultaNotificaRequest extends EnviamentSm implements Serializable {

    private Long id;
}
