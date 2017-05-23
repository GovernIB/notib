/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de domicili per a un destinatari de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliTipusEnumDto implements Serializable {

	FISCAL("fiscal"),
	CONCRETO("concreto");

	private final String text;

	NotificaDomiciliTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaDomiciliTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaDomiciliTipusEnumDto valor : NotificaDomiciliTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaDomiciliTipusEnumDto.class.getName() + " per al text " + text);
	}

}
