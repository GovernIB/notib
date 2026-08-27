package es.caib.notib.api.interna.openapi.interficies;

import es.caib.notib.api.interna.openapi.model.consulta.ArxiuApi;
import es.caib.notib.client.domini.Procediment;
import es.caib.notib.logic.intf.dto.PaginaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/procediment")
@Tag(name = "Consulta de procediments", description = "API de consulta de procediments")
public interface ProcedimentApiRestIntf {

	@GetMapping(value="/entitat/{codiEntitat}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté procediments CIE", description = "Retorna la llista de procediments d'una entitat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Procediments de l'entitat paginats", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
    PaginaDto<Procediment> getProcedimentsByEntitat(HttpServletRequest request,
                                                    @PathVariable String codiEntitat,
                                                    @RequestParam(value = "codi", required = false) String codi,
                                                    @RequestParam(value = "nom", required = false) String nom,
                                                    @RequestParam(value = "organGestor", required = false) String organGestor,
                                                    @RequestParam(value = "actiu", required = false) Boolean actiu,
                                                    @RequestParam(value = "comu", required = false) Boolean comu,
                                                    @RequestParam(value = "manual", required = false) Boolean manual,
                                                    @RequestParam(value = "entregaCieActiva", required = false) Boolean entregaCieActiva,
                                                    @RequestParam(value = "requereixPermisDirecte", required = false) Boolean requereixPermisDirecte,
                                                    @RequestParam(value = "pagina", required = false) Integer pagina,
                                                    @RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/entitat/{codiEntitat}/procediment/cie/actiu", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté procediments amb entrega CIE", description = "Retorna la llista de procediments d'una entitat que tenen l'entrega CIE activada a algun nivell")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Llistat de procediments de l'entitat que tenen l'entrega CIE activada a algun nivell ", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	List<Procediment> getProcedimentsCieByEntitat(HttpServletRequest request,
												  @PathVariable String codiEntitat,
												  @RequestParam(value = "codi", required = false) String codi,
												  @RequestParam(value = "nom", required = false) String nom,
												  @RequestParam(value = "organGestor", required = false) String organGestor,
												  @RequestParam(value = "actiu", required = false) Boolean actiu,
												  @RequestParam(value = "comu", required = false) Boolean comu,
												  @RequestParam(value = "manual", required = false) Boolean manual,
												  @RequestParam(value = "entregaCieActiva", required = false) Boolean entregaCieActiva,
												  @RequestParam(value = "requereixPermisDirecte", required = false) Boolean requereixPermisDirecte,
												  @RequestParam(value = "pagina", required = false) Integer pagina,
												  @RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/entitat/{codiEntitat}/procediment/{codiProcediment}/organ/{organCodi}/cie/actiu", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta si entrega CIE permesa", description = "Retorna cert si el procediment té activa l'entrega CIE per l'entitat i òrgan especificat en el paràmetre")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta CIE per procediment", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	Boolean getProcedimentsCieByEntitatAndOrganAndCodi(HttpServletRequest request, @PathVariable String codiProcediment, @PathVariable String codiEntitat, @PathVariable String codiOrgan);




}
