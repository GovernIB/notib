
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

@Getter
public class RespostaBaseApi {

    @Schema(name = "error", implementation = Boolean.class, example = "true",
            description = "Indica si s'ha produït algún error en l'enviament")
    private boolean error;
    @Schema(name = "errorData", implementation = Date.class, example = "2023-05-29T07:32:03.526+0000",
            description = "Data en que s'ha produït l'error")
    private Date errorData;
    @Schema(name = "errorDescripcio", implementation = String.class, example = "java.lang.NullPointerException",
            description = "Identificador de la notificació a Notib")
    private String errorDescripcio;

}
