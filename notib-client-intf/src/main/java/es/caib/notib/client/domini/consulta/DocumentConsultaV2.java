package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DocumentConsultaV2 {

	private String nom;
	private String mediaType;
	private Long mida;
	private String url;
	
}
