/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de numeració de domicili per a un destinatari
 * de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliNumeracioTipusEnumDto implements Serializable {

	NUMERO("num"),
	PUNTO_KILOMETRICO("pkm"),
	SIN_NUMERO("s/n"),
	APARTADO_CORREOS("apc");

	private final String text;

	NotificaDomiciliNumeracioTipusEnumDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static NotificaDomiciliNumeracioTipusEnumDto toEnum(String text) {
		if (text == null)
			return null;
		for (NotificaDomiciliNumeracioTipusEnumDto valor : NotificaDomiciliNumeracioTipusEnumDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaDomiciliNumeracioTipusEnumDto.class.getName() + " per al text " + text);
	}
	
}
