
package es.caib.notib.client.domini.ampliarPlazo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.caib.notib.client.domini.RespostaBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespuestaAmpliarPlazoOE extends RespostaBase {

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
