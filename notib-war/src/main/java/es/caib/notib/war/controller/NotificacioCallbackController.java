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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import es.caib.notib.core.api.ws.notificacio.Notificacio; 
import es.caib.notib.core.api.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstat;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsService;
import es.caib.notib.war.validation.RestPreconditions;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/notificacio")
public class NotificacioCallbackController extends BaseController {
	
	@Autowired 
	private NotificacioWsService notificacioWSService;
	

	@RequestMapping(value = "/documentacioRest", method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}
	
	// Server

	@RequestMapping(
			value = "/rest/altaEnviament", 
			method = RequestMethod.POST,
//			headers = "application/xml, application/json",
			produces = {"application/json", "application/xml" },
			consumes = {"application/json", "application/xml" }
			)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public List<String> altaEnviament(@RequestBody Notificacio notificacio) throws GeneralSecurityException, IOException {
		
		// Comprovar la seguretat
		// fer un registre de la petició
//		try {
			// cridar a notific@
//		}catch(Exception e) {
//			
//		}
		// enregistrar el resultat
		// retornar el resultat
		
		
		RestPreconditions.checkNotNull(notificacio);
		List<String> references = notificacioWSService.alta(notificacio);
		return references;
		
	}
	
	@RequestMapping(
			value = "/rest/infoEnviament/{referencia}", 
			method = RequestMethod.GET, 
			headers = "Accept=application/xml, application/json", 
			produces = {"application/json", "application/xml" })
	@ResponseBody
	public Notificacio infoEnviament(@PathVariable( "referencia" ) String referencia) throws UnsupportedEncodingException, IOException {
		
		
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consulta(referencia);
		
	}
	
	
	@RequestMapping(
			value = "/rest/consultaEstat/{referencia}", 
			method = RequestMethod.GET, 
			headers = "Accept=application/xml, application/json", 
			produces = {"application/json", "application/xml" })
	@ResponseBody
	public NotificacioEstat consultaEstat(@PathVariable( "referencia" ) String referencia) throws JsonProcessingException {
		
		
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consultaEstat(referencia);
		
	}
	
	
//	@RequestMapping(
//			value = "/consultaDatat/{referencia}", 
//			method = RequestMethod.GET, 
//			headers = "Accept=application/xml, application/json", 
//			produces = {"application/json", "application/xml" })
//	@ResponseBody
//	public InfoDatat consultaDatat(@PathVariable( "referencia" ) String referencia) {
//		RestPreconditions.checkNotNull(referencia);
//		return null;
//	}
	
	
	@RequestMapping(
			value = "/rest/consultaCertificacio/{referencia}", 
			method = RequestMethod.GET, 
			headers = "Accept=application/xml, application/json", 
			produces = {"application/json", "application/xml" })
	@ResponseBody
	public NotificacioCertificacio consultaCertificacio(@PathVariable( "referencia" ) String referencia) throws UnsupportedEncodingException, IOException {
		
		
		RestPreconditions.checkNotNull(referencia);
		return notificacioWSService.consultaCertificacio(referencia);
		
	}
	
	
//	@RequestMapping(
//			value = "/comunicacioSeu", 
//			method = RequestMethod.POST, 
//			headers = "Accept=application/xml, application/json", 
//			produces = {"application/json", "application/xml" },
//			consumes = {"application/json", "application/xml" })
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public String comunicacioSeu(@RequestBody LecturaNotificacio lecturaNotificacio) {
//		RestPreconditions.checkNotNull(lecturaNotificacio);
//		return null;
//	}
	
//	// Client
//
//	public String datatOrganisme(DatatOrganisme datatOrganisme) {
//		RestTemplate restTemplate = new RestTemplate();
//		String response = restTemplate.postForObject("url_aplicacio_client/datatOrganisme", datatOrganisme , String.class);
//		return response;
//	}
//	
//	public String certificatOrganisme(CertificatOrganisme certificatOrganisme) {
//		RestTemplate restTemplate = new RestTemplate();
//		String response = restTemplate.postForObject("url_aplicacio_client/certificatOrganisme", certificatOrganisme , String.class);
//		return response;
//	}
}
