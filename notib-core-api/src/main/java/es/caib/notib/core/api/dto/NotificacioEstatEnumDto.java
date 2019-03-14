/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent d'enviament a Notifica.
 *  - ENVIADA: Enviada a Notifica.
 *  - FINALITZADA: Estat final de la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnumDto implements Serializable {
	PENDENT(0),
	ENVIADA(1),
	FINALITZADA(2);
	
	private Integer numVal;
	
	NotificacioEstatEnumDto(int numVal) {
        this.numVal = numVal;
    }
	
	public int getNumVal() {
		return numVal;
	}
	
	public Long getLongVal() {
		return Long.parseLong(numVal.toString());
	}
}
