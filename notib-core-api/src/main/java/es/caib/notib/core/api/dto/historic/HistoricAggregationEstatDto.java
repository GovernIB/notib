package es.caib.notib.core.api.dto.historic;

import java.util.Date;

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;

public class HistoricAggregationEstatDto extends HistoricAggregation {
	private NotificacioEstatEnumDto estat;
	
	public HistoricAggregationEstatDto(
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
	}
	
	public HistoricAggregationEstatDto(
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
			NotificacioEstatEnumDto estat) {
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
		this.estat = estat;
	}
	public HistoricAggregationEstatDto(Date data) {
		super(data);
		this.estat = null;
	}

}
