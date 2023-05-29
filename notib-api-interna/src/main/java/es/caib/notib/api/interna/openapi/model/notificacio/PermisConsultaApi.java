
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "PermisConsulta")
public class PermisConsultaApi {

    @Schema(name = "codiDir3Entitat", implementation = String.class, example = "A04003003",
            description = "Codi dir3 de l'entitat en la que es vol donar permís de consulta")
    protected String codiDir3Entitat;
    @Schema(name = "permisConsulta", implementation = Boolean.class, example = "true",
            description = "Indica si es vol donar o treure el permís de consulta")
    protected boolean permisConsulta;
    @Schema(name = "procedimentCodi", implementation = String.class, example = "666666",
            description = "Codi SIA del procediment sobre el que es vol donar permís de consulta")
    protected String procedimentCodi;
    @Schema(name = "usuariCodi", implementation = String.class, example = "u000000",
            description = "Codi de l'usuari al que es vol donar permís de consulta")
    protected String usuariCodi;

}
