/**
 * 
 */
package es.caib.notib.logic.intf.exception;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;

/**
 * Excepció que es produeix al accedir a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class EnviamentSmEstatException extends RuntimeException {

	private EnviamentSmEstat estat;

	public EnviamentSmEstatException(String message, EnviamentSmEstat estat) {
		super(message);
		this.estat = estat;
	}

	public EnviamentSmEstatException(
			String message,
			EnviamentSmEstat estat,
			Throwable cause) {
		super(message, cause);
		this.estat = estat;
	}

	public EnviamentSmEstat getEstat() {
		return estat;
	}

}
