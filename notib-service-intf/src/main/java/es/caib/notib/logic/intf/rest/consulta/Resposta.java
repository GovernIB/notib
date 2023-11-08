package es.caib.notib.logic.intf.rest.consulta;

import java.util.Date;
import java.util.List;

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
public class Resposta {

	private int numeroElementsTotals;
	private int numeroElementsRetornats;
	private List<Transmissio> resultat;
	private boolean error;
	private String errorDescripcio;
	private Date errorData;
	
}
