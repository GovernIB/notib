
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.client.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Informació sobre el datat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Datat {

    private EnviamentEstat estat;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date data;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String origen;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String receptorNif;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String receptorNom;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String errorDescripcio;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String numSeguiment;

}