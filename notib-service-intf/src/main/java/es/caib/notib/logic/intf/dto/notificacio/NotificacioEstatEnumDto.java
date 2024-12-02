package es.caib.notib.logic.intf.dto.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent d'enviament a registre.
 *  - ENVIADA: Enviada a Notifica o registrada SIR.
 *  - REGISTRADA: Enviada al registre.
 *  - FINALITZADA: Estat final de la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnumDto implements Serializable {
	PENDENT(0, 1<<0, "#FF0000"), 		// 15, M=1				00000000000001
	ENVIADA(1, 1<<1, "#00FF00"),		// 23, M=2				00000000000010
	REGISTRADA(2, 1<<2, "#0000FF"),	// 24, M=4				00000000000100
	FINALITZADA(3, 1<<3, "#FFFF00"),	// 22, M=8				00000000001000
	PROCESSADA(4, 1<<4, "#FF00FF"),	// 25, M=16
	EXPIRADA(10, 1<<5, "#00FFFF"),	// 10, M=32
	NOTIFICADA(14, 1<<6, "#800000"),	// 14, M=64
	REBUTJADA(20, 1<<7, "#808000"),	// 20, M=128
	ENVIAT_SIR(27, 1<<8, "#008080"),	// 27, M=256
	ENVIADA_AMB_ERRORS(28, 1<<9, "#800080"),	// 28, M=512
	FINALITZADA_AMB_ERRORS(29, 1<<10, "#FFA500"),	// 29, M=1024
	ENVIANT(40, 1<<11, "#A52A2A");	// M=2048

	private Integer numVal;
	private Integer mask;
	private String color;

	NotificacioEstatEnumDto(int numVal, int mask, String color) {
        this.numVal = numVal;
		this.mask = mask;
		this.color = color;
    }
	
	public int getNumVal() {
		return numVal;
	}
	public int getMask() {
		return mask;
	}
	public String getColor() {
		return color;
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

	public static boolean isEnviadaAmbErrors(NotificacioEstatEnumDto estat) {
		return ENVIADA_AMB_ERRORS.equals(estat);
	}

	public static boolean isRegistrada(NotificacioEstatEnumDto estat) {
		return REGISTRADA.equals(estat);
	}
}
