package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
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

	private final Map<String, Consumer<SseEvent>> consumers = new ConcurrentHashMap<>();
	// Darrer event publicat per cua, mentre encara no s'hagi tancat (DONE/ERROR). Un procés llarg
	// (p.ex. la sincronització DIR3, que pot trigar desenes de segons abans del primer event de
	// progrés) pot començar a publicar events abans que el frontend hagi acabat d'establir la
	// connexió SSE (GET /api/sse/{queueId} -> addListener): com que publishEvent no bufferitza res,
	// aquests primers events es perdien en silenci si encara no hi havia listener registrat. Guardant
	// el darrer i reenviant-lo tan bon punt es registra un listener nou, el client sempre arrenca amb
	// l'estat actual encara que s'hagi perdut la connexió a temps del primer (o dels primers) events.
	private final Map<String, SseEvent> lastEvents = new ConcurrentHashMap<>();

	@Override
	public void addListener(SseQueue queue, Consumer<SseEvent> listener) {
		log.debug("SSE listener add: " + queue.name());
		consumers.put(queue.name(), listener);
		SseEvent lastEvent = lastEvents.get(queue.name());
		if (lastEvent != null) {
			log.debug("SSE listener catch-up: " + queue.name() + ", " + lastEvent.getMessage() + ", " + lastEvent.getPercent());
			listener.accept(lastEvent);
		}
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
		Consumer<SseEvent> listener = consumers.get(queue.name());
		if (listener != null) {
			log.debug("SSE listener accept: " + queue.name() + ", " + event.getMessage() + ", " + event.getPercent());
			listener.accept(event);
		} else {
			log.debug("SSE listener discard: " + queue.name() + ", " + event.getMessage() + ", " + event.getPercent());
		}
	}

	@Override
	public void removeListener(SseQueue queue) {
		log.debug("SSE listener remove: " + queue.name());
		consumers.remove(queue.name());
	}

}
