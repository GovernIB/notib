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
public enum InteressatTipusEnumDto implements Serializable {

	FISICA("FISICA"),
	JURIDICA("JURIDICA");

	private final String text;

	InteressatTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static InteressatTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (InteressatTipusEnumDto valor : InteressatTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + 	InteressatTipusEnumDto.class.getName() + " per al text " + text);
	}

}
