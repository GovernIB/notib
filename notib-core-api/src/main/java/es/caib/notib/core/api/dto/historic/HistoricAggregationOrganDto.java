package es.caib.notib.core.api.dto.historic;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricAggregationOrganDto extends HistoricAggregation {

	private Long numEnviaments;
	private Long numProcediments;
	private Long numGrups;
	private String codi;
	private String nom;
	
	public HistoricAggregationOrganDto(
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
			Long numProcediments,
			Long numGrups,
			String codi,
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
		this.numProcediments = numProcediments;
		this.numGrups = numGrups;
		this.codi = codi;
		this.nom = nom;
	}
}
