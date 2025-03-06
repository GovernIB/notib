package es.caib.notib.api.interna.openapi.interficies;

import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface SirAdviserApiRestIntf {

    @PostMapping(value = "/sincronitzar", headers="Content-Type=application/json")
    @Operation(summary = "Actualitza la informació de l'enviament SIR rebut des de registre", description = "Actualitza la informació de l'enviament SIR rebut des de registre")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Sincronitzar enviament SIR", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaSirAdviser.class, description = "Sincronitza enviament SIR"))})})
    RespostaSirAdviser sincronitzarEnviamentSir(HttpServletRequest request, @RequestBody SirAdviser adviser, Model model);

}
