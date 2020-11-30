package es.caib.notib.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import es.caib.notib.core.api.dto.historic.HistoricDadesMostrarEnum;
import es.caib.notib.core.api.dto.historic.HistoricFiltreDto;
import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoricFiltreCommand {

	private Date dataInici;
	private Date dataFi;

	private List<String> organGestorsCodis;
	private List<Long> procedimentsIds;
		
	@NotNull
	private HistoricDadesMostrarEnum dadesMostrar;
	
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET

	private boolean showingTables;
		
	public HistoricFiltreCommand() {
		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
		this.dataFi = dateStartToday.toDate();
		this.dataInici = dateStartToday.minusDays(30).toDate();
		this.organGestorsCodis = new ArrayList<String>();
		this.procedimentsIds = new ArrayList<Long>();
		this.dadesMostrar = HistoricDadesMostrarEnum.ORGANGESTOR;
		this.tipusAgrupament = HistoricTipusEnumDto.DIARI;
		this.showingTables = true;
	}

	public boolean showingDadesOrganGestor() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.ORGANGESTOR;
	}
	public boolean showingDadesProcediment() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.PROCEDIMENT;
	}
	public boolean showingDadesEstat() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.ESTAT;
	}
	public boolean showingDadesGrups() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.GRUPS;
	}

	public boolean showingDadesUsuari() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.USUARI;
	}
	
	public HistoricFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, HistoricFiltreDto.class);
	}
}
