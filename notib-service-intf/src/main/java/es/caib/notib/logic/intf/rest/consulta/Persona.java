package es.caib.notib.logic.intf.rest.consulta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Persona {

	private PersonaTipus tipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String email;
	
}
