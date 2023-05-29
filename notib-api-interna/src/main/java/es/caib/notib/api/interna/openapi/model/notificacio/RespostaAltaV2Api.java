
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.NotificacioEstatEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/**
 * Informació retornada per l'alta d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "RespostaAltaV2")
public class RespostaAltaV2Api extends RespostaBaseApi {

    @Schema(name = "identificador", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
            description = "Identificador únic que se li ha assignat a la notificació a Notib")
    private String identificador;
    @Schema(name = "estat", implementation = NotificacioEstatEnum.class, example = "PENDENT",
            description = "Estat de la notificació")
    private NotificacioEstatEnum estat;
    @Schema(name = "referencies",
            description = "Referències (identificadors) úniques que s'han assignat a de cada un dels enviaments de la notificació")
    private List<EnviamentReferenciaV2Api> referencies;
    @Schema(name = "dataCreacio", implementation = String.class, example = "2023-05-29T07:32:03.526+0000",
            description = "Data en que s'ha donat d'alta la notificació")
    private Date dataCreacio;

}
