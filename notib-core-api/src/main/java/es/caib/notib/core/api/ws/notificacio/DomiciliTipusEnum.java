/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;

/**
 * Enumerat que indica el tipus de domicili per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DomiciliTipusEnum implements Serializable {

	FISCAL,
	CONCRET;

	public NotificaDomiciliTipusEnumDto toNotificaDomiciliTipusEnumDto() {
		switch( this ) {
			case FISCAL: return NotificaDomiciliTipusEnumDto.FISCAL;
			case CONCRET: return  NotificaDomiciliTipusEnumDto.CONCRETO;
		}
		return null;
	}

	public static DomiciliTipusEnum toDomiciliTipusEnum(NotificaDomiciliTipusEnumDto dto) {
		if (dto == null) return null;
		switch( dto ) {
			case FISCAL: return FISCAL;
			case CONCRETO: return  CONCRET;
		}
		return null;
	}

}
