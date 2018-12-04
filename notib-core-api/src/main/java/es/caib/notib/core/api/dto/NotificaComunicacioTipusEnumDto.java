/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de comunicació de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaComunicacioTipusEnumDto implements Serializable {

	SINCRON("SINCRON"),
	ASINCRON("ASINCRON");

	private final String text;

	NotificaComunicacioTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaComunicacioTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaComunicacioTipusEnumDto valor : NotificaComunicacioTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + 	NotificaComunicacioTipusEnumDto.class.getName() + " per al text " + text);
	}

}
