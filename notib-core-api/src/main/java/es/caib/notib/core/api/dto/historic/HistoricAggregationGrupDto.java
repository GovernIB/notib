package es.caib.notib.core.api.dto.historic;

import java.util.Date;

public class HistoricAggregationGrupDto extends HistoricAggregation {
	private String grup;
	
	public HistoricAggregationGrupDto(
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
			Long numComOrigenWeb) {
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
		// TODO Auto-generated constructor stub
	}
	public HistoricAggregationGrupDto(Date data) {
		super(data);
	}

}
