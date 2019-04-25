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
public enum NotificacioRegistreEstatEnumDto implements Serializable {
	VALID(1),
	RESERVA(2),
	PENDENT(3),
	OFICI_EXTERN(4),
	OFICI_INTERN(5),
	OFICI_ACCEPTAT(6),
	DISTRIBUIT(7),
	ANULAT(8),
	RECTIFICAT(9),
	REBUTJAT(10),
	REENVIAT(11),
	DISTRIBUINT(12),
	OFICI_SIR(13);
	
	private Integer numVal;
	
	NotificacioRegistreEstatEnumDto(int numVal) {
        this.numVal = numVal;
    }
	
	public int getNumVal() {
		return numVal;
	}
	
	public Long getLongVal() {
		return Long.parseLong(numVal.toString());
	}
}