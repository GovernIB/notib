
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.client.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació del document que s'envia amb la notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentV2 {
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String arxiuId;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String arxiuNom;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String contingutBase64;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String uuid;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String csv;
    private Boolean modoFirma;
    private boolean normalitzat;
    private OrigenEnum origen;
    private TipusDocumentalEnum tipoDocumental;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String url;
    private ValidesaEnum validesa;

    public boolean isEmpty() {
        return ((arxiuNom == null || arxiuNom.isEmpty()) &&
                (contingutBase64 == null || contingutBase64.isEmpty()) &&
                (url == null || url.isEmpty()) &&
                (uuid == null || uuid.isEmpty()) &&
                (csv == null || csv.isEmpty()));
    }

}
