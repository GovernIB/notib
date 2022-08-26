package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PersonaConsultaV2 {

	private GenericInfo tipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String email;
	
}
