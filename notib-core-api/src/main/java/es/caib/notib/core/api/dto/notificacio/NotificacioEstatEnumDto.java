package es.caib.notib.core.api.dto.notificacio;

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
	PENDENT(0, 1<<0), // 15
	ENVIADA(1, 1<<1),	// 23
	REGISTRADA(2, 1<<2),	// 24
	FINALITZADA(3, 1<<3),	// 22
	PROCESSADA(4, 1<<4),	// 25
	EXPIRADA(10, 1<<5),	// 10
	NOTIFICADA(14, 1<<6),	// 14
	REBUTJADA(20, 1<<7),	// 20
	ENVIAT_SIR(27, 1<<8),	// 27
	ENVIADA_AMB_ERRORS(28, 1<<9),	// 28
	FINALITZADA_AMB_ERRORS(29, 1<<10),	// 29
	ENVIANT(40, 1<<11);	//

	private Integer numVal;
	private Integer mask;
	
	NotificacioEstatEnumDto(int numVal, int mask) {
        this.numVal = numVal;
		this.mask = mask;
    }
	
	public int getNumVal() {
		return numVal;
	}
	public int getMask() {
		return mask;
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
