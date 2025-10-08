/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.client.domini.*;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Controlador del servei REST v1 per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Deprecated
@Controller
@RequestMapping("/api/services/notificacioV2")
public class NotificacioApiRestV1Controller extends NotificacioApiRestBaseController {


	@PostMapping(value = "/alta", produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public RespostaAlta alta(@RequestBody Notificacio notificacio, HttpServletRequest request, HttpServletResponse response) {

		try {
			var r =  notificacioServiceWs.alta(notificacio);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaAlta.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@GetMapping(value = {"/consultaEstatNotificacio/**"}, produces="application/json")
	@ResponseBody
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(HttpServletRequest request, HttpServletResponse response) {

		try {
			var identificador = extractIdentificador(request);
			if (identificador.isEmpty()) {
				var err = "No s'ha informat cap identificador de la notificació";
				return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			var r = notificacioServiceWs.consultaEstatNotificacio(identificador);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@GetMapping(value = {"/consultaEstatEnviament/**"}, produces="application/json")
	@ResponseBody
	public RespostaConsultaEstatEnviament consultaEstatEnviament(HttpServletRequest request, HttpServletResponse response) {

		var referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				var err = "No s'ha informat cap referència de l'enviament";
				logoutSession(request, response);
				return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			var r = notificacioServiceWs.consultaEstatEnviament(referencia);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@PostMapping(value = {"/consultaDadesRegistre"}, produces="application/json")
	@ResponseBody
	public RespostaConsultaDadesRegistre consultaDadesRegistre(@RequestBody DadesConsulta dadesConsulta, HttpServletRequest request, HttpServletResponse response) {

		try {
			var r = notificacioServiceWs.consultaDadesRegistre(dadesConsulta);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaDadesRegistre.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@GetMapping(value = {"/consultaJustificantNotificacio/**"}, produces="application/json")
	@ResponseBody
	public RespostaConsultaJustificantEnviament consultaJustificantV1(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@PostMapping(value = "/permisConsulta", produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public String donarPermisConsultaV1(@RequestBody PermisConsulta permisConsulta, HttpServletRequest request, HttpServletResponse response) {

		var r = donarPermisConsulta(permisConsulta);
		logoutSession(request, response);
		return r;
	}
}
