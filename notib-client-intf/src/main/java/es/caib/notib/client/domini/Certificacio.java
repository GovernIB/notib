
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
 * Informació sobre la certificació d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Certificacio {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date data;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String origen;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String contingutBase64;
    private int tamany;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String hash;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String metadades;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String csv;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String tipusMime;

}
