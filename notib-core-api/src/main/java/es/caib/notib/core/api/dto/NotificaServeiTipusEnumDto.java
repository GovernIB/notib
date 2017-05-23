/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de servei per a un destinatari de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaServeiTipusEnumDto implements Serializable {

	NORMAL("normal"),
	URGENTE("urgente");

	private final String text;

	NotificaServeiTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaServeiTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaServeiTipusEnumDto valor : NotificaServeiTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaServeiTipusEnumDto.class.getName() + " per al text " + text);
	}

}
