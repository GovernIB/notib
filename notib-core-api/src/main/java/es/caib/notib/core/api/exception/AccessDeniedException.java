/**
 * 
 */
package es.caib.notib.core.api.exception;

/**
 * Excepció que es llança quan l'usuari intenta accedir a una funcionalitat sobre la que no té permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class AccessDeniedException extends RuntimeException {

	private String message;

	public AccessDeniedException(
			String message) {
		super(message);
		this.message = message;
	}

}
