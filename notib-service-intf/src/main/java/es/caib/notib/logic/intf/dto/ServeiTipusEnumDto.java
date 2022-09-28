/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de comunicació de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ServeiTipusEnumDto implements Serializable {

	NORMAL("NORMAL"),
	URGENT("URGENT");

	private final String text;

	ServeiTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static ServeiTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (ServeiTipusEnumDto valor : ServeiTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + 	ServeiTipusEnumDto.class.getName() + " per al text " + text);
	}

}
