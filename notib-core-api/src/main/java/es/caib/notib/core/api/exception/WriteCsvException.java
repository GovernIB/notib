package es.caib.notib.core.api.exception;

/**
 * Excepció que es llança quan No s'ha pogut escriure el CSV 
 * d'informe o d'errors per a l'enviament massiu.
 *   
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class WriteCsvException extends RuntimeException{

	public WriteCsvException(String message) {
		super(message);
	}

}