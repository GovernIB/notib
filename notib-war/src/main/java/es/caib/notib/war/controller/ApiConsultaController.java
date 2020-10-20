package es.caib.notib.war.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.rest.consulta.Arxiu;
import es.caib.notib.core.api.rest.consulta.Resposta;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;

@Controller
@RequestMapping("/api/consulta/v1")
@Api(value = "/rest/consulta", description = "API de consulta de comunicacions i notificacions")
public class ApiConsultaController {

	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	
	@RequestMapping(
			value="/comunicacions/{dniTitular}",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions d'un titular donat el seu dni",
			notes = "Retorna informació de totes les comunicacions d'un titular, i el seu estat",
			position = 0,
			response = Resposta.class,
			tags = "Comunicacions")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les comunicacions a consultar",
			required = true)
	@ResponseBody
	public Resposta comunicacionsByTitular(HttpServletRequest request, @PathVariable String dniTitular) {
		
		List<NotificacioEnviamentDto> enviaments = enviamentService.findComunicacionsByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/consulta/v1")
				.buildAndExpand().toUri();
		String basePath = location.toString();
		
		Resposta resposta = new Resposta();
		resposta.setNumeroElements(enviaments.size());
		resposta.setResultat(enviaments, basePath);
		return resposta;
	}
	
	@RequestMapping(
			value="/notificacions/{dniTitular}",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les notificacions d'un titular donat el seu dni",
			notes = "Retorna informació de totes les notificacions d'un titular, i el seu estat",
			response = Resposta.class,
			position = 3,
			tags = "Notificacions")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les notificacions a consultar",
			required = true)
	@ResponseBody
	public Resposta notificacionsByTitular(HttpServletRequest request, @PathVariable String dniTitular) {

		List<NotificacioEnviamentDto> enviaments = enviamentService.findNotificacionsByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/consulta/v1")
				.buildAndExpand().toUri();
		String basePath = location.toString();
		
		Resposta resposta = new Resposta();
		resposta.setNumeroElements(enviaments.size());
		resposta.setResultat(enviaments, basePath);
		return resposta;
	}
	
	@RequestMapping(
			value="/comunicacions/{dniTitular}/pendents",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions pendents (no llegides) d'un titular donat el seu dni",
			notes = "Retorna informació sobre les comunicacions pendents d'un titular, i el seu estat",
			response = Resposta.class,
			position = 1,
			tags = "Comunicacions")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les comunicacions a consultar",
			required = true)
	@ResponseBody
	public Resposta comunicacionsPendentsByTitular(HttpServletRequest request, @PathVariable String dniTitular) {
		
		List<NotificacioEnviamentDto> enviaments = enviamentService.findComunicacionsPendentsByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/consulta/v1")
				.buildAndExpand().toUri();
		String basePath = location.toString();
		
		Resposta resposta = new Resposta();
		resposta.setNumeroElements(enviaments.size());
		resposta.setResultat(enviaments, basePath);
		return resposta;
	}
	
	@RequestMapping(
			value="/notificacions/{dniTitular}/pendents",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les notificacions pendents (no llegides) d'un titular donat el seu dni",
			notes = "Retorna informació sobre les notificacions pendents d'un titular, i el seu estat",
			response = Resposta.class,
			position = 4,
			tags = "Notificacions")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les notificacions a consultar",
			required = true)
	@ResponseBody
	public Resposta notificacionsPendentsByTitular(HttpServletRequest request, @PathVariable String dniTitular) {

		List<NotificacioEnviamentDto> enviaments = enviamentService.findNotificacionsPendentsByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/consulta/v1")
				.buildAndExpand().toUri();
		String basePath = location.toString();
		
		Resposta resposta = new Resposta();
		resposta.setNumeroElements(enviaments.size());
		resposta.setResultat(enviaments, basePath);
		return resposta;
	}
	
	@RequestMapping(
			value="/comunicacions/{dniTitular}/llegides",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions llegides d'un titular donat el seu dni",
			notes = "Retorna informació sobre les comunicacions ja llegides d'un titular, i el seu estat",
			response = Resposta.class,
			position = 2,
			tags = "Comunicacions")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les comunicacions a consultar",
			required = true)
	@ResponseBody
	public Resposta comunicacionsLlegidesByTitular(HttpServletRequest request, @PathVariable String dniTitular) {

		List<NotificacioEnviamentDto> enviaments = enviamentService.findComunicacionsLlegidesByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/consulta/v1")
				.buildAndExpand().toUri();
		String basePath = location.toString();
		
		Resposta resposta = new Resposta();
		resposta.setNumeroElements(enviaments.size());
		resposta.setResultat(enviaments, basePath);
		return resposta;
	}
	
	@RequestMapping(
			value="/notificacions/{dniTitular}/llegides",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les notificacions llegides d'un titular donat el seu dni",
			notes = "Retorna informació sobre les notificacions ja llegides d'un titular, i el seu estat",
			response = Resposta.class,
			position = 5,
			tags = "Notificacions")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les notificacions a consultar",
			required = true)
	@ResponseBody
	public Resposta notificacionsLlegidesByTitular(HttpServletRequest request, @PathVariable String dniTitular) {

		List<NotificacioEnviamentDto> enviaments = enviamentService.findNotificacionsLlegidesByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/consulta/v1")
				.buildAndExpand().toUri();
		String basePath = location.toString();
		
		Resposta resposta = new Resposta();
		resposta.setNumeroElements(enviaments.size());
		resposta.setResultat(enviaments, basePath);
		return resposta;
	}
	
	@RequestMapping(
			value="/document/{notificacioId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(
			value = "Obté el document d'una notificació",
			notes = "Retorna el document de la notificació. El contingut del document està en Base64",
			response = Arxiu.class,
			position = 6,
			tags = "Documents")
	@ApiParam(
			name = "notificacioId",
			value = "Identificador de la notificació de la que es vol obtenir el document",
			required = true)
	@ResponseBody
	public Arxiu getDocument(HttpServletRequest request, @PathVariable Long notificacioId) {
		Arxiu document = null;
		ArxiuDto arxiu = notificacioService.getDocumentArxiu(notificacioId);
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
			document = Arxiu.builder()
					.nom(arxiu.getNom())
					.mediaType(arxiu.getContentType())
					.contingut(contingutDocumentBasse64).build();
		}
		return document;
	}
	
	@RequestMapping(
			value="/certificacio/{enviamentId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(
			value = "Obté la certificació d'una notificació",
			notes = "Retorna el document de certificació de lectura de la notificació. El contingut del document està en Base64",
			response = Arxiu.class,
			position = 7,
			tags = "Documents")
	@ApiParam(
			name = "notificacioId",
			value = "Identificador de l'enviament de la que es vol obtenir la certificació",
			required = true)
	@ResponseBody
	public Arxiu getCertificacio(HttpServletRequest request, @PathVariable Long enviamentId) {
		Arxiu certificacio = null;
		ArxiuDto arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
		if (arxiu != null && arxiu.getContingut() != null) {
			String contingutCertificacioBasse64 = Base64.encodeBase64String(arxiu.getContingut());
			certificacio = Arxiu.builder()
					.nom(arxiu.getNom())
					.mediaType(arxiu.getContentType())
					.contingut(contingutCertificacioBasse64).build();
		}
		return certificacio;
	}
	
	@RequestMapping(
			value="/justificant/{enviamentId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(
			value = "Obté el justificant d'una comunicació",
			notes = "Retorna el document de justificant de entrega de la comunicació. El contingut del document està en Base64",
			response = Arxiu.class,
			position = 8,
			tags = "Documents")
	@ApiParam(
			name = "notificacioId",
			value = "Identificador de l'enviament de la que es vol obtenir el justificant",
			required = true)
	@ResponseBody
	public Arxiu getJustificant(HttpServletRequest request, @PathVariable Long enviamentId) {
		Arxiu justificant = null;
		byte[] contingutJustificant = enviamentService.getDocumentJustificant(enviamentId);
		if (contingutJustificant != null) {
			String contingutJustificantBasse64 = Base64.encodeBase64String(contingutJustificant); 
			justificant = Arxiu.builder()
					.nom("Justificant")
					.mediaType(com.google.common.net.MediaType.PDF.toString())
					.contingut(contingutJustificantBasse64).build();
		}
		return justificant;
	}
	
}
