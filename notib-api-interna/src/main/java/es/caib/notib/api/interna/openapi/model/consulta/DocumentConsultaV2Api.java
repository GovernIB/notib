package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "DocumentConsultaV2")
public class DocumentConsultaV2Api {

	@Schema(name = "nom", implementation = String.class, example = "document.pdf", description = "Nom del document")
	private String nom;
	@Schema(name = "mediaType", implementation = String.class, example = "application/pdf", description = "Tipus de document")
	private String mediaType;
	@Schema(name = "mida", implementation = Long.class, example = "4532", description = "Mida del document")
	private Long mida;
	@Schema(name = "url", implementation = String.class, example = "http://localhost:8080/notibapi/interna/v2/document/00000000-0000-0000-0000-000000000000", description = "Url per a la descàrrega del document")
	private String url;
	
}
