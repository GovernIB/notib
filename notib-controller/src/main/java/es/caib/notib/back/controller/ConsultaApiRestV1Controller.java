package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.rest.consulta.Arxiu;
import es.caib.notib.logic.intf.rest.consulta.Resposta;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
@Slf4j
@RequestMapping("/api/consulta/v1")
public class ConsultaApiRestV1Controller extends  BaseController {

	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	private static final String PATH = "/api/consulta/v1";


	@GetMapping(value="/comunicacions/{dniTitular}", produces = "application/json")
	@ResponseBody
	public Resposta comunicacionsByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {
		
		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(null)
					.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		var r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;

	}
	
	@GetMapping(value="/notificacions/{dniTitular}", produces = "application/json")
	@ResponseBody
	public Resposta notificacionsByTitular(
			HttpServletRequest request,HttpServletResponse response,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(null)
					.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		var r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/comunicacions/{dniTitular}/pendents", produces = "application/json")
	@ResponseBody
	public Resposta comunicacionsPendentsByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(false)
					.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		var r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/notificacions/{dniTitular}/pendents", produces = "application/json")
	@ResponseBody
	public Resposta notificacionsPendentsByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(false)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		var r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/comunicacions/{dniTitular}/llegides", produces = "application/json")
	@ResponseBody
	public Resposta comunicacionsLlegidesByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.COMUNICACIO).estatFinal(true)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		var r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/notificacions/{dniTitular}/llegides", produces = "application/json")
	@ResponseBody
	public Resposta notificacionsLlegidesByTitular(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable String dniTitular,
			@RequestParam (value = "dataInicial", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInicial,
			@RequestParam (value = "dataFinal", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFinal,
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@RequestParam(value = "mida", required = false) Integer mida) {

		var location = ServletUriComponentsBuilder.fromServletMapping(request).path(PATH).buildAndExpand().toUri();
		var basePath = location.toString();
		var consulta = ApiConsulta.builder().dniTitular(dniTitular).tipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO).estatFinal(true)
				.basePath(basePath).pagina(pagina).mida(mida).dataInicial(dataInicial).dataFinal(dataFinal).build();
		var r = enviamentService.findEnviaments(consulta);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/document/{notificacioId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Arxiu> getDocument(HttpServletRequest request, HttpServletResponse response, @PathVariable Long notificacioId) {

		Arxiu document = null;
		ArxiuDto arxiu = null;
		var status = HttpStatus.OK;
		try {
			arxiu = notificacioService.getDocumentArxiu(notificacioId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el document per a la notificació amb identificador " + notificacioId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			if (arxiu.getContentType() == null && arxiu.getNom() != null) {
					if (arxiu.getNom().endsWith(".pdf")) {
						arxiu.setContentType("application/pdf");
					} else if (arxiu.getNom().endsWith(".zip")) {
						arxiu.setContentType("application/zip");
					}

			}
			var contingutDocumentBasse64 = Base64.encodeBase64String(arxiu.getContingut());
			document = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutDocumentBasse64).build();
			var r = new ResponseEntity<>(document, status);
			logoutSession(request, response);
			return r;
		}
		document = Arxiu.builder().error(true).missatgeError("No s'ha trobat el document.").build();
		status = HttpStatus.BAD_REQUEST;
		var r = new ResponseEntity<>(document, status);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/certificacio/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Arxiu> getCertificacio(HttpServletRequest request, HttpServletResponse response, @PathVariable Long enviamentId) {

		Arxiu certificacio = null;
		ArxiuDto arxiu = null;
		var status = HttpStatus.OK;
		try {
			arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat la certificació per a l'enviament amb identificador " + enviamentId);
		}
		if (arxiu != null && arxiu.getContingut() != null) {
			var contingutCertificacioBasse64 = Base64.encodeBase64String(arxiu.getContingut());
			certificacio = Arxiu.builder().nom(arxiu.getNom()).mediaType(arxiu.getContentType()).contingut(contingutCertificacioBasse64).build();
			var r = new ResponseEntity<>(certificacio, status);
			logoutSession(request, response);
			return r;
		}
		certificacio = Arxiu.builder().error(true).missatgeError("No s'ha trobat la certificació.").build();
		status = HttpStatus.BAD_REQUEST;
		var r = new ResponseEntity<>(certificacio, status);
		logoutSession(request, response);
		return r;
	}

	@GetMapping(value="/justificant/{enviamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Arxiu> getJustificant(HttpServletRequest request, HttpServletResponse response, @PathVariable Long enviamentId) {

		Arxiu justificant = null;
		byte[] contingutJustificant = null;
		var status = HttpStatus.OK;
		try {
			contingutJustificant = enviamentService.getDocumentJustificant(enviamentId);
		} catch (Exception e) {
			log.debug("No s'ha trobat el justificant per a l'enviament amb identificador " + enviamentId);
		}
		if (contingutJustificant != null) {
			var contingutJustificantBasse64 = Base64.encodeBase64String(contingutJustificant);
			justificant = Arxiu.builder().nom("Justificant").mediaType(com.google.common.net.MediaType.PDF.toString()).contingut(contingutJustificantBasse64).build();
			var r = new ResponseEntity<>(justificant, status);
			logoutSession(request, response);
			return r;
		}
		justificant = Arxiu.builder().error(true).missatgeError("No s'ha trobat el justificant.").build();
		status = HttpStatus.BAD_REQUEST;
		var r = new ResponseEntity<>(justificant, status);
		logoutSession(request, response);
		return r;
	}
}
