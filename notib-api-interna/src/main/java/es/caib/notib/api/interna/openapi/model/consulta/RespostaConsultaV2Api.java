package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Schema(name = "RespostaConsultaV2")
public class RespostaConsultaV2Api {

	@Schema(name = "numeroElementsTotals", implementation = Integer.class, example = "127", description = "Número total de notificacions, segons el filtre aplicat")
	int numeroElementsTotals;
	@Schema(name = "numeroElementsRetornats", implementation = Integer.class, example = "10", description = "Número de notificacions retornades, depenent de la paginació")
	int numeroElementsRetornats;
	@Schema(name = "resultat", description = "Llistat de les notificacions retornades")
	List<TransmissioV2Api> resultat;
	@Schema(name = "error", implementation = Boolean.class, example = "false", description = "Error en la consulta")
	private boolean error;
	@Schema(name = "errorDescripcio", implementation = String.class, example = "java.lang.NullPointerException", description = "Descripció de l'error")
	private String errorDescripcio;
	@Schema(name = "errorData", implementation = Date.class, example = "2023-05-29T07:32:03.526+0000", description = "Data de l'error")
	private Date errorData;
}
