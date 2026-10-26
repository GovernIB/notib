package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.helper.AvisEventHelper;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.model.sse.AvisosActiusEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

	private final AvisEventHelper avisEventHelper;

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
		} else if (event.getTargetUser() == null && !SseQueue.AVISOS.equals(queue)) {
			// Els events adreçats a un usuari concret (p.ex. estats de remesa calculats per a la seva
			// consulta) no són progrés d'un procés: no s'han de reenviar a un listener futur. Tampoc
			// els d'avisos: cada subscriptor nou ja rep l'estat actual en connectar.
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

	@Override
	public void notifyAvisosActius() {

		// Si hi ha una transacció en curs, s'espera que es confirmi: abans els canvis no són visibles
		// i, si es desfà, no hi ha res a notificar.
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					publicarAvisosActius();
				}
			});
		} else {
			publicarAvisosActius();
		}
	}

	private void publicarAvisosActius() {

		try {
			log.debug("Notificant als clients que els avisos actius han canviat");
			var event = new SseEvent(SseEvent.SseEventName.AVISOS_CANVIATS, 0, SseEvent.SseEventStatus.RUNNING, null);
			event.setData(avisEventHelper.findAvisosActius());
			publishEvent(SseQueue.AVISOS, event);
		} catch (Exception ex) {
			log.error("Error notificant als clients que els avisos actius han canviat", ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AvisosActiusEvent getAvisosActius(String rol, Long entitatId) {
		return avisEventHelper.getAvisosActius(rol, entitatId);
	}

	/**
	 * Un avís s'activa i es desactiva tot sol en arribar la seva data d'inici o de final, sense que
	 * ningú no toqui la taula. Just passada la mitjanit es notifica el canvi perquè les pantalles
	 * obertes no hagin d'esperar a una recàrrega per a veure'l.
	 */
	@Scheduled(cron = "1 0 0 * * *")
	public void notifyAvisosActiusCron() {
		log.debug("Notificació dels avisos activats o desactivats per data");
		notifyAvisosActius();
	}

}
