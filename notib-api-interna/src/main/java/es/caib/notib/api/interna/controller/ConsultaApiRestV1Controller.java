package es.caib.notib.api.interna.controller;

import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.rest.consulta.Arxiu;
import es.caib.notib.logic.intf.rest.consulta.Resposta;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/consulta/v1")
@Tag(name = "Consulta v1", description = "API de consulta de comunicacions i notificacions v1")
public class ConsultaApiRestV1Controller {

	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	
	@GetMapping(value="/comunicacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions d'un titular donat el seu dni", description = "Retorna informació de totes les comunicacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Resposta.class, description = "Informació de comunicacions/notificacions"))})})
	public Resposta comunicacionsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@Parameter(name = "dataFinal", description = "Datfa final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {
		
		var location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(null)
						.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		return enviamentService.findEnviaments(consulta);
	}
	
	@GetMapping(value="/notificacions/{dniTitular}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions d'un titular donat el seu dni", description = "Retorna informació de totes les notificacions d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Resposta.class, description = "Informació de comunicacions/notificacions"))})})
	public Resposta notificacionsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@Parameter(name = "dataFinal", description = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(null)
								.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		return enviamentService.findEnviaments(consulta);
	}

	@GetMapping(value="/comunicacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les comunicacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Resposta.class, description = "Informació de comunicacions/notificacions"))})})
	public Resposta comunicacionsPendentsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@Parameter(name = "dataFinal", description = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(false)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		return enviamentService.findEnviaments(consulta);
	}

	@GetMapping(value="/notificacions/{dniTitular}/pendents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions pendents (no llegides) d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions pendents d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Resposta.class, description = "Informació de comunicacions/notificacions"))})})
	public Resposta notificacionsPendentsByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@Parameter(name = "dataFinal", description = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(false)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		return enviamentService.findEnviaments(consulta);
	}

	@GetMapping(value="/comunicacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les comunicacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les comunicacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Resposta.class, description = "Informació de comunicacions/notificacions"))})})
	public Resposta comunicacionsLlegidesByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@Parameter(name = "dataFinal", description = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(true)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		return enviamentService.findEnviaments(consulta);
	}

	@GetMapping(value="/notificacions/{dniTitular}/llegides", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta totes les notificacions llegides d'un titular donat el seu dni", description = "Retorna informació sobre les notificacions ja llegides d'un titular, i el seu estat")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Resposta.class, description = "Informació de comunicacions/notificacions"))})})
	public Resposta notificacionsLlegidesByTitular(
			HttpServletRequest request,
			@Parameter(name = "dniTitular", description = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@Parameter(name = "dataInicial", description = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@Parameter(name = "dataFinal", description = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@Parameter(name = "pagina", description = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@Parameter(name = "mida", description = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(true)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		return enviamentService.findEnviaments(consulta);
	}

	@GetMapping(value="/document/{notificacioId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté el document d'una notificació", description = "Retorna el document de la notificació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Arxiu.class, description = "Informació de comunicacions/notificacions"))})})
	public ResponseEntity<Arxiu> getDocument(HttpServletRequest request, @Parameter(description = "Identificador de la notificació de la que es vol obtenir el document", required = true) @PathVariable Long notificacioId) {

		Arxiu document;
		ArxiuDto arxiu = null;
		var status = HttpStatus.OK;
		try {
			arxiu = notificacioService.getDocumentArxiu(notificacioId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el document per a la notificació amb identificador " + notificacioId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			if (arxiu.getContentType() == null && arxiu.getNom() != null) {
				var type = arxiu.getNom().endsWith(".pdf") ? "application/pdf" : (arxiu.getNom().endsWith(".pdf") ? "application/zip" :null);
				arxiu.setContentType(type);
			}
			var contingutDocumentBasse64 = Base64.getEncoder().encodeToString(arxiu.getContingut());
			document = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutDocumentBasse64).build();
			return new ResponseEntity<>(document, status);
		}
		document = Arxiu.builder().error(true).missatgeError("No s'ha trobat el document.").build();
		status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(document, status);
	}

	@GetMapping(value="/certificacio/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté la certificació d'una notificació", description = "Retorna el document de certificació de lectura de la notificació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Arxiu.class, description = "Informació de comunicacions/notificacions"))})})
	public ResponseEntity<Arxiu> getCertificacio(HttpServletRequest request, @Parameter(description = "Identificador de l'enviament de la que es vol obtenir la certificació", required = true) @PathVariable Long enviamentId) {

		Arxiu certificacio;
		ArxiuDto arxiu = null;
		var status = HttpStatus.OK;
		try {
			arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat la certificació per a l'enviament amb identificador " + enviamentId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			var contingutCertificacioBasse64 = Base64.getEncoder().encodeToString(arxiu.getContingut());
			certificacio = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutCertificacioBasse64).build();
			return new ResponseEntity<>(certificacio, status);
		}
		certificacio = Arxiu.builder().error(true).missatgeError("No s'ha trobat la certificació.").build();
		status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<Arxiu>(certificacio, status);
	}

	@GetMapping(value="/justificant/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obté el justificant d'una comunicació", description = "Retorna el document de justificant de entrega de la comunicació. El contingut del document està en Base64")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Notificacions/Comunicacions per titular", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = Arxiu.class, description = "Informació de comunicacions/notificacions"))})})
	public ResponseEntity<Arxiu> getJustificant(HttpServletRequest request, @Parameter(description = "Identificador de l'enviament de la que es vol obtenir el justificant", required = true) @PathVariable Long enviamentId) {

		Arxiu justificant;
		byte[] contingutJustificant = null;
		var status = HttpStatus.OK;
		try {
			contingutJustificant = enviamentService.getDocumentJustificant(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el justificant per a l'enviament amb identificador " + enviamentId);
		}
		if (contingutJustificant != null) {
			var contingutJustificantBasse64 = Base64.getEncoder().encodeToString(contingutJustificant);
			justificant = Arxiu.builder().nom("Justificant").mediaType(com.google.common.net.MediaType.PDF.toString()).contingut(contingutJustificantBasse64).build();
			return new ResponseEntity<>(justificant, status);
		}
		justificant = Arxiu.builder().error(true).missatgeError("No s'ha trobat el justificant.").build();
		status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(justificant, status);
	}
}
