/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistre;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviament;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacio;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Controlador del servei REST v1 per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/services/notificacioV2")
public class NotificacioApiRestV1Controller extends NotificacioApiRestBaseController {

	@RequestMapping(value = "/alta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public RespostaAlta alta(@RequestBody NotificacioV2 notificacio, HttpServletRequest request, HttpServletResponse response) {

		try {
			RespostaAlta r =  notificacioServiceWs.alta(notificacio);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaAlta.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		try {
			String identificador = extractIdentificador(request);
			if (identificador.isEmpty()) {
				String err = "No s'ha informat cap identificador de la notificació";
				return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			RespostaConsultaEstatNotificacio r = notificacioServiceWs.consultaEstatNotificacio(identificador);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatEnviament/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespostaConsultaEstatEnviament consultaEstatEnviament(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				String err = "No s'ha informat cap referència de l'enviament";
				logoutSession(request, response);
				return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			RespostaConsultaEstatEnviament r = notificacioServiceWs.consultaEstatEnviament(referencia);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaDadesRegistre"}, method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RespostaConsultaDadesRegistre consultaDadesRegistre(@RequestBody DadesConsulta dadesConsulta, HttpServletRequest request, HttpServletResponse response) {

		try {
			RespostaConsultaDadesRegistre r = notificacioServiceWs.consultaDadesRegistre(dadesConsulta);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaDadesRegistre.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@RequestMapping(value = {"/consultaJustificantNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespostaConsultaJustificantEnviament consultaJustificantV1(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@RequestMapping(value = "/permisConsulta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public String donarPermisConsultaV1(@RequestBody PermisConsulta permisConsulta, HttpServletRequest request, HttpServletResponse response) {
		String r = donarPermisConsulta(permisConsulta);
		logoutSession(request, response);
		return r;
	}
}
