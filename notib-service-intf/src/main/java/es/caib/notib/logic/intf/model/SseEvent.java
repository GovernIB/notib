package es.caib.notib.logic.intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Informació d'un event de progrés.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
public class SseEvent {

	private SseEventName eventName;
	private int percent;
	private SseEventStatus status;
	private String message;
	/** Identificador de l'entitat afectada (usat pels events d'avís de canvi d'estat). */
	private Long entityId;
	/** Dades associades a l'event (p.ex. els estats de remesa calculats, indexats per id). */
	private Object data;
	/**
	 * Si s'informa, l'event només s'envia a les connexions SSE d'aquest usuari (veure SseController).
	 * No s'envia mai al client.
	 */
	@JsonIgnore
	private String targetUser;

	public SseEvent(SseEventName eventName, int percent, SseEventStatus status, String message) {
		this(eventName, percent, status, message, null);
	}

	public SseEvent(SseEventName eventName, int percent, SseEventStatus status, String message, Long entityId) {
		this.eventName = eventName;
		this.percent = percent;
		this.status = status;
		this.message = message;
		this.entityId = entityId;
	}

	/**
	 * Crea un event lleuger d'avís de canvi d'estat d'una entitat (remesa/enviament), perquè els
	 * clients amb aquesta entitat visible al llistat en refresquin la fila. No té percentatge ni
	 * missatge associat, únicament l'identificador de l'entitat que ha canviat.
	 */
	public static SseEvent entityChanged(SseEventName eventName, Long entityId) {
		return new SseEvent(eventName, 0, SseEventStatus.RUNNING, null, entityId);
	}

	/**
	 * Crea un event amb els valors de la columna estat de diverses remeses, calculats de manera
	 * asíncrona després de la càrrega del llistat. Només s'envia a l'usuari que ha fet la consulta:
	 * conté informació de remeses que altres usuaris potser no tenen permís per veure.
	 */
	public static SseEvent estatsRemesaCalculats(Map<Long, String> estats, String usuariCodi) {
		var event = new SseEvent(SseEventName.NOTIFICACIO_ESTAT_CALCULAT, 100, SseEventStatus.RUNNING, null, null);
		event.setData(estats);
		event.setTargetUser(usuariCodi);
		return event;
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
		NOTIFICACIO_ESTAT_CALCULAT,
		ENVIAMENT_ESTAT_CANVIAT
	}

}
