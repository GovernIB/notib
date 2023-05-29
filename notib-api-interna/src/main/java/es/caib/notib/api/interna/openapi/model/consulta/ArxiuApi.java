package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "Arxiu")
public class ArxiuApi {

	@Schema(name = "nom", implementation = String.class, example = "document.pdf", description = "Nom de l'arxiu'")
	String nom;
	@Schema(name = "mediaType", implementation = String.class, example = "codi", description = "Tipus d'arxiu")
	String mediaType;
	@Schema(name = "contingut", implementation = String.class, example = "codi", description = "Contingut de l'arxiu en Base64")
	String contingut;

	@Schema(name = "error", implementation = Boolean.class, example = "false", description = "Indica si s'ha produït un error al intentar recuperar l'arxiu")
	boolean error;
	@Schema(name = "missatgeError", implementation = String.class, example = "No s'ha trobat el document.", description = "Descripció de l'error")
	String missatgeError;
	
}
