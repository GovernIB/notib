
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.RegistreEstatEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

/**
 * Informació del registre d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Registre")
public class RegistreApi {

    @Schema(name = "numero", implementation = Integer.class, example = "1753", description = "Número de registre")
    private Integer numero;
    @Schema(name = "data", implementation = Long.class, example = "1706168093962", description = "Data del registre")
    private Date data;
    @Schema(name = "numeroFormatat", implementation = String.class, example = "GOIBS1753/2023", description = "Número de registre amb format")
    private String numeroFormatat;
    @Schema(name = "estat", implementation = RegistreEstatEnum.class, example = "VALID", description = "Estat del registre")
    private RegistreEstatEnum estat;
    @Schema(name = "oficina", implementation = String.class, example = "Oficina Virtual", description = "Oficina on s'ha realitzat el registre")
    private String oficina;
    @Schema(name = "llibre", implementation = String.class, example = "Govern de les Illes Balears", description = "Llibre on s'ha anotat el registre")
    private String llibre;

}