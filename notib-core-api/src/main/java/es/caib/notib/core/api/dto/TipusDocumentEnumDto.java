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
public enum TipusDocumentEnumDto implements Serializable {
	CSV("0"),
	UUID("1"),
	ARXIU("2"),
	URL("3");
	
	private final String text;

	TipusDocumentEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static TipusDocumentEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (TipusDocumentEnumDto valor : TipusDocumentEnumDto.values()) {
			if (text.equals(valor.getText()) || text.equals(valor.name())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + 	TipusDocumentEnumDto.class.getName() + " per al text " + text);
	}
}
