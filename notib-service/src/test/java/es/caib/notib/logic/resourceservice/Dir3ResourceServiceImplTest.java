package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.LegacyHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.model.FieldOption;
import es.caib.notib.logic.intf.model.Dir3Resource;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test unitari per a Dir3ResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class Dir3ResourceServiceImplTest {

	@Mock
	private LegacyHelper legacyHelper;
	@Mock
	private ConfigHelper configHelper;
	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private OrganGestorResourceRepository organGestorResourceRepository;
	@InjectMocks
	private Dir3ResourceServiceImpl service;

	@Test
	void entityRepositoryFindOneShouldReturnMappedEntity() {
		Dir3Resource resource = new Dir3Resource();
		resource.setCodi("ABC");
		when(legacyHelper.dir3FindOne("ABC")).thenReturn(Optional.of(resource));
		Optional<NoDatabaseResourceEntity<Dir3Resource, String>> result =
			service.entityRepositoryFindOne("ABC");
		assertTrue(result.isPresent());
		assertEquals("ABC", result.get().getId());
		assertEquals(resource, result.get().getResource());
	}

	@Test
	void entityRepositoryFindOneShouldReturnEmptyIfNotFound() {
		when(legacyHelper.dir3FindOne("XYZ")).thenReturn(Optional.empty());
		Optional<NoDatabaseResourceEntity<Dir3Resource, String>> result = service.entityRepositoryFindOne("XYZ");
		assertTrue(result.isEmpty());
	}

	@Test
	void entityRepositoryFindEntitiesShouldCallLegacyAndMapResults() {
		Dir3Resource resource = new Dir3Resource();
		resource.setCodi("A1");
		Page<Dir3Resource> page = new PageImpl<>(List.of(resource));
		when(legacyHelper.dir3FindMultiple(
			any(), any(), any(), any(), any(), any(), any(), any(), any()
		)).thenReturn(page);
		when(configHelper.getConfigAsBoolean(any())).thenReturn(true);
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(organGestorResourceRepository.findCodisByEntitatAndCodiIn(any(), any()))
			.thenReturn(Collections.emptyList());
		Page<NoDatabaseResourceEntity<Dir3Resource, String>> result =
			service.entityRepositoryFindEntities(null, "", new String[]{}, PageRequest.of(0, 10));
		assertEquals(1, result.getTotalElements());
		assertEquals("A1", result.getContent().get(0).getId());
	}

	@Test
	void calcularCampsRecursShouldSetNoCifWhenCifIsNull() {
		Dir3Resource resource = new Dir3Resource();
		resource.setCodi("A1");
		resource.setCif(null);
		Page<Dir3Resource> page = new PageImpl<>(List.of(resource));
		when(configHelper.getConfigAsBoolean(any())).thenReturn(true);
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(organGestorResourceRepository.findCodisByEntitatAndCodiIn(any(), any()))
			.thenReturn(Collections.emptyList());
		when(legacyHelper.dir3FindMultiple(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
		service.entityRepositoryFindEntities(null, "", new String[]{}, PageRequest.of(0, 10));
		assertTrue(resource.isNoCif());
	}

	@Test
	void calcularCampsRecursShouldSetNoSirWhenNotSir() {
		Dir3Resource resource = new Dir3Resource();
		resource.setCodi("A1");
		resource.setCif("123");
		resource.setSir(false);
		Page<Dir3Resource> page = new PageImpl<>(List.of(resource));
		when(legacyHelper.dir3FindMultiple(any(), any(), any(), any(), any(), any(), any(), any(), any()))
			.thenReturn(page);
		when(configHelper.getConfigAsBoolean(any())).thenReturn(true);
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(organGestorResourceRepository.findCodisByEntitatAndCodiIn(any(), any()))
			.thenReturn(Collections.emptyList());
		service.entityRepositoryFindEntities(null, "", new String[]{}, PageRequest.of(0, 10));
		assertTrue(resource.isNoSir());
	}

	@Test
	void filterProcessorShouldReturnNivellAdministracioOptions() {
		Dir3ResourceServiceImpl.Dir3FilterProcessor processor =
			service.new Dir3FilterProcessor();
		List<FieldOption> options = processor.getOptions(
			Dir3Resource.Dir3ResourceFilter.Fields.nivellAdministracio,
			null
		);
		assertFalse(options.isEmpty());
		assertEquals("1", options.get(0).getValue());
	}

	@Test
	void filterProcessorShouldReturnEmptyForUnknownField() {
		Dir3ResourceServiceImpl.Dir3FilterProcessor processor =
			service.new Dir3FilterProcessor();
		List<FieldOption> options = processor.getOptions("unknown", null);
		assertTrue(options.isEmpty());
	}

}
