/**
 * 
 */
package es.caib.notib.logic.intf.exception;

/**
 * Excepció que es llança quan l'usuari no te permisos per accedir a un objecte.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NoPermisosException extends RuntimeException {

	private String message;

	public NoPermisosException(
			String message) {
		super(message);
		this.message = message;
	}

}
