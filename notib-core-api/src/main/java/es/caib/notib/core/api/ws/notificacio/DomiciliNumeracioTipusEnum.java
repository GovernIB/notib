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
			case PUNT_KILOMETRIC: return  NotificaDomiciliNumeracioTipusEnumDto.PUNTO_KILOMETRICO;
			case SENSE_NUMERO: return  NotificaDomiciliNumeracioTipusEnumDto.SIN_NUMERO;
			case APARTAT_CORREUS: return  NotificaDomiciliNumeracioTipusEnumDto.APARTADO_CORREOS;
		}
		
		return null;
	}
	
	public static DomiciliNumeracioTipusEnum toDomiciliNumeracioTipusEnum(NotificaDomiciliNumeracioTipusEnumDto dto) {
		
		switch( dto ) {
			case NUMERO: return NUMERO;
			case PUNTO_KILOMETRICO: return PUNT_KILOMETRIC;
			case SIN_NUMERO: return SENSE_NUMERO;
			case APARTADO_CORREOS: return APARTAT_CORREUS;
		}
		
		return null;
	}

}
