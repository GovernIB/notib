package es.caib.notib.core.api.rest.consulta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Arxiu {

	String nom;
	String mediaType;
	String contingut;
	
}
