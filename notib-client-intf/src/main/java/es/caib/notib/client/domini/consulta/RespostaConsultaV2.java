package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespostaConsultaV2 {

	int numeroElementsTotals;
	int numeroElementsRetornats;
	List<TransmissioV2> resultat;
	
}
