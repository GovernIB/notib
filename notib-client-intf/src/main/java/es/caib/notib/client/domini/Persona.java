
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.client.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Informació d'una persona per a un enviament.
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
public class Persona {

    private boolean incapacitat;
    private InteressatTipus interessatTipus;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String nom;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String llinatge1;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String llinatge2;
    protected DocumentTipus documentTipus;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String nif;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String telefon;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String email;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String raoSocial;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String dir3Codi;

    public String getNif() {
        return nif != null && Character.isDigit(nif.charAt(0)) && nif.length() < 9 ? afegirZerosNif() : nif;
    }

    private String afegirZerosNif() {

        int length = 9 - nif.length();
        for (int foo = 0; foo < length; foo++) {
            nif = 0 + nif;
        }
        return nif;
    }
}
