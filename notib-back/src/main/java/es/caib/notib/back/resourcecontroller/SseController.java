package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PreDestroy;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Servei REST de gestió de Server Sent Events (SSE).
 *
 * @author Límit Tecnologies
 */
@Slf4j
@RestController("apiSseController")
@RequestMapping(BaseConfig.API_PATH + "/sse")
@RequiredArgsConstructor
public class SseController extends BaseController {

	// En producció l'aplicació queda darrere d'un proxy que talla les connexions inactives més de
	// 30 segons. Com que un event real (canvi d'estat, progrés d'una sync...) pot trigar molt més a
	// arribar, cal enviar periòdicament un "batec" perquè el proxy no consideri la connexió SSE
	// inactiva i la tanqui, encara que el client no en faci res (és un comentari SSE, no un event).
	private static final long HEARTBEAT_INTERVAL_SECONDS = 15;

	private final SseEventService sseEventService;
	private final ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(2, sseHeartbeatThreadFactory());

	private static ThreadFactory sseHeartbeatThreadFactory() {
		var counter = new AtomicInteger();
		return runnable -> {
			var thread = new Thread(runnable, "sse-heartbeat-" + counter.incrementAndGet());
			thread.setDaemon(true);
			return thread;
		};
	}

	@PreDestroy
	public void shutdown() {
		heartbeatScheduler.shutdownNow();
	}

	@Hidden
	@GetMapping
	@Operation(
		summary = "Operacions disponibles",
		description = "Index d'enllaços a les operacions disponibles en aquest servei."
	)
	public ResponseEntity<CollectionModel<?>> index() {
		List<Link> indexLinks = new ArrayList<>();
		indexLinks.add(linkTo(methodOn(getClass()).stream(null)).withRel("subscribe"));
		CollectionModel<?> resources = CollectionModel.of(
			Collections.emptySet(),
			indexLinks.toArray(Link[]::new));
		return ResponseEntity.ok(resources);
	}

	@GetMapping("/{queueId}")
	@Operation(
		summary = "Subscriure's a un flux d'events",
		description = "Es subscriu al flux d'events indicat amb queueId"
	)
	public ResponseEntity<SseEmitter> stream(@PathVariable String queueId) {
		Optional<SseEventService.SseQueue> queue =
			Arrays.stream(SseEventService.SseQueue.values())
				.filter(q -> q.name().equals(queueId))
				.findFirst();
		if (queue.isPresent()) {
			SseEmitter emitter = new SseEmitter(0L);
			// L'identificador del listener no es coneix fins que retorna addListener, però les
			// callbacks de l'emitter (incloent la del propi listener, que es pot invocar de manera
			// síncrona per reenviar l'últim event conegut) el necessiten: es guarda en un
			// AtomicReference perquè totes hi tinguin accés un cop assignat.
			var listenerId = new AtomicReference<String>();
			listenerId.set(sseEventService.addListener(queue.get(), event -> {
				try {
					emitter.send(SseEmitter.event().name(event.getEventName().name()).data(event));
					if (SseEvent.SseEventStatus.DONE.equals(event.getStatus()) || SseEvent.SseEventStatus.ERROR.equals(event.getStatus())) {
						emitter.complete();
						sseEventService.removeListener(queue.get(), listenerId.get());
					}
				} catch (Exception ex) {
					emitter.completeWithError(ex);
					sseEventService.removeListener(queue.get(), listenerId.get());
				}
			}));
			ScheduledFuture<?> heartbeat = heartbeatScheduler.scheduleAtFixedRate(
				() -> sendHeartbeat(emitter),
				HEARTBEAT_INTERVAL_SECONDS,
				HEARTBEAT_INTERVAL_SECONDS,
				TimeUnit.SECONDS);
			emitter.onCompletion(() -> {
				log.debug("Emitter onCompletion");
				heartbeat.cancel(true);
				sseEventService.removeListener(queue.get(), listenerId.get());
			});
			emitter.onTimeout(() -> {
				log.debug("Emitter onTimeout");
				heartbeat.cancel(true);
				sseEventService.removeListener(queue.get(), listenerId.get());
			});
			emitter.onError(e -> {
				log.debug("Emitter onError", e);
				heartbeat.cancel(true);
				sseEventService.removeListener(queue.get(), listenerId.get());
			});
			return ResponseEntity.ok(emitter);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Envia un comentari SSE buit (una línia ":", ignorada per l'EventSource del client) només
	 * perquè hi hagi tràfic de tant en tant a la connexió i el proxy intermedi no la doni per
	 * inactiva. Si l'enviament falla és que la connexió ja no és vàlida: no cal fer res, l'emitter
	 * mateix dispararà onError/onCompletion i el batec quedarà cancel·lat des d'allà.
	 */
	private void sendHeartbeat(SseEmitter emitter) {
		try {
			emitter.send(SseEmitter.event().comment("heartbeat"));
		} catch (Exception ex) {
			log.debug("SSE heartbeat: connexió ja tancada", ex);
		}
	}

	@Override
	protected Link getIndexLink() {
		return linkTo(methodOn(getClass()).index()).withRel("sse");
	}

}
