/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus d'enviament de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaEnviamentTipusEnumDto implements Serializable {

	NOTIFICACIO("notificacion"),
	COMUNICACIO("comunicacion");

	private final String text;

	NotificaEnviamentTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaEnviamentTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaEnviamentTipusEnumDto valor : NotificaEnviamentTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaEnviamentTipusEnumDto.class.getName() + " per al text " + text);
	}

}
