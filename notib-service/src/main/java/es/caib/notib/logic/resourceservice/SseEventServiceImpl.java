package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Implementació del servei que gestiona l'enviament de Server Sent Events (SSE).
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SseEventServiceImpl implements SseEventService {

	// Múltiples listeners per cua: cada connexió SSE (un per usuari/pestanya) en registra un, tots
	// reben tots els events publicats a la cua. Abans només s'admetia un listener per cua (pensat
	// per un únic procés de progrés com la sincronització DIR3), i un segon subscriptor desallotjava
	// silenciosament el primer; ara cal difondre canvis d'estat a tots els usuaris que tenen un
	// llistat obert.
	private final Map<String, Map<String, Consumer<SseEvent>>> consumers = new ConcurrentHashMap<>();
	// Darrer event publicat per cua, mentre encara no s'hagi tancat (DONE/ERROR). Un procés llarg
	// (p.ex. la sincronització DIR3, que pot trigar desenes de segons abans del primer event de
	// progrés) pot començar a publicar events abans que el frontend hagi acabat d'establir la
	// connexió SSE (GET /api/sse/{queueId} -> addListener): com que publishEvent no bufferitza res,
	// aquests primers events es perdien en silenci si encara no hi havia listener registrat. Guardant
	// el darrer i reenviant-lo tan bon punt es registra un listener nou, el client sempre arrenca amb
	// l'estat actual encara que s'hagi perdut la connexió a temps del primer (o dels primers) events.
	private final Map<String, SseEvent> lastEvents = new ConcurrentHashMap<>();

	@Override
	public String addListener(SseQueue queue, Consumer<SseEvent> listener) {
		var listenerId = UUID.randomUUID().toString();
		log.debug("SSE listener add: " + queue.name() + ", " + listenerId);
		consumers.computeIfAbsent(queue.name(), q -> new ConcurrentHashMap<>()).put(listenerId, listener);
		SseEvent lastEvent = lastEvents.get(queue.name());
		if (lastEvent != null) {
			log.debug("SSE listener catch-up: " + queue.name() + ", " + lastEvent.getMessage() + ", " + lastEvent.getPercent());
			listener.accept(lastEvent);
		}
		return listenerId;
	}

	@Override
	public void publishEvent(SseQueue queue, SseEvent event) {
		if (SseEvent.SseEventStatus.DONE.equals(event.getStatus()) || SseEvent.SseEventStatus.ERROR.equals(event.getStatus())) {
			// Procés acabat: no ha de quedar cap estat resident que es reenviï a un listener futur
			// d'una execució posterior, no relacionada, d'aquesta mateixa cua.
			lastEvents.remove(queue.name());
		} else {
			lastEvents.put(queue.name(), event);
		}
		var listeners = consumers.get(queue.name());
		if (listeners == null || listeners.isEmpty()) {
			log.debug("SSE listener discard: " + queue.name() + ", " + event.getMessage() + ", " + event.getPercent());
			return;
		}
		log.debug("SSE listener accept: " + queue.name() + ", " + listeners.size() + " listener(s), " + event.getMessage() + ", " + event.getPercent());
		for (var listener : listeners.values()) {
			listener.accept(event);
		}
	}

	@Override
	public void removeListener(SseQueue queue, String listenerId) {
		log.debug("SSE listener remove: " + queue.name() + ", " + listenerId);
		var listeners = consumers.get(queue.name());
		if (listeners != null) {
			listeners.remove(listenerId);
		}
	}

}
