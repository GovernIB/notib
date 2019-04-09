/**
 * 
 */
package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWs;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.core.api.ws.notificacio.NotificacioV2;
import es.caib.notib.core.api.ws.notificacio.PermisConsulta;
import es.caib.notib.core.api.ws.notificacio.RespostaAlta;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatNotificacio;

/**
 * Controlador del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api")
@Api(value = "/services", description = "Notificacio API")
public class NotificacioServiceController extends BaseController {

	@Autowired
	private NotificacioServiceWs notificacioServiceWsV1;
	@Autowired
	private NotificacioServiceWsV2 notificacioServiceWsV2;

	@RequestMapping(value = {"/apidoc", "/rest"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "apidoc";
	}

	@RequestMapping(
			value = "/services/notificacioV2/alta", 
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Genera una notificació", 
			notes = "Retorna una llista amb els codis dels enviaments creats")
	@ResponseBody
	public RespostaAlta alta(
			@ApiParam(
					name = "notificacio",
					value = "Objecte amb les dades necessàries per a generar una notificació",
					required = true) 
			@RequestBody NotificacioV2 notificacio) {
		return notificacioServiceWsV2.alta(notificacio);
	}
	
	@RequestMapping(
			value = "/services/notificacio/alta", 
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Genera una notificació", 
			notes = "Retorna una llista amb els codis dels enviaments creats")
	@ResponseBody
	public RespostaAlta alta(
			@ApiParam(
					name = "notificacio",
					value = "Objecte amb les dades necessàries per a generar una notificació",
					required = true) 
			@RequestBody Notificacio notificacio) {
		return notificacioServiceWsV1.alta(notificacio);
	}

	@RequestMapping(
			value = {"/services/notificacio/consultaEstatNotificacio/{referencia}", "/services/notificacioV2/consultaEstatNotificacio/{referencia}"}, 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta de la informació d'una notificació",
			response = Notificacio.class)
	@ResponseBody
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			@ApiParam(
					name = "identificador",
					value = "Identificador de la notificació a consultar",
					required = true)
			@PathVariable("identificador")
			String identificador) {
		return notificacioServiceWsV1.consultaEstatNotificacio(identificador);
	}

	@RequestMapping(
			value = {"/services/notificacio/consultaEstatEnviament/{referencia}", "/services/notificacioV2/consultaEstatEnviament/{referencia}"}, 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta de la informació d'un enviament",
			response = Notificacio.class)
	@ResponseBody
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			@ApiParam(
					name = "referencia",
					value = "Referència de la notificació a consultar",
					required = true)
			@PathVariable("referencia")
			String referencia) {
		return notificacioServiceWsV1.consultaEstatEnviament(referencia);
	}
	
	@RequestMapping(
			value = "/services/notificacioV2/permisConsulta", 
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Donar permis de consulta sobre un procediment", 
			notes = "Retorna un string amb el valor de resposta (Permís creat / ERROR)")
	@ResponseBody
	public String donarPermisos(
			@ApiParam(
					name = "permisConsulta",
					value = "Objecte amb les dades necessàries per donar el permís",
					required = true) 
			@RequestBody PermisConsulta permisConsulta) {
		String messatge = null;
		
		if (notificacioServiceWsV2.donarPermisConsulta(permisConsulta)) {
			messatge = "OK";
		}
		return messatge;
	}

}
