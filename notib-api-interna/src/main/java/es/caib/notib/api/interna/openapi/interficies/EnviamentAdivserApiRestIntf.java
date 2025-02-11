package es.caib.notib.api.interna.openapi.interficies;

import es.caib.notib.api.interna.model.adviser.CieAdviser;
import es.caib.notib.api.interna.model.adviser.EnviamentCieAdviser;
import es.caib.notib.api.interna.openapi.model.consulta.RespostaConsultaV2Api;
import es.caib.notib.logic.intf.dto.AdviserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/cie/adviser")
@Tag(name = "Consulta v2", description = "API de consulta v2")
public interface EnviamentAdivserApiRestIntf {

    @PostMapping(value = "/sincronitzar", headers="Content-Type=application/json")
    @Operation(summary = "Actualitza la informació de l'enviament postal rebut des del CIE", description = "Actualitza la informació de l'enviament postal rebut des del CIE")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Sincronitzar enviament CIE", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Sincronitza enviament CIE"))})})
    AdviserResponseDto sincronitzarEnviamentCie(HttpServletRequest request, @RequestBody CieAdviser adviser, Model model);

}
