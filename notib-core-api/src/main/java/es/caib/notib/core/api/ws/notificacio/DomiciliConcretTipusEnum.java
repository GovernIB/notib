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
			case ESTRANGER: return  NotificaDomiciliConcretTipusEnumDto.EXTRANJERO;
			case SENSE_NORMALITZAR: return  NotificaDomiciliConcretTipusEnumDto.SIN_NORMALIZAR;
			case APARTAT_CORREUS: return  NotificaDomiciliConcretTipusEnumDto.APARTADO_CORREOS;
		}
		return null;
	}

	public static DomiciliConcretTipusEnum toDomiciliConcretTipusEnum(NotificaDomiciliConcretTipusEnumDto dto) {
		if (dto == null) return null;
		switch( dto ) {
			case NACIONAL: return DomiciliConcretTipusEnum.NACIONAL;
			case EXTRANJERO: return DomiciliConcretTipusEnum.ESTRANGER;
			case SIN_NORMALIZAR: return DomiciliConcretTipusEnum.SENSE_NORMALITZAR;
			case APARTADO_CORREOS: return DomiciliConcretTipusEnum.APARTAT_CORREUS;
		}
		return null;
	}

}
