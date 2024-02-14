/**
 * 
 */
package es.caib.notib.war.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.notib.client.domini.AplicacioClientVersio;
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
@Api(value = "/services/v1", description = "Notificacio API v1")
public class NotificacioApiRestV1Controller extends NotificacioApiRestBaseController {

	@RequestMapping(value = "/alta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Registra i envia la notificació a Notific@.", notes = "Retorna una llista amb els codis dels enviaments creats per poder consultar el seu estat posteriorment")
	@ResponseBody
	public RespostaAlta alta(@ApiParam(name = "notificacio", value = "Objecte amb les dades necessàries per a generar una notificació", required = true)
							 @RequestBody NotificacioV2 notificacio, HttpServletRequest request, HttpServletResponse response) {

		try {
			aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V1));
			RespostaAlta r =  notificacioServiceWsV2.alta(notificacio);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaAlta.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ApiOperation(value = "Consulta de la informació d'una notificació", response = RespostaConsultaEstatNotificacio.class, notes = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@")
	@ApiParam(name = "identificador", value = "Identificador de la notificació a consultar", required = true)
	@ResponseBody
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		try {
			aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V1));
			String identificador = extractIdentificador(request);
			if (identificador.isEmpty()) {
				String err = "No s'ha informat cap identificador de la notificació";
				return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			RespostaConsultaEstatNotificacio r = notificacioServiceWsV2.consultaEstatNotificacio(identificador);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatNotificacio.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatEnviament/**"}, method = RequestMethod.GET, produces="application/json")
	@ApiOperation(value = "Consulta la informació de l'estat d'un enviament dins Notific@", response = RespostaConsultaEstatEnviament.class, notes = "Retorna la informació sobre l'estat de l'enviament dins Notific@.")
	@ApiParam(name = "referencia", value = "Referència de la notificació a consultar", required = true)
	@ResponseBody
	public RespostaConsultaEstatEnviament consultaEstatEnviament(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V1));
		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				String err = "No s'ha informat cap referència de l'enviament";
				logoutSession(request, response);
				return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			RespostaConsultaEstatEnviament r = notificacioServiceWsV2.consultaEstatEnviament(referencia);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatEnviament.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaDadesRegistre"}, method = RequestMethod.POST, produces="application/json")
	@ApiOperation(value = "Genera el justificant i consulta la informació del registre d'una notificació.", response = RespostaConsultaDadesRegistre.class, notes = "Retorna la informació del registre i el justificant d'una notificació dins Notib.")
	@ResponseBody
	public RespostaConsultaDadesRegistre consultaDadesRegistre(@ApiParam(name = "dadesConsulta", value = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament", required = false)
															   @RequestBody DadesConsulta dadesConsulta, HttpServletRequest request, HttpServletResponse response) {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V1));
		try {
			RespostaConsultaDadesRegistre r = notificacioServiceWsV2.consultaDadesRegistre(dadesConsulta);
			logoutSession(request, response);
			return r;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaDadesRegistre.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@RequestMapping(value = {"/consultaJustificantNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ApiOperation(value = "Consulta el justificant de l'enviament d'una notificació", response = RespostaConsultaJustificantEnviament.class, notes = "Retorna el document PDF amb el justificant de l'enviament de la notificació")
	@ApiParam(name = "identificador", value = "Identificador de la notificació a consultar", required = true)
	@ResponseBody
	public RespostaConsultaJustificantEnviament consultaJustificantV1(HttpServletRequest request) {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V1));
		return consultaJustificant(request);
	}

	@RequestMapping(value = "/permisConsulta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Donar permis de consulta a un usuari sobre un procediment", notes = "Aquest mètode permet donar el permís de consulta a un usuari específic")
	@ResponseBody
	public String donarPermisConsultaV1(@ApiParam(name = "permisConsulta", value = "Objecte amb les dades necessàries per donar el permís", required = true)
										@RequestBody PermisConsulta permisConsulta, HttpServletRequest request, HttpServletResponse response) {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V1));
		String r = donarPermisConsulta(permisConsulta);
		logoutSession(request, response);
		return r;
	}
}
