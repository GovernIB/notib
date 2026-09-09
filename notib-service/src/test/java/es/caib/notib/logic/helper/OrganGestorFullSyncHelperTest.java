package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganGestorFullSyncHelperTest {

	@Test
	void sincronitzarTotShouldRunAllPhasesAndPublishDoneAtTheEnd() throws Exception {
		// given
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		var organGestorDir3Sync = new es.caib.notib.logic.intf.model.OrganGestorDir3Sync(
			null, new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviFusio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio[0],
			true, false);
		Mockito.when(organGestorSyncHelper.sincronitzar(Mockito.any(), Mockito.eq(false), Mockito.eq(SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC)))
			.thenReturn(organGestorDir3Sync);
		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of());
		var progressEventService = Mockito.mock(SseEventService.class);

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);
		var entitat = new EntitatResourceEntity();
		entitat.setId(1L);
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when
		helper.sincronitzarTot(entitat);

		// then
		Mockito.verify(organGestorSyncHelper).sincronitzar(entitat, false, SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC);
		Mockito.verify(procSerSyncHelper).actualitzaProcediments(Mockito.any(), Mockito.any());
		Mockito.verify(procSerSyncHelper).actualitzaServeis(Mockito.any(), Mockito.any());
		Mockito.verify(organGestorService).syncOficinesSIR(1L);
		var eventCaptor = ArgumentCaptor.forClass(SseEvent.class);
		Mockito.verify(progressEventService, Mockito.atLeastOnce())
			.publishEvent(Mockito.eq(SseEventService.SseQueue.PROGRESS), eventCaptor.capture());
		var events = eventCaptor.getAllValues();
		assertTrue(events.stream().allMatch(e -> e.getEventName() == SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC));
		assertEquals(SseEvent.SseEventStatus.DONE, events.get(events.size() - 1).getStatus());
		assertEquals(100, events.get(events.size() - 1).getPercent());
	}

}
