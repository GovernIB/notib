package es.caib.notib.core.api.dto.historic;

import java.util.Date;

import lombok.Data;

@Data
public class HistoricAggregation {

	protected Date data;
	
	private Long numNotTotal;
	private Long numComTotal;
	
	private Long numNotCorrectes;
	private Long numComCorrectes;
	
	private Long numNotAmbError;
	private Long numComAmbError;
	
	private Long numNotProcedimentComu;
	private Long numComProcedimentComu;
	
	private Long numNotAmbGrup;	
	private Long numComAmbGrup;
	
	private Long numNotOrigenApi;
	private Long numComOrigenApi;
	
	private Long numNotOrigenWeb;
	private Long numComOrigenWeb;
	
//	private Long numNotdestiAdm;
//	private Long numComdestiAdm;
//	
//	private Long numNotdestiCiutada;
//	private Long numComdestiCiutada;
	
	public HistoricAggregation(
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
		super();
		this.data = data;
		this.numNotTotal = numNotTotal;
		this.numComTotal = numComTotal;
		this.numNotCorrectes = numNotCorrectes;
		this.numComCorrectes = numComCorrectes;
		this.numNotAmbError = numNotAmbError;
		this.numComAmbError = numComAmbError;
		this.numNotProcedimentComu = numNotProcedimentComu;
		this.numComProcedimentComu = numComProcedimentComu;
		this.numNotAmbGrup = numNotAmbGrup;
		this.numComAmbGrup = numComAmbGrup;
		this.numNotOrigenApi = numNotOrigenApi;
		this.numComOrigenApi = numComOrigenApi;
		this.numNotOrigenWeb = numNotOrigenWeb;
		this.numComOrigenWeb = numComOrigenWeb;
	}
	
	
	public HistoricAggregation() {
		super();
		this.data = null;
		this.numNotTotal = 0L;
		this.numComTotal = 0L;
		this.numNotCorrectes = 0L;
		this.numComCorrectes = 0L;
		this.numNotAmbError = 0L;
		this.numComAmbError = 0L;
		this.numNotProcedimentComu = 0L;
		this.numComProcedimentComu = 0L;
		this.numNotAmbGrup = 0L;
		this.numComAmbGrup = 0L;
		this.numNotOrigenApi = 0L;
		this.numComOrigenApi = 0L;
		this.numNotOrigenWeb = 0L;
		this.numComOrigenWeb = 0L;
	}


	public HistoricAggregation(Date data) {
		this();
		this.data = data;
	}
	
	
}
