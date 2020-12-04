package es.caib.notib.core.api.dto.historic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class HistoricFiltreDto {

	private Date dataInici;
	private Date dataFi;

	private List<String> organGestorsCodis;
	private List<Long> procedimentsIds;
	
	private HistoricDadesMostrarEnum dadesMostrar;
		
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET 

	public boolean emptyOrgansGestors() {
		return this.organGestorsCodis == null || this.organGestorsCodis.isEmpty();
	}
	
	public boolean emptyProcediments() {
		return this.procedimentsIds == null || this.procedimentsIds.isEmpty();
	}
	
	public List<Date> getQueriedDates() {
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date dataFinal = cal.getTime();
			
			cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date data = cal.getTime();
			
			List<Date> dates = new ArrayList<Date> ();
			dates.add(data);
			while(data.compareTo(dataFinal) < 0) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				data = cal.getTime();
				dates.add(data);
			}
			return dates;
			
		} else if (tipusAgrupament == HistoricTipusEnumDto.MENSUAL) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date dataFinal = cal.getTime();
			
			cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date data = cal.getTime();
			
	
			List<Date> dates = new ArrayList<Date> ();
			dates.add(data);
			while(data.compareTo(dataFinal) < 0) {
				cal.add(Calendar.MONTH, 1);
				data = cal.getTime();
				dates.add(data);
			}
			return dates;
			
		} else {
			return null;
		}
		
	}
}
