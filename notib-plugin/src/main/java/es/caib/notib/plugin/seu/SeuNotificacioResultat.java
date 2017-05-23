/**
 * 
 */
package es.caib.notib.plugin.seu;

import java.util.Date;

/**
 * Dades resultants de fer una notificació telemàtica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SeuNotificacioResultat {

	private String registreNumero;
	private Date registreData;

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

}
