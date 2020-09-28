package es.caib.notib.core.api.rest.consulta;

import lombok.Data;

@Data
public class Persona {

	private PersonaTipus tipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String email;
//	private String dir3Codi;
	
}
