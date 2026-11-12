package es.caib.notib.back.resourcecontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import es.caib.notib.logic.intf.resourceservice.SseEventService;

/**
 * Prova unitaria de {@link SseController#(SseEmitter, ScheduledFuture, SseEventService.SseQueue, String)}
 * invocada via reflection (el mètode es vol deixar private).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SseController.sendHeartbeat")
class SseControllerTest {

	private SseController controller;

	private SseEventService sseEventService;

	private void init() {
		sseEventService = mock(SseEventService.class);
		controller = new SseController(sseEventService);
	}

	private void invokeSendHeartbeat(SseEmitter emitter, ScheduledFuture<?> heartbeat,
			SseEventService.SseQueue queue, String listenerId) throws Exception {
		Method m = SseController.class.getDeclaredMethod("sendHeartbeat", SseEmitter.class,
				ScheduledFuture.class, SseEventService.SseQueue.class, String.class);
		m.setAccessible(true);
		m.invoke(controller, emitter, heartbeat, queue, listenerId);
	}

	@Test
	@DisplayName("quando send() llança IOException, cancela el batec, treu el listener i tampa l'emitter amb complete()")
	void sendHeartbeat_quandoSendLlançaIOException_cancelaBatecINetejaListener() throws Exception {
		init();

		SseEmitter emitter = mock(SseEmitter.class);
		ScheduledFuture<?> heartbeat = mock(ScheduledFuture.class);
		SseEventService.SseQueue queue = SseEventService.SseQueue.PROGRESS;
		String listenerId = "test-listener-id";

		doThrow(new IOException("Stream is closed")).when(emitter).send(any());

		invokeSendHeartbeat(emitter, heartbeat, queue, listenerId);

		verify(heartbeat).cancel(true);
		verify(sseEventService).removeListener(queue, listenerId);
		verify(emitter).complete();
		verify(emitter, never()).completeWithError(any());
	}

	@Test
	@DisplayName("quando send() no llança excepció, no fa res (no cancela, no treu listener, no tampa)")
	void sendHeartbeat_quandoSendOk_noFesRes() throws Exception {
		init();

		SseEmitter emitter = mock(SseEmitter.class);
		ScheduledFuture<?> heartbeat = mock(ScheduledFuture.class);
		SseEventService.SseQueue queue = SseEventService.SseQueue.PROGRESS;
		String listenerId = "test-listener-id";

		invokeSendHeartbeat(emitter, heartbeat, queue, listenerId);

		verify(heartbeat, never()).cancel(true);
		verify(sseEventService, never()).removeListener(queue, listenerId);
		verify(emitter, never()).complete();
	}

	@Test
	@DisplayName("quando send() llança IOException i complete() també llança IOException, no propaga res")
	void sendHeartbeat_quandoCompleteLlançaIOException_noPropaga() throws Exception {
		init();

		SseEmitter emitter = mock(SseEmitter.class);
		ScheduledFuture<?> heartbeat = mock(ScheduledFuture.class);
		SseEventService.SseQueue queue = SseEventService.SseQueue.PROGRESS;
		String listenerId = "test-listener-id";

		doThrow(new IOException("Stream is closed")).when(emitter).send(any());
		doThrow(new RuntimeException("complete falla")).when(emitter).complete();

		invokeSendHeartbeat(emitter, heartbeat, queue, listenerId);

		verify(heartbeat).cancel(true);
		verify(sseEventService).removeListener(queue, listenerId);
		verify(emitter).complete();
	}

	@Test
	@DisplayName("quando send() llança IOException no es duu a completeWithError")
	void sendHeartbeat_quandoIOException_noCompleteWithError() throws Exception {
		init();

		SseEmitter emitter = mock(SseEmitter.class);
		ScheduledFuture<?> heartbeat = mock(ScheduledFuture.class);
		SseEventService.SseQueue queue = SseEventService.SseQueue.PROGRESS;
		String listenerId = "test-listener-id";

		doThrow(new IOException("Stream is closed")).when(emitter).send(any());

		invokeSendHeartbeat(emitter, heartbeat, queue, listenerId);

		verify(emitter, never()).completeWithError(any(Throwable.class));
	}

	/**
	 * Instancia el controlador amb el constructor generat per {@code @RequiredArgsConstructor}.
	 * El camp heartbeatScheduler queda amb el valor per defecte, que no es utilitza en aquest test.
	 *
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
}
