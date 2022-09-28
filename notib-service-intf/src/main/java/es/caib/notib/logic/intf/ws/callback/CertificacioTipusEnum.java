/**
 * 
 */
package es.caib.notib.logic.intf.ws.callback;

import java.io.Serializable;

import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;

/**
 * Enumerat que indica el tipus de certificació retornada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum CertificacioTipusEnum implements Serializable {

	JUSTIFICANT,
	SOBRE;

	public NotificaCertificacioTipusEnumDto toNotificaCertificacioTipusEnumDto() {
		switch( this ) {
			case JUSTIFICANT: return NotificaCertificacioTipusEnumDto.ACUSE;
			case SOBRE: return NotificaCertificacioTipusEnumDto.SOBRE;
		}
		return null;
	}

	public static CertificacioTipusEnum toCertificacioTipusEnum(NotificaCertificacioTipusEnumDto dto) {
		if (dto == null) return null;
		switch( dto ) {
			case ACUSE: return JUSTIFICANT;
			case SOBRE: return SOBRE;
		}
		return null;
	}

}
