/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;

/**
 * Enumerat que indica el tipus d'arxiu d'una certificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum CertificacioArxiuTipusEnum implements Serializable {

	PDF,
	XML;

	public NotificaCertificacioArxiuTipusEnumDto toNotificaCertificacioArxiuTipusEnumDto() {
		switch( this ) {
			case PDF: return NotificaCertificacioArxiuTipusEnumDto.PDF;
			case XML: return NotificaCertificacioArxiuTipusEnumDto.XML;
		}
		return null;
	}

	public static CertificacioArxiuTipusEnum toCertificacioArxiuTipusEnum(NotificaCertificacioArxiuTipusEnumDto dto) {
		if (dto == null) return null;
		switch( dto ) {
			case PDF: return PDF;
			case XML: return XML;
		}
		return null;
	}

}
