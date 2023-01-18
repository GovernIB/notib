package es.caib.notib.logic.intf.dto.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent d'enviament a Notifica.
 *  - ENVIADA: Enviada a Notifica.
 *  - REGISTRADA: Enviada al registre.
 *  - FINALITZADA: Estat final de la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnumDto implements Serializable {
	PENDENT(0),
	ENVIADA(1),
	REGISTRADA(2),
	FINALITZADA(3),
	PROCESSADA(4),
	EXPIRADA(10),
	NOTIFICADA(14),
	REBUTJADA(20),
	ENVIAT_SIR(27),
	ENVIADA_AMB_ERRORS(28),
	FINALITZADA_AMB_ERRORS(29),
	ENVIANT(40);

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

	// Mètodes auxiliars
	public static boolean isSirEnviat(NotificacioEstatEnumDto estat) {
		return ENVIAT_SIR.equals(estat) || FINALITZADA.equals(estat) || PROCESSADA.equals(estat);
	}

	public static boolean isRegistrat(NotificacioEstatEnumDto estat) {
		return !PENDENT.equals(estat);
	}
}