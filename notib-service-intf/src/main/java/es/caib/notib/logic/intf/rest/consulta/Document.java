package es.caib.notib.logic.intf.rest.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Document {

	private String nom;
	private String mediaType;
	private Long mida;
	private String url;
	
}
