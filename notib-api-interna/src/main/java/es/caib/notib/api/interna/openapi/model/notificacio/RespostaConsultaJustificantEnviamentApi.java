
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "RespostaConsultaJustificantEnviament")
public class RespostaConsultaJustificantEnviamentApi extends RespostaBaseApi {

    @Schema(name = "justificant", implementation = FitxerApi.class,
            description = "Fitxer del justificant")
    private FitxerApi justificant;

}