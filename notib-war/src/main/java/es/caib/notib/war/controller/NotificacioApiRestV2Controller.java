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
@Api(value = "/services/v2", description = "Notificacio API v2")
public class NotificacioApiRestV2Controller extends NotificacioApiRestBaseController {

	@RequestMapping(value = "/alta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Registra i envia la notificació a Notific@.", notes = "Retorna una llista amb els codis dels enviaments creats per poder consultar el seu estat posteriorment")
	@ResponseBody
	public RespostaAltaV2 alta(@ApiParam(name = "notificacio", value = "Objecte amb les dades necessàries per a generar una notificació", required = true)
								@RequestBody NotificacioV2 notificacio, HttpServletRequest request, HttpServletResponse response) {

		try {
			aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V2));
			RespostaAltaV2 resposta = notificacioServiceWsV2.altaV2(notificacio);
			logoutSession(request, response);
			return resposta;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaAltaV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ApiOperation(value = "Consulta de la informació d'una notificació", response = RespostaConsultaEstatNotificacioV2.class,
					notes = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@")
	@ApiParam(name = "identificador", value = "Identificador de la notificació a consultar", required = true)
	@ResponseBody
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacio(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V2));
		String identificador = extractIdentificador(request);
		try {
			if (identificador.isEmpty()) {
				String err = "No s'ha informat cap identificador de la notificació";
				return RespostaConsultaEstatNotificacioV2.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			RespostaConsultaEstatNotificacioV2 not =  notificacioServiceWsV2.consultaEstatNotificacioV2(identificador);
			logoutSession(request, response);
			return not;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatNotificacioV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaEstatEnviament/**"}, method = RequestMethod.GET, produces="application/json")
	@ApiOperation(value = "Consulta la informació de l'estat d'un enviament dins Notific@", response = RespostaConsultaEstatEnviamentV2.class,
					notes = "Retorna la informació sobre l'estat de l'enviament dins Notific@.")
	@ApiParam(name = "referencia", value = "Referència de la notificació a consultar", required = true)
	@ResponseBody
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviament(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V2));
		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				String err = "No s'ha informat cap referència de l'enviament";
				return RespostaConsultaEstatEnviamentV2.builder().error(true).errorDescripcio(err).errorData(new Date()).build();
			}
			RespostaConsultaEstatEnviamentV2 resposta = notificacioServiceWsV2.consultaEstatEnviamentV2(referencia);
			logoutSession(request, response);
			return resposta;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaEstatEnviamentV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@RequestMapping(value = {"/consultaDadesRegistre"}, method = RequestMethod.POST, produces="application/json")
	@ApiOperation(value = "Genera el justificant i consulta la informació del registre d'una notificació.", response = RespostaConsultaDadesRegistreV2.class,
					notes = "Retorna la informació del registre i el justificant d'una notificació dins Notib.")
	@ResponseBody
	public RespostaConsultaDadesRegistreV2 consultaDadesRegistre(
			@ApiParam(name = "dadesConsulta", value = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament", required = false)
			@RequestBody DadesConsulta dadesConsulta, HttpServletRequest request, HttpServletResponse response) {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V2));
		try {
			RespostaConsultaDadesRegistreV2 resposta = notificacioServiceWsV2.consultaDadesRegistreV2(dadesConsulta);
			logoutSession(request, response);
			return resposta;
		} catch (Exception e) {
			logoutSession(request, response);
			return RespostaConsultaDadesRegistreV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@RequestMapping(value = {"/consultaJustificantNotificacio/**"}, method = RequestMethod.GET, produces="application/json")
	@ApiOperation(value = "Consulta el justificant de l'enviament d'una notificació", response = RespostaConsultaJustificantEnviament.class,
					notes = "Retorna el document PDF amb el justificant de l'enviament de la notificació")
	@ApiParam(name = "identificador", value = "Identificador de la notificació a consultar", required = true)
	@ResponseBody
	public RespostaConsultaJustificantEnviament consultaJustificantV2(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@RequestMapping(value = "/permisConsulta", method = RequestMethod.POST, produces="application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Donar permis de consulta a un usuari sobre un procediment", notes = "Aquest mètode permet donar el permís de consulta a un usuari específic")
	@ResponseBody
	public String donarPermisConsultaV2(@ApiParam(name = "permisConsulta", value = "Objecte amb les dades necessàries per donar el permís", required = true)
										@RequestBody PermisConsulta permisConsulta, HttpServletRequest request, HttpServletResponse response) {

		aplicacioService.addAplicacioClient(getAplicacioClientInfo(AplicacioClientVersio.V2));
		String resposta = donarPermisConsulta(permisConsulta);
		logoutSession(request, response);
		return resposta;
	}
}
