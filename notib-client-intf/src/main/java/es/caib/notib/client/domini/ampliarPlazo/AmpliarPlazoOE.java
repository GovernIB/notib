
package es.caib.notib.client.domini.ampliarPlazo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmpliarPlazoOE {

    protected Envios envios;
//    protected String organismoEmisor;
    protected int plazo;
    protected String motivo;
    protected Long accioMassiva;

}
