/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;

/**
 * Enumerat que indica el tipus de domicili concret per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DomiciliConcretTipusEnum implements Serializable {

	NACIONAL,
	ESTRANGER,
	SENSE_NORMALITZAR,
	APARTAT_CORREUS;

	public NotificaDomiciliConcretTipusEnumDto toNotificaDomiciliConcretTipusEnumDto() {
		switch( this ) {
			case NACIONAL: return NotificaDomiciliConcretTipusEnumDto.NACIONAL;
			case ESTRANGER: return  NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
			case APARTAT_CORREUS: return  NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
			case SENSE_NORMALITZAR: return  NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
			
		}
		return null;
	}

	public static DomiciliConcretTipusEnum toDomiciliConcretTipusEnum(NotificaDomiciliConcretTipusEnumDto dto) {
		if (dto == null) return null;
		switch( dto ) {
			case NACIONAL: return DomiciliConcretTipusEnum.NACIONAL;
			case ESTRANGER: return DomiciliConcretTipusEnum.ESTRANGER;
			case APARTAT_CORREUS: return DomiciliConcretTipusEnum.APARTAT_CORREUS;
			case SENSE_NORMALITZAR: return DomiciliConcretTipusEnum.SENSE_NORMALITZAR;
		}
		return null;
	}

}
