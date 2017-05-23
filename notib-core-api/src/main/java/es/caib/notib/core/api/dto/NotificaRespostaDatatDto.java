/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació de la resposta de Notifica a una consulta de datat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificaRespostaDatatDto extends NotificaRespostaDto {

	private String identificador;
	private String referenciaEmisor;
	private String titularNif;
	private String estatActual;
	private String estatActualDescripcio;
	private Date dataActualitzacio;
	private String numSeguiment;
	private List<NotificaRespostaDatatEventDto> events;
	

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getReferenciaEmisor() {
		return referenciaEmisor;
	}
	public void setReferenciaEmisor(String referenciaEmisor) {
		this.referenciaEmisor = referenciaEmisor;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getEstatActual() {
		return estatActual;
	}
	public void setEstatActual(String estatActual) {
		this.estatActual = estatActual;
	}
	public String getEstatActualDescripcio() {
		return estatActualDescripcio;
	}
	public void setEstatActualDescripcio(String estatActualDescripcio) {
		this.estatActualDescripcio = estatActualDescripcio;
	}
	public Date getDataActualitzacio() {
		return dataActualitzacio;
	}
	public void setDataActualitzacio(Date dataActualitzacio) {
		this.dataActualitzacio = dataActualitzacio;
	}
	public String getNumSeguiment() {
		return numSeguiment;
	}
	public void setNumSeguiment(String numSeguiment) {
		this.numSeguiment = numSeguiment;
	}
	public List<NotificaRespostaDatatEventDto> getEvents() {
		return events;
	}
	public void setEvents(List<NotificaRespostaDatatEventDto> events) {
		this.events = events;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class NotificaRespostaDatatEventDto {
		private String estat;
		private String descripcio;
		private Date data;
		public String getEstat() {
			return estat;
		}
		public void setEstat(String estat) {
			this.estat = estat;
		}
		public String getDescripcio() {
			return descripcio;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
		public Date getData() {
			return data;
		}
		public void setData(Date data) {
			this.data = data;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
