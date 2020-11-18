package es.caib.notib.core.api.rest.consulta;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Resposta {

	int numeroElementsTotals;
	int numeroElementsRetornats;
	List<Transmissio> resultat;
	
}
