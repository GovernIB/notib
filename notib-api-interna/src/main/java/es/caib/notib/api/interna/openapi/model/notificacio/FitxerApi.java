
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;


/**
 * Informació d'un fitxer.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Fitxer")
public class FitxerApi implements Serializable {

    @Schema(name = "nom", implementation = String.class, example = "document", description = "Nom del fitxer")
    private String nom;
    @Schema(name = "contentType", implementation = String.class, example = "application/pdf", description = "Tipus de contingut del fitxer")
    private String contentType;
    @Schema(name = "contingut", implementation = String.class, description = "Contingut del fitxer")
    private byte[] contingut;
    @Schema(name = "tamany", implementation = Long.class, example = "4265384", description = "Mida del fitxer")
    private long tamany;

}