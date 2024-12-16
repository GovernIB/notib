
package es.caib.notib.client.domini.ampliarPlazo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaAmpliarPlazoOE {

    protected String codigoRespuesta;
    protected String descripcionRespuesta;
    protected AmpliacionesPlazo ampliacionesPlazo;

    private List<String> codis;
    private List<String> descripcions;

    public boolean isOk() {

        if (codigoRespuesta != null) {
            return  codigoRespuesta.equals("000");
        }

        for (int i=0; i < codis.size(); i++) {

            if (!"000".equals(codis.get(i))) {
                return false;
            }
        }
        return true;
    }
}
