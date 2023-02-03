/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.client.domini.*;
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
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Controlador del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RestController
@RequestMapping("/api/services/notificacioV22")
@Tag(name = "Notificacio v2", description = "API de notificació v2")
public class NotificacioApiRestV2Controller extends NotificacioApiRestBaseController {

	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registra i envia la notificació a Notific@.", description = "Retorna una llista amb els codis dels enviaments creats per poder consultar el seu estat posteriorment")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Alta de notificació", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaAltaV2.class, description = "Informació de alta"))})})
	@PostMapping(value = "/alta", produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaAltaV2 alta(@Parameter(description = "Objecte amb les dades necessàries per a generar una notificació", required = true) @RequestBody NotificacioV2 notificacio) {
		try {
			return notificacioServiceWs.altaV2(notificacio);
		} catch (Exception e) {
			return RespostaAltaV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	@Operation(summary = "Consulta de la informació d'una notificació", description = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaEstatNotificacioV2.class, description = "Estat de la notificació")) }) })
	@Parameter(name = "identificador", description = "Identificador de la notificació a consultar", required = true)
	@GetMapping(value = {"/consultaEstatNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacio(HttpServletRequest request) throws UnsupportedEncodingException {

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

	@Operation(summary = "Consulta la informació de l'estat d'un enviament dins Notific@", description = "Retorna la informació sobre l'estat de l'enviament dins Notific@.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaEstatEnviamentV2.class, description = "Estat de l'enviament")) }) })
	@Parameter(name = "referencia", description = "Referència de la notificació a consultar", required = true)
	@GetMapping(value = {"/consultaEstatEnviament/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviament(
			HttpServletRequest request) throws UnsupportedEncodingException {

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

	@Operation(summary = "Genera el justificant i consulta la informació del registre d'una notificació.", description = "Retorna la informació del registre i el justificant d'una notificació dins Notib.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaDadesRegistreV2.class, description = "Estat del registre")) }) })
	@PostMapping(value = {"/consultaDadesRegistre"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaDadesRegistreV2 consultaDadesRegistre(@Parameter(description = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament",
					required = false) @RequestBody DadesConsulta dadesConsulta) {

		try {
			return notificacioServiceWs.consultaDadesRegistreV2(dadesConsulta);
		} catch (Exception e) {
			return RespostaConsultaDadesRegistreV2.builder().error(true).errorDescripcio(getErrorDescripcio(e)).build();
		}
	}

	@Operation(summary = "Consulta el justificant de l'enviament d'una notificació", description = "Retorna el document PDF amb el justificant de l'enviament de la notificació")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaJustificantEnviament.class, description = "Justificant de l'enviament")) }) })
	@Parameter(name = "identificador", description = "Identificador de la notificació a consultar", required = true)
	@GetMapping(value = {"/consultaJustificantNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public RespostaConsultaJustificantEnviament consultaJustificantV2(HttpServletRequest request) {
		return consultaJustificant(request);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Donar permis de consulta a un usuari sobre un procediment", description = "Aquest mètode permet donar el permís de consulta a un usuari específic")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Permisos assignats", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class, description = "Estat de l'assignació de permisos")) }) })
	@PostMapping(value = "/permisConsulta", produces = MediaType.APPLICATION_JSON_VALUE)
	public String donarPermisConsultaV2(@Parameter(description = "Objecte amb les dades necessàries per donar el permís", required = true)
										@RequestBody PermisConsulta permisConsulta) {

		return donarPermisConsulta(permisConsulta);
	}
}
