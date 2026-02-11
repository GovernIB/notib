package es.caib.notib.logic.intf.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un event de progrés.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@AllArgsConstructor
public class SseEvent {

	public static final String EVENT_NAME_DIR3_SYNC = "DIR3_SYNC";

	private String eventName;
	private int percent;
	private SseEventStatus status;
	private String message;

	public enum SseEventStatus {
		RUNNING,
		DONE,
		ERROR
	}

}
