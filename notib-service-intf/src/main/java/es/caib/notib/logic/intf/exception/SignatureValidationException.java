/**
 * 
 */
package es.caib.notib.logic.intf.exception;

import lombok.Getter;

/**
 * Excepció que es llança per errors validant un objecte o el seu estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SignatureValidationException extends RuntimeException {

	@Getter
	private String nom;

	public SignatureValidationException(
			String nom,
			String error) {
		super(error);
		this.nom = nom;
	}

}
