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
public class EnviamentSm implements Serializable {

    Integer numIntent;
    String codiUsuari;
}
