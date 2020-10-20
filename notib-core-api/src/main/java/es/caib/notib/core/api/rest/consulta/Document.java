package es.caib.notib.core.api.rest.consulta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Document {

	private String nom;
	private String mediaType;
	private Long mida;
	private String url;
	
}
