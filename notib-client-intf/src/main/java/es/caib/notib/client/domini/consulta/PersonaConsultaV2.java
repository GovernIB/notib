package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonaConsultaV2 {

	private GenericInfo tipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String email;
	
}
