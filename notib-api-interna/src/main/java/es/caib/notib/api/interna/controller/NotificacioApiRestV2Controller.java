/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.api.interna.openapi.interficies.NotificacioApiRestV2Intf;
import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Controlador del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RestController
@RequestMapping("/notificacio/v2")
public class NotificacioApiRestV2Controller extends NotificacioApiRestBaseController implements NotificacioApiRestV2Intf {

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/alta", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaAltaV2 alta(@RequestBody NotificacioV2 notificacio) {
		try {
			return notificacioServiceWs.altaV2(notificacio);
		} catch (Exception e) {
			return RespostaAltaV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@GetMapping(value = {"/consultaEstatNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacio(HttpServletRequest request) {

		var identificador = extractIdentificador(request);
		try {
			if (!identificador.isEmpty()) {
				return notificacioServiceWs.consultaEstatNotificacioV2(identificador);
			}
			var msg = "No s'ha informat cap identificador de la notificació";
			return RespostaConsultaEstatNotificacioV2.builder().error(true).errorDescripcio(msg).errorData(new Date()).build();
		} catch (Exception e) {
			return RespostaConsultaEstatNotificacioV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@GetMapping(value = {"/consultaEstatEnviament/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviament(HttpServletRequest request) throws UnsupportedEncodingException {

		var referencia = extractIdentificador(request);
		try {
			if (!referencia.isEmpty()) {
				return notificacioServiceWs.consultaEstatEnviamentV2(referencia);
			}
			var msg = "No s'ha informat cap referència de l'enviament";
			return RespostaConsultaEstatEnviamentV2.builder().error(true).errorDescripcio(msg).errorData(new Date()).build();
		} catch (Exception e) {
			return RespostaConsultaEstatEnviamentV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@PostMapping(value = {"/consultaDadesRegistre"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaDadesRegistreV2 consultaDadesRegistre(@RequestBody DadesConsulta dadesConsulta) {

		try {
			return notificacioServiceWs.consultaDadesRegistreV2(dadesConsulta);
		} catch (Exception e) {
			return RespostaConsultaDadesRegistreV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@GetMapping(value = {"/consultaJustificantNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaJustificantEnviament consultaJustificantV2(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/permisConsulta", produces = MediaType.APPLICATION_JSON_VALUE)
	public String donarPermisConsultaV2(@RequestBody PermisConsulta permisConsulta) {

		return donarPermisConsulta(permisConsulta);
	}
}
