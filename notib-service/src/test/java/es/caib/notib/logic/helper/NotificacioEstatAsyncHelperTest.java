package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaris per NotificacioEstatAsyncHelper.
 */
class NotificacioEstatAsyncHelperTest {

	private LegacyHelper legacyHelper;
	private SseEventService sseEventService;
	private NotificacioEstatAsyncHelper helper;

	@BeforeEach
	void setUp() {
		legacyHelper = mock(LegacyHelper.class);
		sseEventService = mock(SseEventService.class);
		helper = new NotificacioEstatAsyncHelper(legacyHelper, sseEventService);
	}

	@AfterEach
	void tearDown() {
		helper.shutdown();
	}

	@Test
	void calcularEstatsAsyncShouldPublishEstatsToRequestingUser() {
		when(legacyHelper.actualitzarColumnesEstat(List.of(1L, 2L))).thenReturn(Map.of(1L, "e1", 2L, "e2"));

		helper.calcularEstatsAsync(List.of(1L, 2L), "usuari1");

		var captor = ArgumentCaptor.forClass(SseEvent.class);
		verify(sseEventService, timeout(5000)).publishEvent(eq(SseEventService.SseQueue.REMESA_ENVIAMENT_ESTAT), captor.capture());
		var event = captor.getValue();
		assertEquals(SseEvent.SseEventName.NOTIFICACIO_ESTAT_CALCULAT, event.getEventName());
		assertEquals("usuari1", event.getTargetUser());
		assertEquals(Map.of(1L, "e1", 2L, "e2"), event.getData());
	}

	@Test
	void calcularEstatsAsyncShouldCalculateOnceAndNotifyEveryWaitingUser() throws Exception {
		var bloquejat = new CountDownLatch(1);
		when(legacyHelper.actualitzarColumnesEstat(List.of(1L))).thenAnswer(inv -> {
			bloquejat.await(5, TimeUnit.SECONDS);
			return Map.of(1L, "e1");
		});

		helper.calcularEstatsAsync(List.of(1L), "usuari1");
		// Mentre el primer càlcul està en curs, un altre usuari consulta la mateixa remesa
		helper.calcularEstatsAsync(List.of(1L), "usuari2");
		bloquejat.countDown();

		var captor = ArgumentCaptor.forClass(SseEvent.class);
		verify(sseEventService, timeout(5000).times(2)).publishEvent(eq(SseEventService.SseQueue.REMESA_ENVIAMENT_ESTAT), captor.capture());
		verify(legacyHelper, times(1)).actualitzarColumnesEstat(any());
		var usuaris = captor.getAllValues().stream().map(SseEvent::getTargetUser).sorted().collect(java.util.stream.Collectors.toList());
		assertEquals(List.of("usuari1", "usuari2"), usuaris);
	}

	@Test
	void calcularEstatsAsyncShouldNotPublish_whenCalculationFails() throws Exception {
		var fet = new CountDownLatch(1);
		when(legacyHelper.actualitzarColumnesEstat(any())).thenAnswer(inv -> {
			fet.countDown();
			throw new RuntimeException("error");
		});

		helper.calcularEstatsAsync(List.of(1L), "usuari1");

		assertTrue(fet.await(5, TimeUnit.SECONDS));
		Thread.sleep(200);
		verify(sseEventService, never()).publishEvent(any(), any());
	}

	@Test
	void estatsRemesaCalculatsShouldNotBeReplayedToNewListeners() {
		var sse = new es.caib.notib.logic.resourceservice.SseEventServiceImpl();
		sse.publishEvent(SseEventService.SseQueue.REMESA_ENVIAMENT_ESTAT, SseEvent.estatsRemesaCalculats(Map.of(1L, "e1"), "usuari1"));
		var rebut = new SseEvent[1];
		sse.addListener(SseEventService.SseQueue.REMESA_ENVIAMENT_ESTAT, e -> rebut[0] = e);
		assertNull(rebut[0]);
	}


	@Test
	void calcularEstatsAsyncShouldSplitIntoBlocksAndPublishAll() {
		List<Long> ids = java.util.stream.LongStream.rangeClosed(1, 45).boxed().collect(java.util.stream.Collectors.toList());
		when(legacyHelper.actualitzarColumnesEstat(any())).thenAnswer(inv -> {
			java.util.Collection<Long> bloc = inv.getArgument(0);
			Map<Long, String> estats = new java.util.HashMap<>();
			bloc.forEach(id -> estats.put(id, "e" + id));
			return estats;
		});

		helper.calcularEstatsAsync(ids, "usuari1");

		var captor = ArgumentCaptor.forClass(java.util.Collection.class);
		verify(legacyHelper, timeout(5000).times(3)).actualitzarColumnesEstat(captor.capture());
		var mides = captor.getAllValues().stream().map(java.util.Collection::size).sorted().collect(java.util.stream.Collectors.toList());
		assertEquals(List.of(5, 20, 20), mides);
		var events = ArgumentCaptor.forClass(SseEvent.class);
		verify(sseEventService, timeout(5000).times(3)).publishEvent(eq(SseEventService.SseQueue.REMESA_ENVIAMENT_ESTAT), events.capture());
		var publicats = events.getAllValues().stream().mapToInt(e -> ((Map<?, ?>) e.getData()).size()).sum();
		assertEquals(45, publicats);
	}

	@Test
	void actualitzarColumnesEstatShouldRetryOneByOne_whenBlockFails() {
		when(legacyHelper.actualitzarColumnesEstat(List.of(1L, 2L, 3L))).thenThrow(new RuntimeException("flush"));
		when(legacyHelper.actualitzarColumnesEstat(List.of(1L))).thenReturn(Map.of(1L, "e1"));
		when(legacyHelper.actualitzarColumnesEstat(List.of(2L))).thenThrow(new RuntimeException("remesa amb error"));
		when(legacyHelper.actualitzarColumnesEstat(List.of(3L))).thenReturn(Map.of(3L, "e3"));

		var estats = NotificacioEstatAsyncHelper.actualitzarColumnesEstat(legacyHelper, List.of(1L, 2L, 3L));

		assertEquals(Map.of(1L, "e1", 3L, "e3"), estats);
	}
}
