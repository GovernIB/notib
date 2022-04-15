/**
 * 
 */
package es.caib.notib.war.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
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
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Controlador del servei REST v1 per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/services/notificacioV2")
@Api(value = "/services/v1", description = "Notificacio API v1")
public class NotificacioApiRestV1Controller extends NotificacioApiRestBaseController {

	@RequestMapping(
			value = "/alta",
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

		try {
			return notificacioServiceWsV2.alta(notificacio);
		} catch (Exception e) {
			return RespostaAlta.builder()
					.error(true)
					.errorDescripcio(getErrorDescripcio(e))
					.build();
		}
	}

	@RequestMapping(
			value = {"/consultaEstatNotificacio/**"},
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

		String identificador = extractIdentificador(request);
		try {
			if (identificador.isEmpty()) {
				return RespostaConsultaEstatNotificacio.builder()
						.error(true)
						.errorDescripcio("No s'ha informat cap identificador de la notificació")
						.errorData(new Date())
						.build();
			}
			return notificacioServiceWsV2.consultaEstatNotificacio(identificador);
		} catch (Exception e) {
			return RespostaConsultaEstatNotificacio.builder()
					.error(true)
					.errorDescripcio(getErrorDescripcio(e))
					.errorData(new Date())
					.build();
		}
	}

	@RequestMapping(
			value = {"/consultaEstatEnviament/**"},
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

		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				return RespostaConsultaEstatEnviament.builder()
						.error(true)
						.errorDescripcio("No s'ha informat cap referència de l'enviament")
						.errorData(new Date())
						.build();
			}
			return notificacioServiceWsV2.consultaEstatEnviament(referencia);
		} catch (Exception e) {
			return RespostaConsultaEstatEnviament.builder()
					.error(true)
					.errorDescripcio(getErrorDescripcio(e))
					.errorData(new Date())
					.build();
		}
	}

	@RequestMapping(
			value = {"/consultaDadesRegistre"},
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
		try {
			return notificacioServiceWsV2.consultaDadesRegistre(dadesConsulta);
		} catch (Exception e) {
			return RespostaConsultaDadesRegistre.builder()
					.error(true)
					.errorDescripcio(getErrorDescripcio(e))
					.build();
		}
	}

	@RequestMapping(
			value = {"/consultaJustificantNotificacio/**"},
			method = RequestMethod.GET,
			produces="application/json")
	@ApiOperation(
			value = "Consulta el justificant de l'enviament d'una notificació",
			notes = "Retorna el document PDF amb el justificant de l'enviament de la notificació",
			response = RespostaConsultaJustificantEnviament.class)
	@ApiParam(
			name = "identificador",
			value = "Identificador de la notificació a consultar",
			required = true)
	@ResponseBody
	public RespostaConsultaJustificantEnviament consultaJustificantV1(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@RequestMapping(
			value = "/permisConsulta",
			method = RequestMethod.POST,
			produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(
			value = "Donar permis de consulta a un usuari sobre un procediment",
			notes = "Aquest mètode permet donar el permís de consulta a un usuari específic")
	@ResponseBody
	public String donarPermisConsultaV1(
			@ApiParam(
					name = "permisConsulta",
					value = "Objecte amb les dades necessàries per donar el permís",
					required = true)
			@RequestBody PermisConsulta permisConsulta) {
		return donarPermisConsulta(permisConsulta);
	}
}
