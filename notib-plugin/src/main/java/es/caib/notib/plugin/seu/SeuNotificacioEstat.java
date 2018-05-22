/**
 * 
 */
package es.caib.notib.plugin.seu;

import java.util.Date;

/**
 * Informació sobre el justificant d'una notificació telemàtica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeuNotificacioEstat {

	public enum ZonaperJustificantEstat {
		PENDENT,
	    LLEGIDA,
	    REBUTJADA;
	};

	private Date data;
	private ZonaperJustificantEstat estat;
	private Long fitxerCodi;
	private String fitxerClau;



	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public ZonaperJustificantEstat getEstat() {
		return estat;
	}
	public void setEstat(ZonaperJustificantEstat estat) {
		this.estat = estat;
	}
	public Long getFitxerCodi() {
		return fitxerCodi;
	}
	public void setFitxerCodi(Long fitxerCodi) {
		this.fitxerCodi = fitxerCodi;
	}
	public String getFitxerClau() {
		return fitxerClau;
	}
	public void setFitxerClau(String fitxerClau) {
		this.fitxerClau = fitxerClau;
	}

}
