
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Size;

/**
 * Informació sobre l'entrega a la DEH.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "EntregaDeh")
public class EntregaDehApi {

    @Schema(name = "obligat", implementation = Boolean.class, example = "false", defaultValue = "false", required = false,
            description = "Indica si l’enviament és obligatori o voluntari")
    private boolean obligat;
    @Size(max = 64)
    @Schema(name = "procedimentCodi", implementation = String.class, example = "666666", required = false,
            description = "Codi del procediment a la DEH per a enviaments voluntaris.\n" +
                    " * Aquest camp NO s'utilitza, sinó que s’agafa el codi del procediment de la notificació.\n" +
                    "(Es manté únicament per compatibilitat amb els serveis de Notific@)")
    private String procedimentCodi;

}
