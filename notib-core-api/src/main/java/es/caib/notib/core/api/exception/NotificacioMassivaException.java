package es.caib.notib.core.api.exception;

/**
 * Excepció que es llança quan s'ha superat el màxim nombre de línies permès
 * per al CSV de càrrega massiva.
 *   
 * @author Limit Tecnologies <limit@limit.es>
 */

public class NotificacioMassivaException extends RuntimeException {

	private Long fila;
	private String columna;

	public NotificacioMassivaException(
			Long fila,
			String columna,
			String message,
			Throwable throwable) {
		super(message, throwable);
		this.fila = fila;
		this.columna = columna;
	}

}
