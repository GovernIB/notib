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

	@Test
	void addListenerShouldReplayLastNonFinalEventToNewListener() {
		// given: un primer publishEvent es perd perquè encara no hi ha cap listener registrat
		// (equivalent a que el frontend encara no hagi acabat d'establir la connexió SSE).
		SseEventService.SseQueue queue = SseEventService.SseQueue.TEST;
		SseEvent event = newSseEvent();
		service.publishEvent(queue, event);
		AtomicBoolean called = new AtomicBoolean(false);
		SseEvent[] received = new SseEvent[1];
		// when: el listener es registra DESPRÉS d'aquell event
		service.addListener(queue, e -> {
			called.set(true);
			received[0] = e;
		});
		// then: rep igualment l'últim event conegut, sense necessitat d'un nou publishEvent
		assertTrue(called.get());
		assertEquals(event.getPercent(), received[0].getPercent());
		assertEquals(event.getMessage(), received[0].getMessage());
	}

	@Test
	void addListenerShouldNotReplayEventAfterDoneOrError() {
		// given: l'últim event publicat és terminal (DONE)
		SseEventService.SseQueue queue = SseEventService.SseQueue.TEST;
		service.publishEvent(queue, new SseEvent(SseEvent.SseEventName.DIR3_SYNC, 100, SseEvent.SseEventStatus.DONE, null));
		AtomicBoolean called = new AtomicBoolean(false);
		// when: un listener nou (d'una execució posterior no relacionada) es registra
		service.addListener(queue, e -> called.set(true));
		// then: no s'ha de reenviar l'estat d'una execució ja acabada
		assertFalse(called.get());
	}

	private SseEvent newSseEvent() {
		SseEvent.SseEventName eventName = SseEvent.SseEventName.DIR3_SYNC;
		int percent = 50;
		SseEvent.SseEventStatus status = SseEvent.SseEventStatus.RUNNING;
		String message = "Processing";
		return new SseEvent(eventName, percent, status, message);
	}

}
