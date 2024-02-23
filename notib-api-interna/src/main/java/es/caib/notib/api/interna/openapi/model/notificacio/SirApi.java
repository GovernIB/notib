
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

@Getter
@Schema(name = "Sir")
public class SirApi {

    @Schema(name = "dataRecepcio", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha recepcionat el registre")
    private Date dataRecepcio;
    @Schema(name = "dataRegistreDesti", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha registrat en el registre de destí")
    private Date dataRegistreDesti;

}