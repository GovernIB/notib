
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Informació d'un procediment per a una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Procediment")
public class ProcedimentApi {

    @Schema(name = "codiSia", implementation = String.class, example = "666666",
            description = "Codi SIA del procediment")
    protected String codiSia;
    @Schema(name = "nom", implementation = String.class, example = "Instància genèrica",
            description = "Nom del procediment")
    protected String nom;

}
