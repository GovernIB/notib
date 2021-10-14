/**
 * 
 */
package es.caib.notib.war.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.notib.core.api.rest.consulta.AppInfo;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.util.UtilitatsNotib;
import es.caib.notib.core.api.ws.notificacio.*;
import es.caib.notib.war.interceptor.AplicacioInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.ejb.EJBAccessException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = {"/rest/appinfo"}, method = RequestMethod.GET)
	@ResponseBody
	public AppInfo getAppInfo(HttpServletRequest request) {
		AppInfo appInfo = new AppInfo();
		appInfo.setNom("Notib");
		Map<String, Object> manifestAtributsMap = (Map<String, Object>)request.getAttribute(
				AplicacioInterceptor.REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES);
		appInfo.setVersio(manifestAtributsMap.get("Implementation-Version").toString());
		appInfo.setData(manifestAtributsMap.get("Release-Date").toString());
		return appInfo;
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
			value = {"/services/notificacioV2/consultaEstatNotificacio/**"}, 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta de la informació d'una notificació",
			notes = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@",
			response = RespostaConsultaEstatNotificacio.class)
	@ApiParam(
			name = "identificador",
			value = "Identificador de la notificació a consultar",
			required = true)
	@ResponseBody
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			HttpServletRequest request) throws UnsupportedEncodingException {
		RespostaConsultaEstatNotificacio resp = new RespostaConsultaEstatNotificacio();
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		String identificador = extractIdentificador(request);
		try {
			if (identificador.isEmpty()) {
				resp.setError(true);
				resp.setErrorDescripcio("No s'ha informat cap identificador de la notificació");
				resp.setErrorData(new Date());
				return resp;
			}
			return notificacioServiceWsV2.consultaEstatNotificacio(identificador);
		} catch (Exception e) {
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
			value = {"/services/notificacioV2/consultaEstatEnviament/**"}, 
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta la informació de l'estat d'un enviament dins Notific@",
			notes = "Retorna la informació sobre l'estat de l'enviament dins Notific@.",
			response = RespostaConsultaEstatEnviament.class)
	@ApiParam(
			name = "referencia",
			value = "Referència de la notificació a consultar",
			required = true)
	@ResponseBody
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			HttpServletRequest request) throws UnsupportedEncodingException {
		RespostaConsultaEstatEnviament resp = new RespostaConsultaEstatEnviament();
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				resp.setError(true);
				resp.setErrorDescripcio("No s'ha informat cap referència de l'enviament");
				resp.setErrorData(new Date());
				return resp;
			}
			return notificacioServiceWsV2.consultaEstatEnviament(referencia);
		} catch (Exception e) {
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
	
	private String extractIdentificador(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String[] urlArr = url.split("/consultaEstatNotificacio|/consultaEstatEnviament|/consultaJustificantNotificacio");
		String referencia = urlArr.length > 1 ? urlArr[1].substring(1) : "";
		return referencia;
//		####MÈTODE ANTERIOR PER EXTREURE REFERÈNCIA [NO FUNCIONA SI LA REFERÈNCIA CONTÉ MÉS D'UNA BARRA]
//		####EMPRAR MÈTODE ANTERIOR O TODO: CANVIAR GENERACIÓ REFERÈNCIA PER NO INCLOURE BARRES
//		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
//		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//	 
//		return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
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

	@RequestMapping(
			value = {"/services/notificacioV2/consultaJustificantNotificacio/**"},
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta el justificant de l'enviament d'una notificació",
			notes = "Retorna el document PDF amb el justificant de l'enviament de la notificació",
			response = RespostaConsultaJustificant.class)
	@ApiParam(
			name = "identificador",
			value = "Identificador de la notificació a consultar",
			required = true)
	@ResponseBody
	public RespostaConsultaJustificant consultaJustificant(HttpServletRequest request) {
		RespostaConsultaJustificant resp = new RespostaConsultaJustificant();
		String usuariActualCodi = aplicacioService.getUsuariActual().getCodi();
		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				resp.setError(true);
				resp.setErrorDescripcio("No s'ha informat cap identificador de la notificació");
				resp.setErrorData(new Date());
				return resp;
			}
			return notificacioServiceWsV2.consultaJustificantEnviament(referencia);
		} catch (Exception e) {
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
}
