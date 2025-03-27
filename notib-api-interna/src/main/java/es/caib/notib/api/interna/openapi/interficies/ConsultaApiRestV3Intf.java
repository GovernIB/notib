package es.caib.notib.api.interna.openapi.interficies;

import es.caib.notib.api.interna.openapi.model.consulta.ArxiuApi;
import es.caib.notib.api.interna.openapi.model.consulta.RespostaConsultaV2Api;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.consulta.Arxiu;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import io.swagger.v3.oas.annotations.Operation;
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

@RequestMapping("/consulta/v3")
@Tag(name = "Consulta v3", description = "API de consulta v3")
public interface ConsultaApiRestV3Intf {

	@GetMapping(value="/enviaments/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes notificacions i comunicacions del titular donat el seu dni", description = "Retorna informació de totes les notificacions i comunicacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions i comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de notificacions i comunicacions"))})})
	RespostaConsultaV2 enviamnetsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/comunicacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions d'un titular donat el seu dni", description = "Retorna informació de totes les comunicacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	RespostaConsultaV2 comunicacionsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/notificacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions d'un titular donat el seu dni", description = "Retorna informació de totes les notificacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	RespostaConsultaV2 notificacionsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/enviaments/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions i comunicacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions i comunicacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions i comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació deles notificacions i omunicacions"))})})
	RespostaConsultaV2 enviamentsPendentsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/comunicacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les comunicacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	RespostaConsultaV2 comunicacionsPendentsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/notificacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	RespostaConsultaV2 notificacionsPendentsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/enviaments/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions i comunicacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions i comunicacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions i comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de les notificacions i comunicacions"))})})
	RespostaConsultaV2 enviamentsLlegitsByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);


	@GetMapping(value="/comunicacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les comunicacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	RespostaConsultaV2 comunicacionsLlegidesByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/notificacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = RespostaConsultaV2Api.class, description = "Informació de comunicacions/notificacions"))})})
	RespostaConsultaV2 notificacionsLlegidesByTitular(
			HttpServletRequest request,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy", fallbackPatterns = {"yyyy-MM-dd"}) Date dataFinal,
			@RequestParam (value = "visibleCarpeta", required = false) Boolean visibleCarpeta,
			@RequestParam (value = "lang", required = false) Idioma lang,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida);

	@GetMapping(value="/document/{notificacioId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté el document d'una notificació", description = "Retorna el document de la notificació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	ResponseEntity<Arxiu> getDocument(HttpServletRequest request, @PathVariable Long notificacioId);

	@GetMapping(value="/certificacio/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté la certificació d'una notificació", description = "Retorna el document de certificació de lectura de la notificació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	ResponseEntity<Arxiu> getCertificacio(HttpServletRequest request, @PathVariable Long enviamentId);

	@GetMapping(value="/justificant/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté el justificant d'una comunicació", description = "Retorna el document de justificant de entrega de la comunicació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = ArxiuApi.class, description = "Informació de comunicacions/notificacions"))})})
	ResponseEntity<Arxiu> getJustificant(HttpServletRequest request, @PathVariable Long enviamentId);

}
