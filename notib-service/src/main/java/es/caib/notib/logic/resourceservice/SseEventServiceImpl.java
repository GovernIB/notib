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

	@Override
	public void addListener(SseQueue queue, Consumer<SseEvent> listener) {
		consumers.put(queue.name(), listener);
	}

	@Override
	public void publishEvent(SseQueue queue, SseEvent event) {
		Consumer<SseEvent> listener = consumers.get(queue.name());
		if (listener != null) {
			listener.accept(event);
		}
	}

	@Override
	public void removeListener(SseQueue queue) {
		consumers.remove(queue.name());
	}

}
