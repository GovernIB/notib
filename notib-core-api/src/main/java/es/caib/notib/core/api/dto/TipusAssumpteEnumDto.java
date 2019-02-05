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
public enum TipusAssumpteEnumDto implements Serializable {

	ASSUMPTE1("ASSUMPTE1"),
	ASSUMPTE2("ASSUMPTE2");
	
	private final String text;

	TipusAssumpteEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static TipusAssumpteEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (TipusAssumpteEnumDto valor : TipusAssumpteEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + TipusAssumpteEnumDto.class.getName() + " per al text " + text);
	}

}
