package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganGestorSyncHelperTest {

	@Test
	void divisionsShouldIncludeTheSuccessorNodes() {
		// given: DIR3 reports codi A01 (currently vigent in DB) as extinct in its latest
		// version, with two historicosUO successors A02 and A03 both vigent — a division.
		var a01Extint = new NodeDir3();
		a01Extint.setCodi("A01");
		a01Extint.setDenominacio("Unitat A01");
		a01Extint.setEstat("E");
		a01Extint.setHistoricosUO(List.of("A02", "A03"));

		var a02 = new NodeDir3();
		a02.setCodi("A02");
		a02.setDenominacio("Unitat A02");
		a02.setEstat("V");

		var a03 = new NodeDir3();
		a03.setCodi("A03");
		a03.setDenominacio("Unitat A03");
		a03.setEstat("V");

		var pluginHelper = Mockito.mock(PluginHelper.class);
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var repository = Mockito.mock(OrganGestorResourceRepository.class);
		var progressEventService = Mockito.mock(SseEventService.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of(a01Extint, a02, a03));
		var existingA01 = new OrganGestorResourceEntity();
		existingA01.setCodi("A01");
		Mockito.when(repository.findByEntitat(Mockito.any())).thenReturn(List.of(existingA01));
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of());

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when
		OrganGestorDir3Sync result = helper.sincronitzar(entitat, true);

		// then
		assertEquals(1, result.getDivisions().length);
		assertEquals(2, result.getDivisions()[0].getNous().length);
	}

	@Test
	void divisionSuccessorsShouldNotAlsoAppearAsCreacions() {
		// given: same division scenario as above (A01 extint -> successors A02 and A03, both
		// new/not yet in the DB). A02 and A03 are the VALUES of divisionsMap (A01 is the key), so
		// isCodiInAnyMap's keySet-only check does not exclude them from getCreacions, and they end
		// up double-listed under both "Divisions" and "Nous" (creacions).
		var a01Extint = new NodeDir3();
		a01Extint.setCodi("A01");
		a01Extint.setDenominacio("Unitat A01");
		a01Extint.setEstat("E");
		a01Extint.setHistoricosUO(List.of("A02", "A03"));

		var a02 = new NodeDir3();
		a02.setCodi("A02");
		a02.setDenominacio("Unitat A02");
		a02.setEstat("V");

		var a03 = new NodeDir3();
		a03.setCodi("A03");
		a03.setDenominacio("Unitat A03");
		a03.setEstat("V");

		var pluginHelper = Mockito.mock(PluginHelper.class);
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var repository = Mockito.mock(OrganGestorResourceRepository.class);
		var progressEventService = Mockito.mock(SseEventService.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of(a01Extint, a02, a03));
		var existingA01 = new OrganGestorResourceEntity();
		existingA01.setCodi("A01");
		Mockito.when(repository.findByEntitat(Mockito.any())).thenReturn(List.of(existingA01));
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of());

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when
		OrganGestorDir3Sync result = helper.sincronitzar(entitat, true);

		// then: A02 and A03 must be listed as division successors...
		assertEquals(1, result.getDivisions().length);
		assertEquals(2, result.getDivisions()[0].getNous().length);
		// ...but NOT also as creacions.
		boolean anyDivisionSuccessorAlsoInCreacions = Arrays.stream(result.getCreacions())
			.anyMatch(c -> "A02".equals(c.getNou().getCodi()) || "A03".equals(c.getNou().getCodi()));
		assertFalse(anyDivisionSuccessorAlsoInCreacions, "Division successors must not also be listed as creacions");
	}

	@Test
	void sincronitzarShouldPublishUnderTheGivenEventName() {
		// given
		var pluginHelper = Mockito.mock(PluginHelper.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of());
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var repository = Mockito.mock(OrganGestorResourceRepository.class);
		Mockito.when(repository.findByEntitat(Mockito.any())).thenReturn(List.of());
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		var progressEventService = Mockito.mock(es.caib.notib.logic.intf.resourceservice.SseEventService.class);
		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");
		var eventCaptor = org.mockito.ArgumentCaptor.forClass(es.caib.notib.logic.intf.model.SseEvent.class);

		// when
		helper.sincronitzar(entitat, true, es.caib.notib.logic.intf.model.SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC);

		// then
		Mockito.verify(progressEventService, Mockito.atLeastOnce())
			.publishEvent(Mockito.eq(es.caib.notib.logic.intf.resourceservice.SseEventService.SseQueue.PROGRESS), eventCaptor.capture());
		assertTrue(eventCaptor.getAllValues().stream()
			.allMatch(e -> e.getEventName() == es.caib.notib.logic.intf.model.SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC));
	}

	@Test
	void sincronitzarShouldPersistNousAnticsOnSubstitucio() {
		// given: DIR3 reports A01 (currently vigent in DB) evolving into A02 (still vigent),
		// with A01's own latest record marked extinct.
		var a01 = new NodeDir3();
		a01.setCodi("A01");
		a01.setDenominacio("Unitat A01");
		a01.setEstat("E");
		a01.setHistoricosUO(List.of("A02"));
		var a02 = new NodeDir3();
		a02.setCodi("A02");
		a02.setDenominacio("Unitat A02");
		a02.setEstat("V");
		a02.setSuperior("");

		var pluginHelper = Mockito.mock(PluginHelper.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of(a01, a02));
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var resourceRepository = Mockito.mock(OrganGestorResourceRepository.class);
		var existingA01 = new OrganGestorResourceEntity();
		existingA01.setCodi("A01");
		var existingA02 = new OrganGestorResourceEntity();
		existingA02.setCodi("A02");
		existingA02.setEstat(es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.V);
		Mockito.when(resourceRepository.findByEntitat(Mockito.any())).thenReturn(List.of(existingA01, existingA02));
		var progressEventService = Mockito.mock(es.caib.notib.logic.intf.resourceservice.SseEventService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		// legacyA01 and legacyA02 belong to the SAME entitat as the one being synced, so
		// persistirTransicions' entitat-scoping filter must accept them.
		var legacyEntitat = Mockito.mock(es.caib.notib.persist.entity.EntitatEntity.class);
		Mockito.when(legacyEntitat.getCodi()).thenReturn("ENT1");
		var legacyA01 = OrganGestorEntity.builder().codi("A01").entitat(legacyEntitat).build();
		var legacyA02 = OrganGestorEntity.builder().codi("A02").entitat(legacyEntitat).build();
		// persistirTransicions collects all involved codis (survivor + extinct) into a single
		// list and issues ONE findByCodiIn call with it, so both legacy entities must come back
		// from that single combined-list call.
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.argThat(l -> l != null && l.contains("A01") && l.contains("A02"))))
			.thenReturn(List.of(legacyA01, legacyA02));

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, resourceRepository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when: actualitzarOrganGestor logs via the NotibLogger singleton (populated in
		// production by Spring's @PostConstruct), which is absent in this plain unit test, so
		// its static accessor is mocked for the duration of the call.
		try (var mockedNotibLogger = Mockito.mockStatic(es.caib.notib.logic.utils.NotibLogger.class)) {
			var mockNotibLogger = Mockito.mock(es.caib.notib.logic.utils.NotibLogger.class);
			mockedNotibLogger.when(es.caib.notib.logic.utils.NotibLogger::getInstance).thenReturn(mockNotibLogger);
			helper.sincronitzar(entitat, false);
		}

		// then: A01 (the extinct one, DTO-field-`nou` for substitucions per the inverted
		// naming) must record A02 (the survivor, DTO-field-`vell`) as its successor.
		assertTrue(legacyA01.getNous().contains(legacyA02));
		// and: the inverse side must also be maintained in-memory during the same session, so a
		// downstream read of the survivor's `antics` (e.g. PermisosHelper.actualitzarPermisosOrgansObsolets)
		// sees the newly-added link.
		assertTrue(legacyA02.getAntics().contains(legacyA01));
	}

	@Test
	void persistirTransicionsShouldIgnoreOrgansFromADifferentEntitat() {
		// given: DIR3 reports A01 (currently vigent in DB) evolving into A02 (still vigent), same
		// substitucio scenario as above, BUT findByCodiIn returns a legacy A02 entity that belongs
		// to a DIFFERENT entitat than the one being synced (a codi collision across entitats).
		var a01 = new NodeDir3();
		a01.setCodi("A01");
		a01.setDenominacio("Unitat A01");
		a01.setEstat("E");
		a01.setHistoricosUO(List.of("A02"));
		var a02 = new NodeDir3();
		a02.setCodi("A02");
		a02.setDenominacio("Unitat A02");
		a02.setEstat("V");
		a02.setSuperior("");

		var pluginHelper = Mockito.mock(PluginHelper.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of(a01, a02));
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var resourceRepository = Mockito.mock(OrganGestorResourceRepository.class);
		var existingA01 = new OrganGestorResourceEntity();
		existingA01.setCodi("A01");
		var existingA02 = new OrganGestorResourceEntity();
		existingA02.setCodi("A02");
		existingA02.setEstat(es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.V);
		Mockito.when(resourceRepository.findByEntitat(Mockito.any())).thenReturn(List.of(existingA01, existingA02));
		var progressEventService = Mockito.mock(es.caib.notib.logic.intf.resourceservice.SseEventService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);

		var entitatSincronitzada = Mockito.mock(es.caib.notib.persist.entity.EntitatEntity.class);
		Mockito.when(entitatSincronitzada.getCodi()).thenReturn("ENT1");
		var entitatAliena = Mockito.mock(es.caib.notib.persist.entity.EntitatEntity.class);
		Mockito.when(entitatAliena.getCodi()).thenReturn("ENT-ALIENA");
		var legacyA01 = OrganGestorEntity.builder().codi("A01").entitat(entitatSincronitzada).build();
		// legacyA02 belongs to a DIFFERENT entitat than the one being synced.
		var legacyA02 = OrganGestorEntity.builder().codi("A02").entitat(entitatAliena).build();
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.argThat(l -> l != null && l.contains("A01") && l.contains("A02"))))
			.thenReturn(List.of(legacyA01, legacyA02));

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, resourceRepository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		try (var mockedNotibLogger = Mockito.mockStatic(es.caib.notib.logic.utils.NotibLogger.class)) {
			var mockNotibLogger = Mockito.mock(es.caib.notib.logic.utils.NotibLogger.class);
			mockedNotibLogger.when(es.caib.notib.logic.utils.NotibLogger::getInstance).thenReturn(mockNotibLogger);
			helper.sincronitzar(entitat, false);
		}

		// then: legacyA02 belongs to a different entitat than the one being synced, so it must be
		// excluded from consideration and the transition must not be applied at all (legacyA01's
		// `nous` is never touched, staying at its unset builder default of null).
		assertTrue(legacyA01.getNous() == null || legacyA01.getNous().isEmpty());
		Mockito.verify(organGestorRepository, Mockito.never()).saveAll(Mockito.any());
	}

}
