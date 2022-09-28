package es.caib.notib.logic.intf.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan s'ha superat el màxim nombre de línies permès
 * per al CSV de càrrega massiva.
 *   
 * @author Limit Tecnologies <limit@limit.es>
 */

public class NotificacioMassivaException extends RuntimeException {

	@Getter
	private Integer fila;
	@Getter
	private String columna;

	public NotificacioMassivaException(
			Integer fila,
			String columna,
			String message,
			Throwable throwable) {
		super(message, throwable);
		this.fila = fila;
		this.columna = columna;
	}

}
