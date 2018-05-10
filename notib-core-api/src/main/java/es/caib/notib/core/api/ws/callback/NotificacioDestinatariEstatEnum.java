/**
 * 
 */
package es.caib.notib.core.api.ws.callback;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;

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
	
	public NotificacioEnviamentEstatEnumDto toNotificacioDestinatariEstatEnumDto() {
		switch( this ) {
			case NOTIB_PENDENT: return NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT;
			case NOTIB_ENVIADA: return NotificacioEnviamentEstatEnumDto.NOTIB_ENVIADA;
			case ABSENT: return NotificacioEnviamentEstatEnumDto.ABSENT;
			case ADRESA_INCORRECTA: return NotificacioEnviamentEstatEnumDto.ADRESA_INCORRECTA;
			case DESCONEGUT: return NotificacioEnviamentEstatEnumDto.DESCONEGUT;
			case ENVIADA_CI: return NotificacioEnviamentEstatEnumDto.ENVIADA_CI;
			case ENVIADA_DEH: return NotificacioEnviamentEstatEnumDto.ENVIADA_DEH;
			case ENVIAMENT_PROGRAMAT: return NotificacioEnviamentEstatEnumDto.ENVIAMENT_PROGRAMAT;
			case ENTREGADA_OP: return NotificacioEnviamentEstatEnumDto.ENTREGADA_OP;
			case ERROR_ENTREGA: return NotificacioEnviamentEstatEnumDto.ERROR_ENTREGA;
			case EXPIRADA: return NotificacioEnviamentEstatEnumDto.EXPIRADA;
			case EXTRAVIADA: return NotificacioEnviamentEstatEnumDto.EXTRAVIADA;
			case MORT: return NotificacioEnviamentEstatEnumDto.MORT;
			case LLEGIDA: return NotificacioEnviamentEstatEnumDto.LLEGIDA;
			case NOTIFICADA: return NotificacioEnviamentEstatEnumDto.NOTIFICADA;
			case PENDENT_ENVIAMENT: return NotificacioEnviamentEstatEnumDto.PENDENT_ENVIAMENT;
			case PENDENT_SEU: return NotificacioEnviamentEstatEnumDto.PENDENT_SEU;
			case PENDENT_CIE: return NotificacioEnviamentEstatEnumDto.PENDENT_CIE;
			case PENDENT_DEH: return NotificacioEnviamentEstatEnumDto.PENDENT_DEH;
			case REBUTJADA: return NotificacioEnviamentEstatEnumDto.REBUTJADA;
			case SENSE_INFORMACIO: return NotificacioEnviamentEstatEnumDto.SENSE_INFORMACIO;
		}
		return null;
	}

	public static NotificacioDestinatariEstatEnum toNotificacioDestinatariEstatEnum(NotificacioEnviamentEstatEnumDto dto) {
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
