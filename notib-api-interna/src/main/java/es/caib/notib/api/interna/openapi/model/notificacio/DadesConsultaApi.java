
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter
@Schema(name = "DadesConsulta")
public class DadesConsultaApi {

    @Schema(name = "identificador", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
            description = "Identificador únic de la notificació a consultar\n" +
                    " * Camp mantingut únicament per compatibilitat amb versions antigues\n" +
                    " * S'ha d'indicar un idientificador o una referència. En cas d'informar ambdós, només es tindrà en compte l'identificador. Es recomana cercar sempre per referència")
    String identificador;
    @Schema(name = "referencia", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
            description = "Referència (identificador) única de l'enviament que es vol consultar\n" +
                    " * S'ha d'indicar un idientificador o una referència. En cas d'informar ambdós, només es tindrà en compte l'identificador.\n" +
                    " * Es recomana cercar sempre per referència")
    String referencia;
    @Schema(name = "ambJustificant", implementation = String.class, example = "true",
            description = "Indica si a la resposta s'ha d'incloure el justificant de registre")
    boolean ambJustificant;

}