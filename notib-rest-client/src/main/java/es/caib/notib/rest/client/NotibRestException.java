/**
 * 
 */
package es.caib.notib.rest.client;

/**
 * Excepció que es produeix al fer una petició via rest.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotibRestException extends RuntimeException {

	public NotibRestException(
			String message) {
		super(message);
	}

	public NotibRestException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
