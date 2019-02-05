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
public enum IdiomaEnumDto implements Serializable {

	CATALÀ("CA"),
	CASTELLA("ES");
	
	private final String text;

	IdiomaEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static IdiomaEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (IdiomaEnumDto valor : IdiomaEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + IdiomaEnumDto.class.getName() + " per al text " + text);
	}

}
