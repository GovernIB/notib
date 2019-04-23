package es.caib.notib.plugin.registre;

import java.util.Date;

import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;

/**
 * Resposta a una consulta de registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaConsultaRegistre extends RespostaBase {

	private String registreNumero;
	private Date registreData;
	private String oficinaCodi;
	private String oficinaDenominacio;
	private String entitatCodi;
	private String entitatDenominacio;
	private String registreNumeroFormatat;
	private String codiLlibre;
	private NotificacioRegistreEstatEnumDto estat;
	
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}
	public String getOficinaDenominacio() {
		return oficinaDenominacio;
	}
	public void setOficinaDenominacio(String oficinaDenominacio) {
		this.oficinaDenominacio = oficinaDenominacio;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatDenominacio() {
		return entitatDenominacio;
	}
	public void setEntitatDenominacio(String entitatDenominacio) {
		this.entitatDenominacio = entitatDenominacio;
	}
	public String getRegistreNumeroFormatat() {
		return registreNumeroFormatat;
	}
	public void setRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}
	public NotificacioRegistreEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioRegistreEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getCodiLlibre() {
		return codiLlibre;
	}
	public void setCodiLlibre(String codiLlibre) {
		this.codiLlibre = codiLlibre;
	}
}
