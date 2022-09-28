/**
 * 
 */
package es.caib.notib.logic.intf.ws.callback;

import java.io.Serializable;

import es.caib.notib.client.domini.EnviamentEstat;

/**
 * Enumerat que indica l'estat d'una notificació per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioDestinatariEstatEnum implements Serializable {
	NOTIB_PENDENT,
	NOTIB_ENVIADA,
	ABSENT,
	ADRESA_INCORRECTA,
	DESCONEGUT,
	ENVIADA_CI,
	ENVIADA_DEH,
	ENVIAMENT_PROGRAMAT,
	ENTREGADA_OP,
	ERROR_ENTREGA,
	EXPIRADA,
	EXTRAVIADA,
	MORT,
	LLEGIDA,
	NOTIFICADA,
	REGISTRADA,
	PENDENT_ENVIAMENT,
	PENDENT_CIE,
	PENDENT_DEH,
	REBUTJADA,
	SENSE_INFORMACIO;
	
	public EnviamentEstat toNotificacioDestinatariEstatEnumDto() {
		switch( this ) {
			case NOTIB_PENDENT: return EnviamentEstat.NOTIB_PENDENT;
			case NOTIB_ENVIADA: return EnviamentEstat.NOTIB_ENVIADA;
			case ABSENT: return EnviamentEstat.ABSENT;
			case ADRESA_INCORRECTA: return EnviamentEstat.ADRESA_INCORRECTA;
			case DESCONEGUT: return EnviamentEstat.DESCONEGUT;
			case ENVIADA_CI: return EnviamentEstat.ENVIADA_CI;
			case ENVIADA_DEH: return EnviamentEstat.ENVIADA_DEH;
			case ENVIAMENT_PROGRAMAT: return EnviamentEstat.ENVIAMENT_PROGRAMAT;
			case ENTREGADA_OP: return EnviamentEstat.ENTREGADA_OP;
			case ERROR_ENTREGA: return EnviamentEstat.ERROR_ENTREGA;
			case EXPIRADA: return EnviamentEstat.EXPIRADA;
			case EXTRAVIADA: return EnviamentEstat.EXTRAVIADA;
			case MORT: return EnviamentEstat.MORT;
			case LLEGIDA: return EnviamentEstat.LLEGIDA;
			case NOTIFICADA: return EnviamentEstat.NOTIFICADA;
			case REGISTRADA: return EnviamentEstat.REGISTRADA;
			case PENDENT_ENVIAMENT: return EnviamentEstat.PENDENT_ENVIAMENT;
			case PENDENT_CIE: return EnviamentEstat.PENDENT_CIE;
			case PENDENT_DEH: return EnviamentEstat.PENDENT_DEH;
			case REBUTJADA: return EnviamentEstat.REBUTJADA;
			case SENSE_INFORMACIO: return EnviamentEstat.SENSE_INFORMACIO;
		}
		return null;
	}

	public static NotificacioDestinatariEstatEnum toNotificacioDestinatariEstatEnum(EnviamentEstat dto) {
		if (dto == null) return null;
		switch( dto ) {
			case NOTIB_PENDENT: return NOTIB_PENDENT;
			case NOTIB_ENVIADA: return NOTIB_ENVIADA;
			case ABSENT: return ABSENT;
			case ADRESA_INCORRECTA: return ADRESA_INCORRECTA;
			case DESCONEGUT: return DESCONEGUT;
			case ENVIADA_CI: return ENVIADA_CI;
			case ENVIADA_DEH: return ENVIADA_DEH;
			case ENVIAMENT_PROGRAMAT: return ENVIAMENT_PROGRAMAT;
			case ENTREGADA_OP: return ENTREGADA_OP;
			case ERROR_ENTREGA: return ERROR_ENTREGA;
			case EXPIRADA: return EXPIRADA;
			case EXTRAVIADA: return EXTRAVIADA;
			case MORT: return MORT;
			case LLEGIDA: return LLEGIDA;
			case NOTIFICADA: return NOTIFICADA;
			case REGISTRADA: return REGISTRADA;
			case PENDENT_ENVIAMENT: return PENDENT_ENVIAMENT;
			case PENDENT_CIE: return PENDENT_CIE;
			case PENDENT_DEH: return PENDENT_DEH;
			case REBUTJADA: return REBUTJADA;
			case SENSE_INFORMACIO: return SENSE_INFORMACIO;
		default:
			break;
		}
		return null;
	}
}
