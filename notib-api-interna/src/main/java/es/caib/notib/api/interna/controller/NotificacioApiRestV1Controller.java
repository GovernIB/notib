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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Hidden
@Deprecated
@RestController
@RequestMapping("/notificacio/v1")
@Tag(name = "Notificacio v1", description = "API de notificació v1")
public class NotificacioApiRestV1Controller extends NotificacioApiRestBaseController {

	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registra i envia la notificació a Notific@.", description = "Retorna una llista amb els codis dels enviaments creats per poder consultar el seu estat posteriorment")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Alta de notificació", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaAlta.class, description = "Informació de alta"))})})
	@PostMapping(value = "/alta", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaAlta alta(@Parameter(description = "Objecte amb les dades necessàries per a generar una notificació", required = true) @RequestBody Notificacio notificacio) {
		try {
			return notificacioServiceWs.alta(notificacio);
		} catch (Exception e) {
			return RespostaAlta.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@Operation(summary = "Consulta de la informació d'una notificació", description = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaEstatNotificacio.class, description = "Estat de la notificació")) }) })
	@Parameter(name = "identificador", description = "Identificador de la notificació a consultar", required = true)
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

	@Operation(summary = "Consulta la informació de l'estat d'un enviament dins Notific@", description = "Retorna la informació sobre l'estat de l'enviament dins Notific@.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaEstatEnviament.class, description = "Estat de l'enviament")) }) })
	@Parameter(name = "referencia", description = "Referència de la notificació a consultar", required = true)
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

	@Operation(summary = "Genera el justificant i consulta la informació del registre d'una notificació.", description = "Retorna la informació del registre i el justificant d'una notificació dins Notib.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaDadesRegistre.class, description = "Estat del registre")) }) })
	@PostMapping(value = {"/consultaDadesRegistre"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaDadesRegistre consultaDadesRegistre(@Parameter(description = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament")
			@RequestBody DadesConsulta dadesConsulta) {

		try {
			return notificacioServiceWs.consultaDadesRegistre(dadesConsulta);
		} catch (Exception e) {
			return RespostaConsultaDadesRegistre.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@Operation(summary = "Consulta el justificant de l'enviament d'una notificació", description = "Retorna el document PDF amb el justificant de l'enviament de la notificació")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaJustificantEnviament.class, description = "Justificant de l'enviament")) }) })
	@Parameter(name = "identificador", description = "Identificador de la notificació a consultar", required = true)
	@GetMapping(value = {"/consultaJustificantNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaJustificantEnviament consultaJustificantV1(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Donar permis de consulta a un usuari sobre un procediment", description = "Aquest mètode permet donar el permís de consulta a un usuari específic")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Permisos assignats", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class, description = "Estat de l'assignació de permisos")) }) })
	@PostMapping(value = "/permisConsulta", produces = MediaType.APPLICATION_JSON_VALUE)
	public String donarPermisConsultaV1(@Parameter(description = "Objecte amb les dades necessàries per donar el permís", required = true)
										@RequestBody PermisConsulta permisConsulta) {

		return donarPermisConsulta(permisConsulta);
	}
}
