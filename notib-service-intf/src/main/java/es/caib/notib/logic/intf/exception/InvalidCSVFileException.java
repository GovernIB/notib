package es.caib.notib.logic.intf.exception;

/**
 * Excepció que es llança quan s'ha superat el màxim nombre de línies permès
 * per al CSV de càrrega massiva.
 *   
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class InvalidCSVFileException extends RuntimeException {

	public InvalidCSVFileException(String message) {
		super(message);
	}

}
