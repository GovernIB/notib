package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "RespostaConsultaV2")
public class RespostaConsultaV2Api {

	@Schema(name = "emisor", implementation = Integer.class, example = "127", description = "Número total de notificacions, segons el filtre aplicat")
	int numeroElementsTotals;
	@Schema(name = "emisor", implementation = Integer.class, example = "10", description = "Número de notificacions retornades, depenent de la paginació")
	int numeroElementsRetornats;
	@Schema(name = "emisor", description = "Llistat de les notificacions retornades")
	List<TransmissioV2Api> resultat;
	
}
