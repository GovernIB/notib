package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.PagadorCieResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test unitari per a PagadorCieResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class PagadorCieResourceServiceImplTest {

	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;
	@Mock
	private OrganGestorResourceRepository organGestorRepository;
	private PagadorCieResourceServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PagadorCieResourceServiceImpl(
			userSessionHelper,
			authenticationHelper,
			notibPermissionHelper,
			organGestorRepository
		);
	}

	// -----------------------------------
	// beforeUpdateSave
	// -----------------------------------
	@Test
	void beforeUpdateSaveShouldUpdateOrgansGestors() {
		// given
		PagadorCieResourceEntity entity = new PagadorCieResourceEntity();
		PagadorCieResource resource = new PagadorCieResource();
		resource.setOrganGestorEmissor(ResourceReference.toResourceReference(1L, "emissor"));
		resource.setOrganGestorPagador(ResourceReference.toResourceReference(2L, "pagador"));
		OrganGestorResourceEntity emissor = new OrganGestorResourceEntity();
		OrganGestorResourceEntity pagador = new OrganGestorResourceEntity();
		pagador.setCodi("CODI");
		when(organGestorRepository.findById(1L)).thenReturn(Optional.of(emissor));
		when(organGestorRepository.findById(2L)).thenReturn(Optional.of(pagador));
		// when
		service.beforeUpdateSave(entity, resource, Map.of());
		// then
		assertEquals(emissor, entity.getOrganGestor());
		assertEquals("CODI", entity.getOrganismePagadorCodi());
	}

	// -----------------------------------
	// beforeCreateSave
	// -----------------------------------
	@Test
	void beforeCreateSaveShouldUpdateOrgansGestors() {
		// given
		PagadorCieResourceEntity entity = new PagadorCieResourceEntity();
		PagadorCieResource resource = new PagadorCieResource();
		resource.setOrganGestorEmissor(ResourceReference.toResourceReference(1L, "emissor"));
		resource.setOrganGestorPagador(ResourceReference.toResourceReference(2L, "pagador"));
		OrganGestorResourceEntity emissor = new OrganGestorResourceEntity();
		OrganGestorResourceEntity pagador = new OrganGestorResourceEntity();
		pagador.setCodi("CODI");
		EntitatResource entitat = new EntitatResource();
		entitat.setId(1L);
		entitat.setNom("Prova");
		EntitatResourceEntity entitatEntity = EntitatResourceEntity.builder().resource(entitat).build();
		when(organGestorRepository.findById(1L)).thenReturn(Optional.of(emissor));
		when(organGestorRepository.findById(2L)).thenReturn(Optional.of(pagador));
		when(userSessionHelper.getCurrentEntitat()).thenReturn(entitatEntity);
		// when
		service.beforeCreateSave(entity, resource, Map.of());
		// then
		assertEquals(emissor, entity.getOrganGestor());
		assertEquals("CODI", entity.getOrganismePagadorCodi());
	}

	// -----------------------------------
	// afterConversion
	// -----------------------------------
	@Test
	void afterConversionShouldMapFields() {
		// given
		PagadorCieResourceEntity entity = new PagadorCieResourceEntity();
		PagadorCieResource resource = new PagadorCieResource();
		OrganGestorResourceEntity organ = new OrganGestorResourceEntity();
		organ.setId(1L);
		organ.setNom("Nom");
		entity.setOrganGestor(organ);
		entity.setOrganismePagadorCodi("CODI");
		OrganGestorResourceEntity pagador = new OrganGestorResourceEntity();
		pagador.setId(2L);
		pagador.setNom("Pagador");
		when(organGestorRepository.findByEntitatAndCodi(any(), eq("CODI")))
			.thenReturn(Optional.of(pagador));
		// when
		service.afterConversion(entity, resource);
		// then
		assertNotNull(resource.getOrganGestorEmissor());
		assertNotNull(resource.getOrganGestorPagador());
	}

	@Test
	void afterConversionShouldHandleMissingPagador() {
		// given
		PagadorCieResourceEntity entity = new PagadorCieResourceEntity();
		PagadorCieResource resource = new PagadorCieResource();
		OrganGestorResourceEntity organ = new OrganGestorResourceEntity();
		organ.setId(1L);
		organ.setNom("Nom");
		entity.setOrganGestor(organ);
		entity.setOrganismePagadorCodi("CODI");
		when(organGestorRepository.findByEntitatAndCodi(any(), any()))
			.thenReturn(Optional.empty());
		// when
		service.afterConversion(entity, resource);
		// then
		assertNotNull(resource.getOrganGestorEmissor());
		assertNull(resource.getOrganGestorPagador());
	}

}
