package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RespostaConsultaV2 {

	int numeroElementsTotals;
	int numeroElementsRetornats;
	List<TransmissioV2> resultat;
	
}
