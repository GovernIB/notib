/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de certificació retornada per Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaCertificacioTipusEnumDto implements Serializable {

	ACUSE("acuse"),
	SOBRE("sobre");

	private final String text;

	NotificaCertificacioTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaCertificacioTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaCertificacioTipusEnumDto valor : NotificaCertificacioTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaCertificacioTipusEnumDto.class.getName() + " per al text " + text);
	}

}
