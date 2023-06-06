/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
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
 * Controlador del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/services/notificacioV22")
public class NotificacioApiRestV2Controller extends NotificacioApiRestBaseController {

	@RequestMapping(value = "/alta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public RespostaAltaV2 alta(@RequestBody NotificacioV2 notificacio, HttpServletRequest request, HttpServletResponse response) {

		try {
			var resposta = notificacioServiceWs.altaV2(notificacio);
			logoutSession(request, response);
			return resposta;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaAltaV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacio(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		var identificador = extractIdentificador(request);
		try {
			if (identificador.isEmpty()) {
				var err = "No s'ha informat cap identificador de la notificació";
				return RespostaConsultaEstatNotificacioV2.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			var not =  notificacioServiceWs.consultaEstatNotificacioV2(identificador);
			logoutSession(request, response);
			return not;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatNotificacioV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatEnviament/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviament(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		var referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				var err = "No s'ha informat cap referència de l'enviament";
				return RespostaConsultaEstatEnviamentV2.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			var resposta = notificacioServiceWs.consultaEstatEnviamentV2(referencia);
			logoutSession(request, response);
			return resposta;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatEnviamentV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaDadesRegistre"}, method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public RespostaConsultaDadesRegistreV2 consultaDadesRegistre(
			@RequestBody DadesConsulta dadesConsulta, HttpServletRequest request, HttpServletResponse response) {

		try {
			var resposta = notificacioServiceWs.consultaDadesRegistreV2(dadesConsulta);
			logoutSession(request, response);
			return resposta;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaDadesRegistreV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@RequestMapping(value = {"/consultaJustificantNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RespostaConsultaJustificantEnviament consultaJustificantV2(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@RequestMapping(value = "/permisConsulta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public String donarPermisConsultaV2(@RequestBody PermisConsulta permisConsulta, HttpServletRequest request, HttpServletResponse response) {

		var resposta = donarPermisConsulta(permisConsulta);
		logoutSession(request, response);
		return resposta;
	}
}
