/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistre;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviament;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacio;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controlador del servei REST v1 per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Deprecated
@Hidden
@RestController
@RequestMapping("/api/services/notificacioV2")
public class NotificacioApiRestLegacyV1Controller extends NotificacioApiRestBaseController {

	@GetMapping(value = {})

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/alta", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaAlta alta(@Parameter(description = "Objecte amb les dades necessàries per a generar una notificació", required = true) @RequestBody Notificacio notificacio) {
		try {
			return notificacioServiceWs.alta(notificacio);
		} catch (Exception e) {
			return RespostaAlta.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@GetMapping(value = {"/consultaEstatNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(HttpServletRequest request) {

		var identificador = extractIdentificador(request);
		try {
			if (!identificador.isEmpty()) {
				return notificacioServiceWs.consultaEstatNotificacio(identificador);
			}
			var msg = "No s'ha informat cap identificador de la notificació";
			return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(msg).errorData(new Date()).build();
		} catch (Exception e) {
			return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@GetMapping(value = {"/consultaEstatEnviament/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaEstatEnviament consultaEstatEnviament(HttpServletRequest request) {

		var referencia = extractIdentificador(request);
		try {
			if (!referencia.isEmpty()) {
				return notificacioServiceWs.consultaEstatEnviament(referencia);
			}
			var msg = "No s'ha informat cap referència de l'enviament";
			return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(msg).errorData(new Date()).build();
		} catch (Exception e) {
			return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@PostMapping(value = {"/consultaDadesRegistre"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaDadesRegistre consultaDadesRegistre(@Parameter(description = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament")
			@RequestBody DadesConsulta dadesConsulta) {

		try {
			return notificacioServiceWs.consultaDadesRegistre(dadesConsulta);
		} catch (Exception e) {
			return RespostaConsultaDadesRegistre.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@GetMapping(value = {"/consultaJustificantNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaJustificantEnviament consultaJustificantV1(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/permisConsulta", produces = MediaType.APPLICATION_JSON_VALUE)
	public String donarPermisConsultaV1(@Parameter(description = "Objecte amb les dades necessàries per donar el permís", required = true)
										@RequestBody PermisConsulta permisConsulta) {

		return donarPermisConsulta(permisConsulta);
	}
}
