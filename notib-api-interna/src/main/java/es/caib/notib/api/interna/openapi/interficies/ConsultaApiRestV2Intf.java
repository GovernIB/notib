package es.caib.notib.api.interna.openapi.interficies;

import es.caib.notib.api.interna.openapi.model.consulta.ArxiuApi;
import es.caib.notib.api.interna.openapi.model.consulta.RespostaConsultaV2Api;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.client.domini.consulta.Arxiu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RequestMapping("/consulta/v2")
@Tag(name = "Consulta v2", description = "API de consulta v2")
public interface ConsultaApiRestV2Intf {

	@GetMapping(value="/comunicacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions d'un titular donat el seu dni", description = "Retorna informació de totes les comunicacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	public RespostaConsultaV2 comunicacionsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@Parameter(name = "visibleCarpeta", description = "Filtrar per visible a carpeta. Si s'indica el valor si, només es retornaran enviaments amb estats visibles per la carpeta. Si s'indica el valor no, es retornaran tots els enviaments independentment de si els seus estats son visible o no a la carpeta.", content = { @Content(schema = @Schema( implementation = Boolean.class, defaultValue = "si", allowableValues = "[si, no, true, false, 1, 0]"))})
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@Parameter(name = "lang", description = "Idioma de les descripcions", content = { @Content(schema = @Schema( implementation = Idioma.class, defaultValue = "ca", allowableValues = "[ca, es]"))})
			@RequestParam (value = "lang", required = false) Idioma lang,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/notificacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions d'un titular donat el seu dni", description = "Retorna informació de totes les notificacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	public RespostaConsultaV2 notificacionsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@Parameter(name = "visibleCarpeta", description = "Filtrar per visible a carpeta. Si s'indica el valor si, només es retornaran enviaments amb estats visibles per la carpeta. Si s'indica el valor no, es retornaran tots els enviaments independentment de si els seus estats son visible o no a la carpeta.", content = { @Content(schema = @Schema( implementation = Boolean.class, defaultValue = "si", allowableValues = "[si, no]"))})
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@Parameter(name = "lang", description = "Idioma de les descripcions", content = { @Content(schema = @Schema( implementation = Idioma.class, defaultValue = "ca", allowableValues = "[ca, es]"))})
			@RequestParam (value = "lang", required = false) Idioma lang,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/comunicacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les comunicacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	public RespostaConsultaV2 comunicacionsPendentsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@Parameter(name = "visibleCarpeta", description = "Filtrar per visible a carpeta. Si s'indica el valor si, només es retornaran enviaments amb estats visibles per la carpeta. Si s'indica el valor no, es retornaran tots els enviaments independentment de si els seus estats son visible o no a la carpeta.", content = { @Content(schema = @Schema( implementation = Boolean.class, defaultValue = "si", allowableValues = "[si, no]"))})
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@Parameter(name = "lang", description = "Idioma de les descripcions", content = { @Content(schema = @Schema( implementation = Idioma.class, defaultValue = "ca", allowableValues = "[ca, es]"))})
			@RequestParam (value = "lang", required = false) Idioma lang,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/notificacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	public RespostaConsultaV2 notificacionsPendentsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@Parameter(name = "visibleCarpeta", description = "Filtrar per visible a carpeta. Si s'indica el valor si, només es retornaran enviaments amb estats visibles per la carpeta. Si s'indica el valor no, es retornaran tots els enviaments independentment de si els seus estats son visible o no a la carpeta.", content = { @Content(schema = @Schema( implementation = Boolean.class, defaultValue = "si", allowableValues = "[si, no]"))})
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@Parameter(name = "lang", description = "Idioma de les descripcions", content = { @Content(schema = @Schema( implementation = Idioma.class, defaultValue = "ca", allowableValues = "[ca, es]"))})
			@RequestParam (value = "lang", required = false) Idioma lang,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/comunicacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les comunicacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	public RespostaConsultaV2 comunicacionsLlegidesByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@Parameter(name = "visibleCarpeta", description = "Filtrar per visible a carpeta. Si s'indica el valor si, només es retornaran enviaments amb estats visibles per la carpeta. Si s'indica el valor no, es retornaran tots els enviaments independentment de si els seus estats son visible o no a la carpeta.", content = { @Content(schema = @Schema( implementation = Boolean.class, defaultValue = "si", allowableValues = "[si, no]"))})
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@Parameter(name = "lang", description = "Idioma de les descripcions", content = { @Content(schema = @Schema( implementation = Idioma.class, defaultValue = "ca", allowableValues = "[ca, es]"))})
			@RequestParam (value = "lang", required = false) Idioma lang,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/notificacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	public RespostaConsultaV2 notificacionsLlegidesByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", content = { @Content(schema = @Schema( implementation = Date.class, format = "date"))}, required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@Parameter(name = "visibleCarpeta", description = "Filtrar per visible a carpeta. Si s'indica el valor si, només es retornaran enviaments amb estats visibles per la carpeta. Si s'indica el valor no, es retornaran tots els enviaments independentment de si els seus estats son visible o no a la carpeta.", content = { @Content(schema = @Schema( implementation = Boolean.class, defaultValue = "si", allowableValues = "[si, no]"))})
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@Parameter(name = "lang", description = "Idioma de les descripcions", content = { @Content(schema = @Schema( implementation = Idioma.class, defaultValue = "ca", allowableValues = "[ca, es]"))})
			@RequestParam (value = "lang", required = false) Idioma lang,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació")
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació")
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/document/{notificacioId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté el document d'una notificació", description = "Retorna el document de la notificació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	public ResponseEntity<Arxiu> getDocument(
			HttpServletRequest request,
			@Parameter(description = "Identificador de la notificació de la que es vol obtenir el document", required = true)
			@PathVariable Long notificacioId);

	@GetMapping(value="/certificacio/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté la certificació d'una notificació", description = "Retorna el document de certificació de lectura de la notificació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	public ResponseEntity<Arxiu> getCertificacio(
			HttpServletRequest request,
			@Parameter(description = "Identificador de l'enviament de la que es vol obtenir la certificació", required = true)
			@PathVariable Long enviamentId);

	@GetMapping(value="/justificant/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté el justificant d'una comunicació", description = "Retorna el document de justificant de entrega de la comunicació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	public ResponseEntity<Arxiu> getJustificant(
			HttpServletRequest request,
			@Parameter(description = "Identificador de l'enviament de la que es vol obtenir el justificant", required = true)
			@PathVariable Long enviamentId);

}
