/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import es.caib.notib.core.api.ws.notificacio.InformacioEnviament;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWs;
import es.caib.notib.war.validation.RestPreconditions;

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
	private NotificacioServiceWs notificacioServiceWs;

	@RequestMapping(value = "/apidoc", method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "apidoc";
	}

	@RequestMapping(
			value = "/services/notificacio/alta", 
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Genera una notificació", 
			notes = "Retorna una llista amb els codis dels enviaments creats")
	public @ResponseBody List<String> alta(
			@ApiParam(name="notificacio", value="Objecte amb les dades necessàries per a generar una notificació", required=true) 
			@RequestBody Notificacio notificacio,
			HttpServletResponse response) throws GeneralSecurityException, IOException {
		RestPreconditions.checkNotNull(notificacio);
		List<String> resposta = notificacioServiceWs.alta(
				notificacio);
		return resposta;
	}

	@RequestMapping(
			value = "/services/notificacio/consulta/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta una notificació",
			response = Notificacio.class)
	public @ResponseBody InformacioEnviament consulta(
			@ApiParam(name="referencia", value="Referència de la notificació a consultar", required=true)
			@PathVariable("referencia")
			String referencia,
			HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		RestPreconditions.checkNotNull(referencia);
		InformacioEnviament informacio = notificacioServiceWs.consulta(referencia);
		if (informacio == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return informacio;
	}

	/*@RequestMapping(
			value = "/services/consultaEstat/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta l'estat d'una notificació",
			response = NotificacioEstat.class)
	public @ResponseBody NotificacioEstatDto consultaEstat(
			@ApiParam(name="referencia", value="Identificador de la notificació de la que es vol consultar el seu estat", required=true) 
			@PathVariable("referencia") String referencia,
			HttpServletResponse response) throws IOException {
		RestPreconditions.checkNotNull(referencia);
		NotificacioEstat estat = notificacioService.cons.consultaEstat(referencia);
		if (estat == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return estat;
	}

	@RequestMapping(
			value = "/services/consultaCertificacio/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta la certificació d'una notificació",
			response = NotificacioCertificacio.class)
	public @ResponseBody NotificacioCertificacio consultaCertificacio(
			@ApiParam(name="referencia", value="Identificador de la notificació de la que es vol consultar la seva certificació", required=true)
			@PathVariable("referencia") String referencia,
			HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		RestPreconditions.checkNotNull(referencia);
		NotificacioCertificacio certificacio = notificacioService.consultaCertificacio(referencia);
		if (certificacio == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return certificacio;
	}*/

}
