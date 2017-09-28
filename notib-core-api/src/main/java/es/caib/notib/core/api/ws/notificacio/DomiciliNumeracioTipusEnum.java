/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;

/**
 * Enumerat que indica el tipus de numeració de domicili per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DomiciliNumeracioTipusEnum implements Serializable {
	NUMERO,
	PUNT_KILOMETRIC,
	SENSE_NUMERO,
	APARTAT_CORREUS;
	public NotificaDomiciliNumeracioTipusEnumDto toNotificaDomiciliNumeracioTipusEnumDto() {
		switch( this ) {
			case NUMERO: return NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
			case PUNT_KILOMETRIC: return  NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
			case APARTAT_CORREUS: return  NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
			case SENSE_NUMERO: return  NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
		}
		return null;
	}
	public static DomiciliNumeracioTipusEnum toDomiciliNumeracioTipusEnum(NotificaDomiciliNumeracioTipusEnumDto dto) {
		if (dto == null) return null;
		switch( dto ) {
			case NUMERO: return NUMERO;
			case PUNT_KILOMETRIC: return PUNT_KILOMETRIC;
			case APARTAT_CORREUS: return APARTAT_CORREUS;
			case SENSE_NUMERO: return SENSE_NUMERO;
		}
		return null;
	}
}
