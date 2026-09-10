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
	/** Identificador de l'entitat afectada (usat pels events d'avís de canvi d'estat). */
	private Long entityId;

	public SseEvent(SseEventName eventName, int percent, SseEventStatus status, String message) {
		this(eventName, percent, status, message, null);
	}

	/**
	 * Crea un event lleuger d'avís de canvi d'estat d'una entitat (remesa/enviament), perquè els
	 * clients amb aquesta entitat visible al llistat en refresquin la fila. No té percentatge ni
	 * missatge associat, únicament l'identificador de l'entitat que ha canviat.
	 */
	public static SseEvent entityChanged(SseEventName eventName, Long entityId) {
		return new SseEvent(eventName, 0, SseEventStatus.RUNNING, null, entityId);
	}

	public enum SseEventStatus {
		RUNNING,
		DONE,
		ERROR
	}

	public enum SseEventName {
		DIR3_SYNC,
		ORGANS_PROCEDIMENTS_SYNC,
		NOTIFICACIO_ESTAT_CANVIAT,
		ENVIAMENT_ESTAT_CANVIAT
	}

}
