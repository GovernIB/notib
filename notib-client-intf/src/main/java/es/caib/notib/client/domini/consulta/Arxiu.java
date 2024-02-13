package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Arxiu {

	String nom;
	String mediaType;
	String contingut;
	
	boolean error;
	String missatgeError;
	
}
