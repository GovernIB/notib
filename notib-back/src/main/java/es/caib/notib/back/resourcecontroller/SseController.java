package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Servei REST de gestió de Server Sent Events (SSE).
 *
 * @author Límit Tecnologies
 */
@RestController("apiSseController")
@RequestMapping(BaseConfig.API_PATH + "/sse")
@RequiredArgsConstructor
public class SseController extends BaseController {

	private final SseEventService sseEventService;

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
			sseEventService.addListener(queue.get(), event -> {
				try {
					emitter.send(SseEmitter.event().name(event.getEventName().name()).data(event));
					if (SseEvent.SseEventStatus.DONE.equals(event.getStatus()) || SseEvent.SseEventStatus.ERROR.equals(event.getStatus())) {
						emitter.complete();
						sseEventService.removeListener(queue.get());
					}
				} catch (Exception ex) {
					emitter.completeWithError(ex);
					sseEventService.removeListener(queue.get());
				}
			});
			emitter.onCompletion(() -> sseEventService.removeListener(queue.get()));
			emitter.onTimeout(() -> sseEventService.removeListener(queue.get()));
			emitter.onError(e -> sseEventService.removeListener(queue.get()));
			return ResponseEntity.ok(emitter);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Override
	protected Link getIndexLink() {
		return linkTo(methodOn(getClass()).index()).withRel("sse");
	}

}
