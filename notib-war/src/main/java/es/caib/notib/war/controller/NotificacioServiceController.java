/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

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
public class NotificacioServiceController extends BaseController {

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
	public @ResponseBody List<String> altaEnviament(
			@RequestBody Notificacio notificacio) throws GeneralSecurityException, IOException {
		RestPreconditions.checkNotNull(notificacio);
		List<String> references = notificacioWSService.alta(notificacio);
		return references;
	}

	@RequestMapping(
			value = "/services/infoEnviament/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	public @ResponseBody Notificacio infoEnviament(
			@PathVariable("referencia") String referencia) throws UnsupportedEncodingException, IOException {
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consulta(referencia);
	}

	@RequestMapping(
			value = "/services/consultaEstat/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	public @ResponseBody NotificacioEstat consultaEstat(
			@PathVariable("referencia") String referencia) throws JsonProcessingException {
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consultaEstat(referencia);
	}

	@RequestMapping(
			value = "/services/consultaCertificacio/{referencia}", 
			method = RequestMethod.GET,
			produces="application/json")
	public @ResponseBody NotificacioCertificacio consultaCertificacio(
			@PathVariable("referencia") String referencia) throws UnsupportedEncodingException, IOException {
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consultaCertificacio(referencia);
	}

}
