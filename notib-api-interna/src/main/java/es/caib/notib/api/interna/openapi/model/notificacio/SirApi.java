
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

@Getter
@Schema(name = "Sir")
public class SirApi {

    @Schema(name = "dataRecepcio", implementation = Date.class, example = "2023-05-29T07:32:03.526+0000",
            description = "Data en que s'ha recepcionat el registre")
    private Date dataRecepcio;
    @Schema(name = "dataRegistreDesti", implementation = Date.class, example = "2023-05-29T07:32:03.526+0000",
            description = "Data en que s'ha registrat en el registre de destí")
    private Date dataRegistreDesti;

}