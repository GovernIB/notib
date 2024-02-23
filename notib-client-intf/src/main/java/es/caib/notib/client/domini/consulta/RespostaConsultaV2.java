package es.caib.notib.client.domini.consulta;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RespostaConsultaV2 {

	private int numeroElementsTotals;
	private int numeroElementsRetornats;
	private List<TransmissioV2> resultat;
	private boolean error;
	private String errorDescripcio;
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date errorData;

	
}
