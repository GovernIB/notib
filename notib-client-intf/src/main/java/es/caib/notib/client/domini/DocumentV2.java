
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
    private ValidesaEnum validesa;

    public boolean isEmpty() {
        return ((arxiuNom == null || arxiuNom.trim().isEmpty()) &&
                (contingutBase64 == null || contingutBase64.trim().isEmpty()) &&
                (uuid == null || uuid.trim().isEmpty()) &&
                (csv == null || csv.trim().isEmpty()));
    }
    public boolean hasOnlyOneSource() {
        int countSources = getCountSources();
        return countSources == 1;
    }
    public boolean hasMultipleSources() {
        int countSources = getCountSources();
        return countSources > 1;
    }

    private int getCountSources() {
        int countSources = 0;
        if(contingutBase64 != null && !contingutBase64.trim().isEmpty()) countSources++;
        if(uuid != null && !uuid.trim().isEmpty()) countSources++;
        if(csv != null && !csv.trim().isEmpty()) countSources++;
        return countSources;
    }


}
