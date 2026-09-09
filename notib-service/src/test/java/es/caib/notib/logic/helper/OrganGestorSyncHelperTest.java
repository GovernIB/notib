package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
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

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService);
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

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService);
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

}
