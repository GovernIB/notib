package es.caib.notib.war.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.rest.consulta.Comunicacio;
import es.caib.notib.core.api.rest.consulta.Notificacio;
import es.caib.notib.core.api.service.EnviamentService;

@Controller
@RequestMapping("/api/rest/v1/consulta")
@Api(value = "/rest/consulta", description = "API de consulta de comunicacions i notificacions")
public class ApiConsultaController {

	@Autowired
	private EnviamentService enviamentService;
	
	@RequestMapping(
			value="/comunicacions/{dniTitular}",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions d'un titular donat el seu dni",
			notes = "Retorna informació de totes les comunicacions d'un titular, i el seu estat",
			response = Comunicacio.class,
			responseContainer = "List")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les comunicacions a consultar",
			required = true)
	@ResponseBody
	public List<Comunicacio> comunicacionsByTitular(HttpServletRequest request, @PathVariable String dniTitular) {
		
		List<NotificacioEnviamentDto> notificacions = enviamentService.findComunicacionsByNif(dniTitular);
		
		URI location = ServletUriComponentsBuilder
				.fromServletMapping(request)
				.path("/api/rest/v1/consulta")
				.buildAndExpand().toUri();
		
//		https://dehu.redsara.es/login
//		URI location = ServletUriComponentsBuilder
////				.fromCurrentRequest().
//				.fromServletMapping(request)
//				.path("/document/{id}")
//				.buildAndExpand(document.getId()).toUri();
		return null;
	}
	
	@RequestMapping(
			value="/notificacions/{dniTitular}",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les notificacions d'un titular donat el seu dni",
			notes = "Retorna informació de totes les notificacions d'un titular, i el seu estat",
			response = Notificacio.class,
			responseContainer = "List")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les notificacions a consultar",
			required = true)
	@ResponseBody
	public List<Notificacio> notificacionsByTitular(@PathVariable String dniTitular) {
		return null;
	}
	
	@RequestMapping(
			value="/comunicacions/{dniTitular}/pendents",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions pendents (no llegides) d'un titular donat el seu dni",
			notes = "Retorna informació sobre les comunicacions pendents d'un titular, i el seu estat",
			response = Comunicacio.class,
			responseContainer = "List")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les comunicacions a consultar",
			required = true)
	@ResponseBody
	public List<Comunicacio> comunicacionsPendentsByTitular(@PathVariable String dniTitular) {
		return null;
	}
	
	@RequestMapping(
			value="/notificacions/{dniTitular}/pendents",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les notificacions pendents (no llegides) d'un titular donat el seu dni",
			notes = "Retorna informació sobre les notificacions pendents d'un titular, i el seu estat",
			response = Notificacio.class,
			responseContainer = "List")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les notificacions a consultar",
			required = true)
	@ResponseBody
	public List<Notificacio> notificacionsPendentsByTitular(@PathVariable String dniTitular) {
		return null;
	}
	
	@RequestMapping(
			value="/comunicacions/{dniTitular}/llegides",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions llegides d'un titular donat el seu dni",
			notes = "Retorna informació sobre les comunicacions ja llegides d'un titular, i el seu estat",
			response = Comunicacio.class,
			responseContainer = "List")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les comunicacions a consultar",
			required = true)
	@ResponseBody
	public List<Comunicacio> comunicacionsLlegidesByTitular(@PathVariable String dniTitular) {
		return null;
	}
	
	@RequestMapping(
			value="/notificacions/{dniTitular}/llegides",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les notificacions llegides d'un titular donat el seu dni",
			notes = "Retorna informació sobre les notificacions ja llegides d'un titular, i el seu estat",
			response = Notificacio.class,
			responseContainer = "List")
	@ApiParam(
			name = "dniTitular",
			value = "DNI del titular de les notificacions a consultar",
			required = true)
	@ResponseBody
	public List<Notificacio> notificacionsLlegidesByTitular(@PathVariable String dniTitular) {
		return null;
	}
	
	@RequestMapping(
			value="/document/{notificacioId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(
			value = "Consulta el document d'una notificació",
			notes = "Retorna el document de la notificació",
			response = byte[].class)
	@ApiParam(
			name = "notificacioId",
			value = "Identificador de la notificació de la que es vol obtenir el document",
			required = true)
	@ResponseBody
	public byte[] getDocument(@PathVariable Long notificacioId) {
		return null;
	}
	
	@RequestMapping(
			value="/certificacio/{enviamentId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(
			value = "Consulta la certificació d'una notificació",
			notes = "Retorna el document de certificació de lectura de la notificació",
			response = byte[].class)
	@ApiParam(
			name = "notificacioId",
			value = "Identificador de l'enviament de la que es vol obtenir la certificació",
			required = true)
	@ResponseBody
	public byte[] getCertificacio(@PathVariable Long enviamentId) {
		return null;
	}
}
