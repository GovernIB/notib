
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
public class RespostaConsultaEstatEnviamentV2 extends RespostaBase {

    private String identificador;
    private String referencia;
    private String notificaIndentificador;
    private EnviamentEstatEnum estat;
    private Date estatData;
    private String estatDescripcio;
    private boolean enviamentSir;
    private boolean dehObligat;
    private String dehNif;
    private boolean entragaPostalActiva;
    private String adressaPostal;
    private Persona interessat;
    @XmlElement(nillable = true)
    private List<Persona> representants;
    private Registre registre;
    private Sir sir;
    private Datat datat;
    private Certificacio certificacio;

    public List<Persona> getRepresentants() {
        if (representants == null) {
            representants = new ArrayList<>();
        }
        return this.representants;
    }

}