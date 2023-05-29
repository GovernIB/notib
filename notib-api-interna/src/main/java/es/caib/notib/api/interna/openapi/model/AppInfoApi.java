package es.caib.notib.api.interna.openapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "AppInfo")
public class AppInfoApi {

	@Schema(name = "nom", implementation = String.class, example = "Notib", description = "Nom de l'aplicació")
	String nom;
	@Schema(name = "versio", implementation = String.class, example = "2.0.1", description = "Versió de l'aplicació")
	String versio;
	@Schema(name = "data", implementation = String.class, example = "2023-05-29T11:36:55Z", description = "Data de compilació de l'aplicació")
	String data;
	
}
