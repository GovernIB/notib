/**
 * 
 */
package es.caib.notib.war.controller;

import javax.ejb.EJBAccessException;
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

import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.util.UtilitatsNotib;
import es.caib.notib.core.api.ws.notificacio.DadesConsulta;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.core.api.ws.notificacio.NotificacioV2;
import es.caib.notib.core.api.ws.notificacio.PermisConsulta;
import es.caib.notib.core.api.ws.notificacio.RespostaAlta;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaDadesRegistre;
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
	private AplicacioService aplicacioService;
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
			value = "Registra i envia la notificació a Notific@.", 
			notes = "Retorna una llista amb els codis dels enviaments creats per poder consultar el seu estat posteriorment")
	@ResponseBody
	public RespostaAlta alta(
			@ApiParam(
					name = "notificacio",
					value = "Objecte amb les dades necessàries per a generar una notificació",
					required = true) 
			@RequestBody NotificacioV2 notificacio) {
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		try {
			return notificacioServiceWsV2.alta(notificacio);
		} catch (Exception e) {
			RespostaAlta resp = new RespostaAlta();
			resp.setError(true);
			if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
				resp.setErrorDescripcio("L'usuari " + usuariActualCodi + " no té els permisos necessaris: " + e.getMessage());
			} else {
				resp.setErrorDescripcio(UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(
						e, 
						EJBAccessException.class));
			}
			if (resp.getErrorDescripcio() != null)
				return resp;
			else 
				resp.setErrorDescripcio(e.getMessage());
			
			return resp;
		}
	}

	@RequestMapping(
			value = {"/services/notificacioV2/consultaEstatNotificacio/{identificador}"}, 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta de la informació d'una notificació",
			notes = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@",
			response = RespostaConsultaEstatNotificacio.class)
	@ResponseBody
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			@ApiParam(
					name = "identificador",
					value = "Identificador de la notificació a consultar",
					required = true)
			@PathVariable("identificador")
			String identificador) {
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		try {
			return notificacioServiceWsV2.consultaEstatNotificacio(identificador);
		} catch (Exception e) {
			RespostaConsultaEstatNotificacio resp = new RespostaConsultaEstatNotificacio();
			resp.setError(true);
			if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
				resp.setErrorDescripcio("L'usuari " + usuariActualCodi + " no té els permisos necessaris: " + e.getMessage());
			} else {
				resp.setErrorDescripcio(UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(
						e, 
						EJBAccessException.class));
			}
			if (resp.getErrorDescripcio() != null)
				return resp;
			else 
				resp.setErrorDescripcio(e.getMessage());
			
			return resp;
		}
	}

	@RequestMapping(
			value = {"/services/notificacioV2/consultaEstatEnviament/{referencia}"}, 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta la informació de l'estat d'un enviament dins Notific@",
			notes = "Retorna la informació sobre l'estat de l'enviament dins Notific@.",
			response = RespostaConsultaEstatEnviament.class)
	@ResponseBody
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			@ApiParam(
					name = "referencia",
					value = "Referència de la notificació a consultar",
					required = true)
			@PathVariable("referencia")
			String referencia) {
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		try {
			return notificacioServiceWsV2.consultaEstatEnviament(referencia);
		} catch (Exception e) {
			RespostaConsultaEstatEnviament resp = new RespostaConsultaEstatEnviament();
			resp.setError(true);
			if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
				resp.setErrorDescripcio("L'usuari " + usuariActualCodi + " no té els permisos necessaris: " + e.getMessage());
			} else {
				resp.setErrorDescripcio(UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(
						e, 
						EJBAccessException.class));
			}
			if (resp.getErrorDescripcio() != null)
				return resp;
			else 
				resp.setErrorDescripcio(e.getMessage());
			
			return resp;
		}
	}
	
	@RequestMapping(
			value = {"/services/notificacioV2/consultaDadesRegistre"}, 
			method = RequestMethod.POST,
			produces="application/json")
	@ApiOperation(
			value = "Genera el justificant i consulta la informació del registre d'una notificació.",
			notes = "Retorna la informació del registre i el justificant d'una notificació dins Notib.",
			response = RespostaConsultaDadesRegistre.class)
	@ResponseBody
	public RespostaConsultaDadesRegistre consultaDadesRegistre(
			@ApiParam(
					name = "dadesConsulta",
					value = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament",
					required = false)
			@RequestBody DadesConsulta dadesConsulta) {
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		try {
			return notificacioServiceWsV2.consultaDadesRegistre(dadesConsulta);
		} catch (Exception e) {
			RespostaConsultaDadesRegistre resp = new RespostaConsultaDadesRegistre();
			resp.setError(true);
			if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
				resp.setErrorDescripcio("L'usuari " + usuariActualCodi + " no té els permisos necessaris: " + e.getMessage());
			} else {
				resp.setErrorDescripcio(UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(
						e, 
						EJBAccessException.class));
			}
			if (resp.getErrorDescripcio() != null)
				return resp;
			else 
				resp.setErrorDescripcio(e.getMessage());
			
			return resp;
		}
	}
	
	
	@RequestMapping(
			value = "/services/notificacioV2/permisConsulta", 
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Donar permis de consulta a un usuari sobre un procediment", 
			notes = "Aquest mètode permet donar el permís de consulta a un usuari específic")
	@ResponseBody
	public String donarPermisConsulta(
			@ApiParam(
					name = "permisConsulta",
					value = "Objecte amb les dades necessàries per donar el permís",
					required = true) 
			@RequestBody PermisConsulta permisConsulta) {
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		String messatge = null;
		try {
			if (notificacioServiceWsV2.donarPermisConsulta(permisConsulta)) {
				messatge = "OK";
			}
			return messatge;
		} catch (Exception e) {
			if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
				messatge = "L'usuari " + usuariActualCodi + " no té els permisos necessaris: " + e.getMessage();
			} else {
				messatge = UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(
						e, 
						EJBAccessException.class);
			}
			return messatge;
		}
	}

}
