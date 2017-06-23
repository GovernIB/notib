/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstat;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsService;
import es.caib.notib.war.validation.RestPreconditions;

/**
 * Controlador que exposa un servei REST per a la gestio de
 * notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api")
@Api(value = "/notificacio", description = "Notificaio API")
@Produces("application/json")
@Consumes("application/json")
public class NotificacioServiceController extends BaseController {

//	private static final int RESPONSE_CODE_OK = 200;
//  private static final int RESPONSE_CODE_CREATED = 201;
//  private static final int RESPONSE_CODE_NOCONTENT = 204;
//  private static final int RESPONSE_CODE_NOTFOUND = 404;
    
	@Autowired
	private NotificacioWsService notificacioWSService;

	@RequestMapping(value = "/apidoc", method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

	@RequestMapping(
			value = "/services/altaEnviament", 
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Genera una notificació", 
//			response = String.class, 
//			responseContainer = "List",
			notes = "Retorna una llista amb els codis de les notificacions generades") //, response=ArrayList.class)
//    @ApiResponses({
//            @ApiResponse(code = RESPONSE_CODE_OK, message = "Notificació generada correctament")
//    })
	public @ResponseBody List<String> altaEnviament(
			@ApiParam(name="notificacio", value="Objecte amb les dades necessàries per a generar una notificació", required=true) 
			@RequestBody Notificacio notificacio) throws GeneralSecurityException, IOException {
		RestPreconditions.checkNotNull(notificacio);
		List<String> references = notificacioWSService.alta(notificacio);
		return references;
	}

	@RequestMapping(
			value = "/services/infoEnviament/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta una notificació",
			response = Notificacio.class)
	public @ResponseBody Notificacio infoEnviament(
			@ApiParam(name="referencia", value="Identificador de la notificació a consultar", required=true)
			@PathVariable("referencia") String referencia) throws UnsupportedEncodingException, IOException {
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consulta(referencia);
	}

	@RequestMapping(
			value = "/services/consultaEstat/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta l'estat d'una notificació",
			response = NotificacioEstat.class)
	public @ResponseBody NotificacioEstat consultaEstat(
			@ApiParam(name="referencia", value="Identificador de la notificació de la que es vol consultar el seu estat", required=true) 
			@PathVariable("referencia") String referencia) throws JsonProcessingException {
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consultaEstat(referencia);
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
			@PathVariable("referencia") String referencia) throws UnsupportedEncodingException, IOException {
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consultaCertificacio(referencia);
	}

}
