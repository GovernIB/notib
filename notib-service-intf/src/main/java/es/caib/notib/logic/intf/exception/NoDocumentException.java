package es.caib.notib.logic.intf.exception;

/**
 * Excepció que es llança quan No s'ha pogut obtenir el document de l'arxiu
 *   
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NoDocumentException extends RuntimeException{

	public NoDocumentException(String message) {
		super(message);
	}

}