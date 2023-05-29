/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.api.interna.openapi.model.AppInfoApi;
import es.caib.notib.client.domini.AppInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Controlador del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Informació Notib", description = "API de informació de Notib")
public class InternaApiRestController {

	@Autowired
	private ServletContext servletContext;

	@SuppressWarnings("unchecked")
	@GetMapping(value = {"/rest/appinfo"}, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Consulta la informació de la API", description = "Retorna la data i la versió de la API REST Interna Notib")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Informació de l'aplicació", content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema( implementation = AppInfoApi.class, description = "Informació de l'aplicació"))})})
	@SecurityRequirements()
	public AppInfo getAppInfo(HttpServletRequest request) throws IOException {

		var appInfo = new AppInfo();
		appInfo.setNom("Api REST interna de Notib");
		var manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
		var manifestAtributs = manifest.getMainAttributes();
		Map<String, Object>manifestAtributsMap = new HashMap<>();
		for (var key: new HashMap(manifestAtributs).keySet()) {
			manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
		}
		if (!manifestAtributsMap.isEmpty()) {
			var version = manifestAtributsMap.get("Implementation-Version");
			var data = manifestAtributsMap.get("Build-Timestamp");
			appInfo.setVersio(version != null ? version.toString() : null);
			appInfo.setData(data != null ? data.toString() : null);
		}
		return appInfo;
	}

}
