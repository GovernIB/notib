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
	VALID(1, "V", "#c1bcbc"),
	RESERVA(2, "R", "#c1bcbc"),
	PENDENT(3, "P", "#c1bcbc"),
	OFICI_EXTERN(4, "E", "#c1bcbc"),
	OFICI_INTERN(5, "I", "#c1bcbc"),
	OFICI_ACCEPTAT(6, "A", "#999999"), 	// SIR
	DISTRIBUIT(7, "D", "#999999"),
	ANULAT(8, "X", "#955959"),
	RECTIFICAT(9, "RC", "#c1bcbc"),
	REBUTJAT(10, "RB", "#955959"),		// SIR
	REENVIAT(11, "RE", "#c1bcbc"),		// SIR
	DISTRIBUINT(12, "DT", "#c1bcbc"),
	OFICI_SIR(13, "S", "#c1bcbc");		// SIR
	
	private Integer numVal;
	private String budget;
	private String color;

	NotificacioRegistreEstatEnumDto(int numVal, String budget, String color) {

		this.numVal = numVal;
		this.budget = budget;
		this.color = color;
    }
	
	public int getNumVal() {
		return numVal;
	}

	public String getBudget() {
		return budget;
	}

	public String getColor() {
		return color;
	}

	public Long getLongVal() {
		return Long.parseLong(numVal.toString());
	}
}