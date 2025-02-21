/**
 * 
 */
package es.caib.notib.logic.intf.dto.cie;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de certificació retornada per Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum CieCertificacioTipus implements Serializable {

	ACUSE("acuse"),
	SOBRE("sobre");

	private final String text;

	CieCertificacioTipus(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static CieCertificacioTipus toEnum(String text) {
		if (text == null)
			return null;
		for (CieCertificacioTipus valor : CieCertificacioTipus.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + CieCertificacioTipus.class.getName() + " per al text " + text);
	}

}
