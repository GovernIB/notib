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
	 * Registra un listener per a una cua. Admet múltiples listeners simultanis per la mateixa cua
	 * (per exemple, un per cada usuari que té el llistat obert): tots reben els events publicats.
	 *
	 * @param queue
	 *            la coa d'events que es vol escoltar.
	 * @param listener
	 *            el consumidor.
	 * @return l'identificador únic d'aquest listener, necessari per eliminar-lo amb removeListener.
	 */
	String addListener(SseQueue queue, Consumer<SseEvent> listener);

	/**
	 * Publica un event. Es reenvia a tots els listeners registrats a la cua indicada.
	 *
	 * @param event
	 *            la informació de l'event.
	 */
	void publishEvent(SseQueue queue, SseEvent event);

	/**
	 * Esborra un listener concret del registre.
	 *
	 * @param queue
	 *            la coa d'events de la qual es vol esborrar el listener.
	 * @param listenerId
	 *            l'identificador retornat per addListener.
	 */
	void removeListener(SseQueue queue, String listenerId);

	enum SseQueue {
		PROGRESS,
		REMESA_ENVIAMENT_ESTAT,
		TEST
	}

}
