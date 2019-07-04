/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació sobre l'entrega a la DEH.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class EntregaDeh {

	private boolean obligat;
	private String procedimentCodi;
	private String emisorNif;

	public boolean isObligat() {
		return obligat;
	}
	public void setObligat(boolean obligat) {
		this.obligat = obligat;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getEmisorNif() {
		return emisorNif;
	}
	public void setEmisorNif(String emisorNif) {
		this.emisorNif = emisorNif;
	}

}
