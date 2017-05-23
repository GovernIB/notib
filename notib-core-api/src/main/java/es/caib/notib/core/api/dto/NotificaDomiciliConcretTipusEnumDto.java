/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de domicili concret per a un destinatari
 * de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliConcretTipusEnumDto implements Serializable {

	NACIONAL("nacional"),
	EXTRANJERO("extranjero"),
	SIN_NORMALIZAR("sin_normalizar"),
	APARTADO_CORREOS("apartado_correos");

	private final String text;

	NotificaDomiciliConcretTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaDomiciliConcretTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaDomiciliConcretTipusEnumDto valor : NotificaDomiciliConcretTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaDomiciliConcretTipusEnumDto.class.getName() + " per al text " + text);
	}

}
