package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "GenericInfo")
public class GenericInfoApi {
    
    @Schema(name = "codi", implementation = String.class, example = "codi", description = "Codi de l'element")
    String codi;
    @Schema(name = "nom", implementation = String.class, example = "nom", description = "Nom de l'element")
    String nom;
    @Schema(name = "descripcio", implementation = String.class, example = "descripcio", description = "Descripcio de l'element")
    String descripcio;
}
