package es.caib.notib.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.notib.core.api.rest.consulta.Comunicacio;
import es.caib.notib.core.api.rest.consulta.Notificacio;

@Controller
@RequestMapping("/api/rest/consulta")
@Api(value = "/rest/consulta", description = "API de consulta de comunicacions i notificacions")
public class ApiConsultaController {

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
}
