
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Informació retornada per l'alta d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespostaAlta {

    private String identificador;
    private NotificacioEstatEnum estat;
    @XmlElement(nillable = true)
    private List<EnviamentReferencia> referencies;
    private boolean error;
    private String errorDescripcio;

    public List<EnviamentReferencia> getReferencies() {
        if (referencies == null) {
            referencies = new ArrayList<>();
        }
        return this.referencies;
    }
}
