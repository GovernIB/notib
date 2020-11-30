package es.caib.notib.core.api.dto.historic;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricAggregationProcedimentDto extends HistoricAggregation {

	private Long numEnviaments;
	private Long numGrups;
	
	private String codiSia;
	private String nom;
	
	public HistoricAggregationProcedimentDto(
			Date data,
			Long numNotTotal,
			Long numComTotal,
			Long numNotCorrectes,
			Long numComCorrectes,
			Long numNotAmbError,
			Long numComAmbError,
			Long numNotProcedimentComu,
			Long numComProcedimentComu,
			Long numNotAmbGrup,
			Long numComAmbGrup,
			Long numNotOrigenApi,
			Long numComOrigenApi,
			Long numNotOrigenWeb,
			Long numComOrigenWeb,
			Long numEnviaments,
			Long numGrups,
			String codiSia,
			String nom) {
		super(
				data,
				numNotTotal,
				numComTotal,
				numNotCorrectes,
				numComCorrectes,
				numNotAmbError,
				numComAmbError,
				numNotProcedimentComu,
				numComProcedimentComu,
				numNotAmbGrup,
				numComAmbGrup,
				numNotOrigenApi,
				numComOrigenApi,
				numNotOrigenWeb,
				numComOrigenWeb);
		this.numEnviaments = numEnviaments;
		this.numGrups = numGrups;
		this.codiSia = codiSia;
		this.nom = nom;
	}
}
