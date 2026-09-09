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

	private SseEventName eventName;
	private int percent;
	private SseEventStatus status;
	private String message;

	public enum SseEventStatus {
		RUNNING,
		DONE,
		ERROR
	}

	public enum SseEventName {
		DIR3_SYNC,
		ORGANS_PROCEDIMENTS_SYNC
	}

}
