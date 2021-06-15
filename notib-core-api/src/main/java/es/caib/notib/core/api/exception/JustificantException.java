/**
 * 
 */
package es.caib.notib.core.api.exception;

/**
 * Excepció que es produeix al generar un justificant d'enviament
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class JustificantException extends RuntimeException {

	private String message;

	public JustificantException(
			String message,
			Throwable cause) {
		super(message);
		this.message = message;
	}

	public JustificantException(String message) {
	}

	public String getSistemaExternCodi() {
		return message;
	}

}
