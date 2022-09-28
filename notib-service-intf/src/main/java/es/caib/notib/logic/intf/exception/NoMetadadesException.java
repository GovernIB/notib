package es.caib.notib.logic.intf.exception;


/**
 * Excepció que es llança quan No s'han pogut obtenir les metadades del document.
 *   
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NoMetadadesException extends RuntimeException{

	public NoMetadadesException(String message) {
		super(message);
	}

}
