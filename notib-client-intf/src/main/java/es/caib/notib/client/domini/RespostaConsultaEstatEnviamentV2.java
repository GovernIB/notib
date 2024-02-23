
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespostaConsultaEstatEnviamentV2 extends RespostaBase {

    private String identificador;
    private String referencia;
    private String notificaIndentificador;
    private EnviamentEstat estat;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date estatData;
    private String estatDescripcio;
    private boolean enviamentSir;
    private boolean dehObligat;
    private String dehNif;
    private boolean entragaPostalActiva;
    private String adressaPostal;
    private PersonaV2 interessat;
    @XmlElement(nillable = true)
    private List<PersonaV2> representants;
    private Registre registre;
    private Sir sir;
    private Datat datat;
    private Certificacio certificacio;

    public List<PersonaV2> getRepresentants() {
        if (representants == null) {
            representants = new ArrayList<>();
        }
        return this.representants;
    }

}