/**
 * 
 */
package es.caib.notib.core.api.ws.callback;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;

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
	PENDENT_ENVIAMENT,
	PENDENT_SEU,
	PENDENT_CIE,
	PENDENT_DEH,
	REBUTJADA,
	SENSE_INFORMACIO;
	
	public NotificacioDestinatariEstatEnumDto toNotificacioDestinatariEstatEnumDto() {
		switch( this ) {
			case NOTIB_PENDENT: return NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT;
			case NOTIB_ENVIADA: return NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA;
			case ABSENT: return NotificacioDestinatariEstatEnumDto.ABSENT;
			case ADRESA_INCORRECTA: return NotificacioDestinatariEstatEnumDto.ADRESA_INCORRECTA;
			case DESCONEGUT: return NotificacioDestinatariEstatEnumDto.DESCONEGUT;
			case ENVIADA_CI: return NotificacioDestinatariEstatEnumDto.ENVIADA_CI;
			case ENVIADA_DEH: return NotificacioDestinatariEstatEnumDto.ENVIADA_DEH;
			case ENVIAMENT_PROGRAMAT: return NotificacioDestinatariEstatEnumDto.ENVIAMENT_PROGRAMAT;
			case ENTREGADA_OP: return NotificacioDestinatariEstatEnumDto.ENTREGADA_OP;
			case ERROR_ENTREGA: return NotificacioDestinatariEstatEnumDto.ERROR_ENTREGA;
			case EXPIRADA: return NotificacioDestinatariEstatEnumDto.EXPIRADA;
			case EXTRAVIADA: return NotificacioDestinatariEstatEnumDto.EXTRAVIADA;
			case MORT: return NotificacioDestinatariEstatEnumDto.MORT;
			case LLEGIDA: return NotificacioDestinatariEstatEnumDto.LLEGIDA;
			case NOTIFICADA: return NotificacioDestinatariEstatEnumDto.NOTIFICADA;
			case PENDENT_ENVIAMENT: return NotificacioDestinatariEstatEnumDto.PENDENT_ENVIAMENT;
			case PENDENT_SEU: return NotificacioDestinatariEstatEnumDto.PENDENT_SEU;
			case PENDENT_CIE: return NotificacioDestinatariEstatEnumDto.PENDENT_CIE;
			case PENDENT_DEH: return NotificacioDestinatariEstatEnumDto.PENDENT_DEH;
			case REBUTJADA: return NotificacioDestinatariEstatEnumDto.REBUTJADA;
			case SENSE_INFORMACIO: return NotificacioDestinatariEstatEnumDto.SENSE_INFORMACIO;
		}
		return null;
	}

	public static NotificacioDestinatariEstatEnum toNotificacioDestinatariEstatEnum(NotificacioDestinatariEstatEnumDto dto) {
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
			case PENDENT_ENVIAMENT: return PENDENT_ENVIAMENT;
			case PENDENT_SEU: return PENDENT_SEU;
			case PENDENT_CIE: return PENDENT_CIE;
			case PENDENT_DEH: return PENDENT_DEH;
			case REBUTJADA: return REBUTJADA;
			case SENSE_INFORMACIO: return SENSE_INFORMACIO;
		}
		return null;
	}
}
