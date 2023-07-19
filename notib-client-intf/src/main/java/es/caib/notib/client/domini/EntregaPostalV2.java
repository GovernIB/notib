
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.client.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Informació de l'entrega postal.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntregaPostalV2 implements Serializable {

    // TODO ATRIBUTS AFEGITS A POSTERIORI. FER UNA HERENCIA PER DEIXAR-HO CORRECTE

    private NotificaDomiciliConcretTipus domiciliConcretTipus;

    // TODO ****************************************************************************


    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String apartatCorreus;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String bloc;
    protected Integer cie;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String codiPostal;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String complement;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String escala;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String formatFulla;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String formatSobre;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String linea1;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String linea2;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String municipiCodi;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String numeroCasa;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String numeroQualificador;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String paisCodi;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String planta;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String poblacio;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String porta;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String portal;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String provincia;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String puntKm;
    protected NotificaDomiciliConcretTipus tipus;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    protected String viaNom;
    protected EntregaPostalVia viaTipus;

}
