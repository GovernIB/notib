package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per a SseEventServiceImpl.
 * <p>
 * Cobreix:
 *  - addListener
 *  - publishEvent
 *  - removeListener
 */
class SseEventServiceImplTest {

	private SseEventServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new SseEventServiceImpl();
	}

	@Test
	void publishEventShouldCallListenerWhenExists() {
		// given
		AtomicBoolean called = new AtomicBoolean(false);
		SseEventService.SseQueue queue = SseEventService.SseQueue.TEST;
		SseEvent event = newSseEvent();
		service.addListener(queue, e -> called.set(true));
		// when
		service.publishEvent(queue, event);
		// then
		assertTrue(called.get());
	}

	@Test
	void publishEventShouldDoNothingWhenNoListener() {
		// given
		SseEventService.SseQueue queue = SseEventService.SseQueue.TEST;
		SseEvent event = newSseEvent();
		// when / then (no exception expected)
		assertDoesNotThrow(() -> service.publishEvent(queue, event));
	}

	@Test
	void removeListenerShouldPreventFurtherNotifications() {
		// given
		AtomicBoolean called = new AtomicBoolean(false);
		SseEventService.SseQueue queue = SseEventService.SseQueue.TEST;
		SseEvent event = newSseEvent();
		service.addListener(queue, e -> called.set(true));
		service.removeListener(queue);
		// when
		service.publishEvent(queue, event);
		// then
		assertFalse(called.get());
	}

	@Test
	void addListenerShouldReplaceExistingListener() {
		// given
		AtomicBoolean firstCalled = new AtomicBoolean(false);
		AtomicBoolean secondCalled = new AtomicBoolean(false);
		SseEventService.SseQueue queue = SseEventService.SseQueue.TEST;
		SseEvent event = newSseEvent();
		service.addListener(queue, e -> firstCalled.set(true));
		service.addListener(queue, e -> secondCalled.set(true));
		// when
		service.publishEvent(queue, event);
		// then
		assertFalse(firstCalled.get());
		assertTrue(secondCalled.get());
	}

	private SseEvent newSseEvent() {
		SseEvent.SseEventName eventName = SseEvent.SseEventName.DIR3_SYNC;
		int percent = 50;
		SseEvent.SseEventStatus status = SseEvent.SseEventStatus.RUNNING;
		String message = "Processing";
		return new SseEvent(eventName, percent, status, message);
	}

}
