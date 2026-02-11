package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.model.SseEvent;

import java.util.function.Consumer;

/**
 * Definició del servei que gestiona l'enviament de Server Sent Events (SSE).
 *
 * @author Límit Tecnologies
 */
public interface SseEventService {

	/**
	 * Registra un listener per a un procés.
	 *
	 * @param queue
	 *            la coa d'events que es vol escoltar.
	 * @param listener
	 *            el consumidor.
	 */
	void addListener(SseQueue queue, Consumer<SseEvent> listener);

	/**
	 * Publica un event.
	 *
	 * @param event
	 *            la informació de l'event.
	 */
	void publishEvent(SseQueue queue, SseEvent event);

	/**
	 * Esborra el listener del registre.
	 *
	 * @param queue
	 *            la coa d'events de la qual es vol esborrar el listener.
	 */
	void removeListener(SseQueue queue);

	enum SseQueue {
		PROGRESS
	}

}
