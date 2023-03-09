package es.caib.notib.war.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.notib.core.api.dto.ApiConsulta;
import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.rest.consulta.Arxiu;
import es.caib.notib.core.api.rest.consulta.Resposta;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Date;

@Controller
@Slf4j
@RequestMapping("/api/consulta/v1")
@Api(value = "/rest/consulta", description = "API de consulta de comunicacions i notificacions")
public class ConsultaApiRestV1Controller extends  BaseController {

	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	
	@RequestMapping(value="/comunicacions/{dniTitular}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Consulta totes les comunicacions d'un titular donat el seu dni", position = 0, response = Resposta.class, tags = "Comunicacions",
			notes = "Retorna informació de totes les comunicacions d'un titular, i el seu estat")
	@ResponseBody
	public Resposta comunicacionsByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@ApiParam(name = "dataInicial", value = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@ApiParam(name = "dataFinal", value = "Datfa final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {
		
		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		String basePath = location.toString();
		ApiConsulta consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(null)
													.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		Resposta r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;

	}
	
	@RequestMapping(value="/notificacions/{dniTitular}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Consulta totes les notificacions d'un titular donat el seu dni", response = Resposta.class, position = 3, tags = "Notificacions",
			notes = "Retorna informació de totes les notificacions d'un titular, i el seu estat")
	@ApiParam(name = "dniTitular", value = "DNI del titular de les notificacions a consultar", required = true)
	@ResponseBody
	public Resposta notificacionsByTitular(
			HttpServletRequest request,HttpServletResponse response,
			@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@ApiParam(name = "dataInicial", value = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@ApiParam(name = "dataFinal", value = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		String basePath = location.toString();
		ApiConsulta consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(null)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		Resposta r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@RequestMapping(value="/comunicacions/{dniTitular}/pendents", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Consulta totes les comunicacions pendents (no llegides) d'un titular donat el seu dni",
			response = Resposta.class, position = 1, tags = "Comunicacions",
			notes = "Retorna informació sobre les comunicacions pendents d'un titular, i el seu estat")
	@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
	@ResponseBody
	public Resposta comunicacionsPendentsByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@ApiParam(name = "dataInicial", value = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@ApiParam(name = "dataFinal", value = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		String basePath = location.toString();
		ApiConsulta consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(false)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		Resposta r = enviamentService.findEnviaments(consulta);
		return r;
	}

	@RequestMapping(value="/notificacions/{dniTitular}/pendents", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Consulta totes les notificacions pendents (no llegides) d'un titular donat el seu dni",
			response = Resposta.class, position = 4, tags = "Notificacions",
			notes = "Retorna informació sobre les notificacions pendents d'un titular, i el seu estat")
	@ApiParam(name = "dniTitular", value = "DNI del titular de les notificacions a consultar", required = true)
	@ResponseBody
	public Resposta notificacionsPendentsByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@ApiParam(name = "dataInicial", value = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@ApiParam(name = "dataFinal", value = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		String basePath = location.toString();
		ApiConsulta consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(false)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		Resposta r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@RequestMapping(value="/comunicacions/{dniTitular}/llegides", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Consulta totes les comunicacions llegides d'un titular donat el seu dni", response = Resposta.class, position = 2, tags = "Comunicacions",
			notes = "Retorna informació sobre les comunicacions ja llegides d'un titular, i el seu estat")
	@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
	@ResponseBody
	public Resposta comunicacionsLlegidesByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@ApiParam(name = "dataInicial", value = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@ApiParam(name = "dataFinal", value = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		String basePath = location.toString();
		ApiConsulta consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(true)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		Resposta r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@RequestMapping(value="/notificacions/{dniTitular}/llegides", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Consulta totes les notificacions llegides d'un titular donat el seu dni", response = Resposta.class, position = 5, tags = "Notificacions",
			notes = "Retorna informació sobre les notificacions ja llegides d'un titular, i el seu estat")
	@ApiParam(name = "dniTitular", value = "DNI del titular de les notificacions a consultar", required = true)
	@ResponseBody
	public Resposta notificacionsLlegidesByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "dniTitular", value = "DNI del titular de les comunicacions a consultar", required = true)
			@PathVariable String dniTitular,
			@ApiParam(name = "dataInicial", value = "Data inicial d'enviament a consultar", required = false)
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@ApiParam(name = "dataFinal", value = "Data final d'enviament a consultar", required = false)
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) {

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/api/consulta/v1").buildAndExpand().toUri();
		String basePath = location.toString();
		ApiConsulta consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(true)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		Resposta r = enviamentService.findEnviaments(consulta);
		return r;
	}

	@RequestMapping(value="/document/{notificacioId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Obté el document d'una notificació", response = Arxiu.class, position = 6, tags = "Documents",
			notes = "Retorna el document de la notificació. El contingut del document està en Base64")
	@ApiParam(name = "notificacioId", value = "Identificador de la notificació de la que es vol obtenir el document", required = true)
	@ResponseBody
	public ResponseEntity<Arxiu> getDocument(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId) {

		Arxiu document = null;
		ArxiuDto arxiu = null;
		HttpStatus status = HttpStatus.OK;
		try {
			arxiu = notificacioService.getDocumentArxiu(notificacioId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el document per a la notificació amb identificador " + notificacioId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			if (arxiu.getContentType() == null) {
				if (arxiu.getNom() != null) {
					if (arxiu.getNom().endsWith(".pdf")) {
						arxiu.setContentType("application/pdf");
					} else if (arxiu.getNom().endsWith(".pdf")) {
						arxiu.setContentType("application/zip");
					}
				}
			}
			String contingutDocumentBasse64 = Base64.encodeBase64String(arxiu.getContingut());
			document = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutDocumentBasse64).build();
			ResponseEntity<Arxiu> r = new ResponseEntity<>(document, status);
			logoutSession(request, response);
			return r;
		}
		document = Arxiu.builder().error(true).missatgeError("No s'ha trobat el document.").build();
		status = HttpStatus.BAD_REQUEST;
		ResponseEntity<Arxiu> r = new ResponseEntity<>(document, status);
		logoutSession(request, response);
		return r;
	}

	@RequestMapping(value="/certificacio/{enviamentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Obté la certificació d'una notificació", response = Arxiu.class, position = 7, tags = "Documents",
			notes = "Retorna el document de certificació de lectura de la notificació. El contingut del document està en Base64")
	@ApiParam(name = "notificacioId", value = "Identificador de l'enviament de la que es vol obtenir la certificació", required = true)
	@ResponseBody
	public ResponseEntity<Arxiu> getCertificacio(HttpServletRequest request, HttpServletResponse response, @PathVariable Long enviamentId) {

		Arxiu certificacio = null;
		ArxiuDto arxiu = null;
		HttpStatus status = HttpStatus.OK;
		try {
			arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat la certificació per a l'enviament amb identificador " + enviamentId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			String contingutCertificacioBasse64 = Base64.encodeBase64String(arxiu.getContingut());
			certificacio = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutCertificacioBasse64).build();
			ResponseEntity<Arxiu> r = new ResponseEntity<>(certificacio, status);
			logoutSession(request, response);
			return r;
		}
		certificacio = Arxiu.builder().error(true).missatgeError("No s'ha trobat la certificació.").build();
		status = HttpStatus.BAD_REQUEST;
		ResponseEntity<Arxiu> r = new ResponseEntity<>(certificacio, status);
		logoutSession(request, response);
		return r;
	}

	@RequestMapping(value="/justificant/{enviamentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Obté el justificant d'una comunicació", response = Arxiu.class, position = 8, tags = "Documents",
			notes = "Retorna el document de justificant de entrega de la comunicació. El contingut del document està en Base64")
	@ApiParam(name = "notificacioId", value = "Identificador de l'enviament de la que es vol obtenir el justificant", required = true)
	@ResponseBody
	public ResponseEntity<Arxiu> getJustificant(HttpServletRequest request, HttpServletResponse response, @PathVariable Long enviamentId) {

		Arxiu justificant = null;
		byte[] contingutJustificant = null;
		HttpStatus status = HttpStatus.OK;
		try {
			contingutJustificant = enviamentService.getDocumentJustificant(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el justificant per a l'enviament amb identificador " + enviamentId);
		}
		if (contingutJustificant != null) {
			String contingutJustificantBasse64 = Base64.encodeBase64String(contingutJustificant);
			justificant = Arxiu.builder().nom("Justificant").mediaType(com.google.common.net.MediaType.PDF.toString()).contingut(contingutJustificantBasse64).build();
			ResponseEntity<Arxiu> r = new ResponseEntity<>(justificant, status);
			logoutSession(request, response);
			return r;
		}
		justificant = Arxiu.builder().error(true).missatgeError("No s'ha trobat el justificant.").build();
		status = HttpStatus.BAD_REQUEST;
		ResponseEntity<Arxiu> r = new ResponseEntity<>(justificant, status);
		logoutSession(request, response);
		return r;
	}
}
