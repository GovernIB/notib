package es.caib.notib.api.interna.openapi.interficies;

import es.caib.notib.api.interna.openapi.model.notificacio.DadesConsultaApi;
import es.caib.notib.api.interna.openapi.model.notificacio.NotificacioV2Api;
import es.caib.notib.api.interna.openapi.model.notificacio.PermisConsultaApi;
import es.caib.notib.api.interna.openapi.model.notificacio.RespostaAltaV2Api;
import es.caib.notib.api.interna.openapi.model.notificacio.RespostaConsultaDadesRegistreV2Api;
import es.caib.notib.api.interna.openapi.model.notificacio.RespostaConsultaEstatEnviamentV2Api;
import es.caib.notib.api.interna.openapi.model.notificacio.RespostaConsultaEstatNotificacioV2Api;
import es.caib.notib.api.interna.openapi.model.notificacio.RespostaConsultaJustificantEnviamentApi;
import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioV3;
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

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RequestMapping("/api/services/notificacioV3")
@Tag(name = "Notificacio v3", description = "API de notificació v3")
public interface NotificacioApiRestV3Intf {

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra i envia la notificació a Notific@.", description = "Retorna una llista amb els codis dels enviaments creats per poder consultar el seu estat posteriorment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alta de notificació", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaAltaV2Api.class, description = "Informació de alta"))})})
    @PostMapping(value = "/alta", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespostaAltaV2 alta(
            @Parameter(description = "Objecte amb les dades necessàries per a donar d'alta una notificació", required = true, schema = @Schema(implementation = NotificacioV2Api.class))
            @RequestBody NotificacioV3 notificacio);

    @Operation(summary = "Consulta de la informació d'una notificació", description = "Retorna la informació sobre l'estat de l'enviament dins Notib o Notific@")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaEstatNotificacioV2Api.class, description = "Estat de la notificació")) }) })
    @Parameter(name = "identificador",
            description = "Identificador de la notificació a consultar. \n" +
                    " * A la url del mètode es mostra aquest identificador com a '**' degut a que per compatibilitat amb versions antigues, es poden trobar identificadors que contenen el caràcter '/'. \n" +
                    " * Actualment els identificadors tenen el format de UUID",
            required = true,
            example = "00000000-0000-0000-0000-000000000000",
            schema = @Schema(implementation = String.class))
    @GetMapping(value = {"/consultaEstatNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacio(HttpServletRequest request) throws UnsupportedEncodingException;

    @Operation(summary = "Consulta la informació de l'estat d'un enviament dins Notific@", description = "Retorna la informació sobre l'estat de l'enviament dins Notific@.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaEstatEnviamentV2Api.class, description = "Estat de l'enviament")) }) })
    @Parameter(name = "referencia", description = "Referència de la notificació a consultar", required = true)
    @Parameter(name = "referencia",
            description = "Referència de la notificació a consultar. \n" +
                    " * A la url del mètode es mostra aquesta referència com a '**' degut a que per compatibilitat amb versions antigues, es poden trobar referències que contenen el caràcter '/'. \n" +
                    " * Actualment les referències tenen el format de UUID",
            required = true,
            example = "00000000-0000-0000-0000-000000000000",
            schema = @Schema(implementation = String.class))
    @GetMapping(value = {"/consultaEstatEnviament/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public RespostaConsultaEstatEnviamentV2 consultaEstatEnviament(HttpServletRequest request) throws UnsupportedEncodingException;

    @Operation(summary = "Genera el justificant i consulta la informació del registre d'una notificació.", description = "Retorna la informació del registre i el justificant d'una notificació dins Notib.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaDadesRegistreV2Api.class, description = "Estat del registre")) }) })
    @PostMapping(value = {"/consultaDadesRegistre"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public RespostaConsultaDadesRegistreV2 consultaDadesRegistre(
            @Parameter(description = "Objecte amb les dades necessàries per consultar les dades de registre d'una notificació o enviament", schema = @Schema(implementation = DadesConsultaApi.class), required = false)
            @RequestBody DadesConsulta dadesConsulta);

    @Operation(summary = "Consulta el justificant de l'enviament d'una notificació", description = "Retorna el document PDF amb el justificant de l'enviament de la notificació")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada correctament", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RespostaConsultaJustificantEnviamentApi.class, description = "Justificant de l'enviament")) }) })
    @Parameter(name = "identificador",
            description = "Identificador de la notificació a consultar. \n" +
                    " * A la url del mètode es mostra aquest identificador com a '**' degut a que per compatibilitat amb versions antigues, es poden trobar identificadors que contenen el caràcter '/'. \n" +
                    " * Actualment els identificadors tenen el format de UUID",
            required = true,
            example = "00000000-0000-0000-0000-000000000000",
            schema = @Schema(implementation = String.class))
    @GetMapping(value = {"/consultaJustificantNotificacio/**"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public RespostaConsultaJustificantEnviament consultaJustificantV2(HttpServletRequest request);

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Donar permis de consulta a un usuari sobre un procediment", description = "Aquest mètode permet donar el permís de consulta a un usuari específic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permisos assignats", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class, description = "Estat de l'assignació de permisos")) }) })
    @PostMapping(value = "/permisConsulta", produces = MediaType.APPLICATION_JSON_VALUE)
    public String donarPermisConsultaV2(
            @Parameter(description = "Objecte amb les dades necessàries per donar el permís", schema = @Schema(implementation = PermisConsultaApi.class), required = true)
            @RequestBody PermisConsulta permisConsulta);
}
