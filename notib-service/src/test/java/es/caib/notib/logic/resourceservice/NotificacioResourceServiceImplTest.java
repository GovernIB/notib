package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.*;
import es.caib.notib.persist.resourceentity.*;
import es.caib.notib.persist.resourcerepository.DocumentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.BasePermission;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per NotificacioResourceServiceImpl.
 * <p>
 * Cobertura:
 *  - beforeCreateSave
 *  - afterCreateSave
 *  - saveDocuments
 *  - saveEnviaments (parcial)
 *  - onChange caducitat
 */
@ExtendWith(MockitoExtension.class)
class NotificacioResourceServiceImplTest {

	@Mock private UserSessionHelper userSessionHelper;
	@Mock private AuthenticationHelper authenticationHelper;
	@Mock private NotificacioEnviamentResourceRepository enviamentRepo;
	@Mock private DocumentResourceRepository documentRepo;
	@Mock private PersonaResourceRepository personaRepo;
	@Mock private LegacyHelper legacyHelper;
	@Mock private NotibPermissionHelper notibPermissionHelper;

	@InjectMocks
	private NotificacioResourceServiceImpl service;

	private NotificacioResourceEntity entity;
	private NotificacioResource resource;

	@BeforeEach
	void setUp() {
		entity = new NotificacioResourceEntity();
		OrganGestorResourceEntity organGestor = new OrganGestorResourceEntity();
		organGestor.setId(1L);
		entity.setOrganGestor(organGestor);
		ProcedimentResourceEntity procediment = new ProcedimentResourceEntity();
		procediment.setId(2L);
		entity.setProcediment(procediment);
		ProcedimentOrganGestorResourceEntity procedimentOrganGestor = new ProcedimentOrganGestorResourceEntity();
		procedimentOrganGestor.setId(3L);
		entity.setProcedimentOrganGestor(procedimentOrganGestor);
		resource = new NotificacioResource();
	}

	// =====================================================
	// additionalSpringFilter
	// =====================================================

	@Test
	void additionalSpringFilterShouldReturnOnlyEntitatFilter_whenNoPermissions() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(BasePermission.READ, null)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(BasePermission.READ, null)).thenReturn(
			Collections.emptyList());
		String result = service.additionalSpringFilter("", null);
		assertEquals("entitat.id:1", result);
	}

	@Test
	void additionalSpringFilterShouldReturnORganGestorCondition_whenPermissionsExist() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ)).thenReturn(List.of(10L));
		String result = service.additionalSpringFilter("", null);
		assertEquals("entitat.id:1 and (organGestor.id in (10))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnProcedimentNoComuCondition_whenAnyProcedimentServeiNoComuPermission() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(BasePermission.READ, null)).thenReturn(
			List.of(5L));
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(BasePermission.READ, null)).thenReturn(
			Collections.emptyList());
		String result = service.additionalSpringFilter("", null);
		assertEquals("entitat.id:1 and (procediment.id in (5))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnProcedimentComuOrganGestorCondition_whenAnyProcedimentServeiComuOrganGestorPermission() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(BasePermission.READ, null)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(BasePermission.READ, null)).thenReturn(
			List.of(7L, 8L));
		String result = service.additionalSpringFilter("", null);
		assertEquals("entitat.id:1 and (procedimentOrganGestor.id in (7,8))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnAllConditionsWithOr_whenAllPermissionsGranted() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ)).thenReturn(
			List.of(10L));
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(BasePermission.READ, null)).thenReturn(
			List.of(20L));
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(BasePermission.READ, null)).thenReturn(
			List.of(30L));
		String result = service.additionalSpringFilter("", null);
		assertEquals("entitat.id:1 and (organGestor.id in (10) or procediment.id in (20) or procedimentOrganGestor.id in (30))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnEmptyOrCondition_whenNoPermissionsAtAll() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(BasePermission.READ, null)).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(BasePermission.READ, null)).thenReturn(
			Collections.emptyList());
		String result = service.additionalSpringFilter("", null);
		assertEquals("entitat.id:1", result);
	}

	// =====================================================
	// beforeCreateSave
	// =====================================================

	@Test
	void beforeCreateSaveShouldSetBasicFields() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		EntitatResourceEntity entitat = new EntitatResourceEntity();
		entitat.setDir3Codi("DIR3");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(entitat);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(List.of(1L));
		service.beforeCreateSave(entity, resource, Map.of());
		assertEquals("user", entity.getUsuariCodi());
		assertEquals(entitat, entity.getEntitat());
		assertEquals("DIR3", entity.getEmisorDir3Codi());
		assertNotNull(entity.getReferencia());
		assertEquals(NotificacioEstatEnumDto.PENDENT, entity.getEstat());
	}

	@Test
	void beforeCreateSaveShouldSaveDocuments() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		DocumentResource doc = new DocumentResource();
		doc.setAttachment(
			new FileReference(
				"document.txt",
				new byte[] { 1 },
				"text/plain",
				1));
		resource.setDocumentsInfo(List.of(doc));
		when(legacyHelper.notificacioAdjuntCreate(any())).thenReturn("fileId");
		when(documentRepo.save(any())).thenAnswer(i -> i.getArgument(0));
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(List.of(1L));
		service.beforeCreateSave(entity, resource, Map.of());
		assertNotNull(entity.getDocument());
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenNoPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(any(), any())).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(any(), any())).thenReturn(
			Collections.emptyList());
		assertThrows(ResourceNotCreatedException.class, () -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenOrganGestorPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(
			List.of(1L));
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(any(), any())).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(any(), any())).thenReturn(
			Collections.emptyList());
		assertDoesNotThrow(() -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenProcedimentServeiNoComuPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(any(), any())).thenReturn(
			List.of(2L));
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(any(), any())).thenReturn(
			Collections.emptyList());
		assertDoesNotThrow(() -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenProcedimentServeiComuOrganGestorPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(any(), any())).thenReturn(
			Collections.emptyList());
		when(notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(any(), any())).thenReturn(
			List.of(3L));
		assertDoesNotThrow(() -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	// =====================================================
	// afterCreateSave
	// =====================================================

	@Test
	void afterCreateSaveShouldSaveEnviaments() {
		NotificacioEnviamentResource enviament = new NotificacioEnviamentResource();
		enviament.setTitularInfo(new PersonaResource());
		resource.setEnviamentsInfo(List.of(enviament));
		NotificacioEnviamentResourceEntity saved = new NotificacioEnviamentResourceEntity();
		when(enviamentRepo.saveAndFlush(any())).thenReturn(saved);
		when(personaRepo.save(any())).thenReturn(new PersonaResourceEntity());
		service.afterCreateSave(entity, resource, Map.of(), false);
		verify(enviamentRepo).saveAndFlush(any());
	}

	@Test
	void afterCreateSaveShouldCallLegacyHelper() {
		NotificacioResourceEntity entity = new NotificacioResourceEntity();
		NotificacioResource resource = new NotificacioResource();
		Map<String, AnswerRequiredException.AnswerValue> answers = Map.of();
		// Afegim enviaments ficticis per veure que saveEnviaments també es crida
		NotificacioEnviamentResource enviament = new NotificacioEnviamentResource();
		PersonaResource titular = new PersonaResource();
		enviament.setTitularInfo(titular);
		resource.setEnviamentsInfo(List.of(enviament));
		NotificacioEnviamentResourceEntity saved = new NotificacioEnviamentResourceEntity();
		saved.setId(11L);
		when(enviamentRepo.saveAndFlush(any())).thenReturn(saved);
		service.afterCreateSave(entity, resource, answers, false);
		// Comprovem que s’ha cridat el legacyHelper amb la mateixa entitat
		verify(legacyHelper).altaNotificacio(entity.getId(), List.of(11L));
	}

	// =====================================================
	// onChange (init)
	// =====================================================

	@Test
	void initOnChangeShouldTriggerCaducitatOnChange() {
		NotificacioResource previous = new NotificacioResource();
		NotificacioResource target = new NotificacioResource();
		var processor = new NotificacioResourceServiceImpl.InitOnChangeLogicProcessor();
		processor.onChange(
			null,
			previous,
			null,
			null,
			Map.of(),
			new String[]{},
			target
		);
		assertNotNull(target.getCaducitat());
	}

	// =====================================================
	// onChange (organGestor)
	// =====================================================

	@Test
	void organGestorOnChangeShouldSetProcedimentRequiredTrue_whenComunicacionsSenseProcedimentPermissionNotGranted() {
		NotificacioResource previous = new NotificacioResource();
		previous.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
		NotificacioResource target = new NotificacioResource();
		ResourceReference<OrganGestorResource, Long> organGestor = ResourceReference.toResourceReference(1L);
		var processor = service.new OrganGestorOnChangeLogicProcessor();
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(
			List.of(10L));
		processor.onChange(
			null,
			previous,
			NotificacioResource.Fields.organGestor,
			organGestor,
			Map.of(),
			new String[]{},
			target
		);
		assertTrue(target.isProcedimentRequired());
	}

	@Test
	void organGestorOnChangeShouldSetProcedimentRequiredFalse_whenComunicacionsSenseProcedimentPermissionGranted() {
		NotificacioResource previous = new NotificacioResource();
		previous.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
		NotificacioResource target = new NotificacioResource();
		ResourceReference<OrganGestorResource, Long> organGestor = ResourceReference.toResourceReference(1L);
		var processor = service.new OrganGestorOnChangeLogicProcessor();
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any())).thenReturn(
			List.of(1L));
		processor.onChange(
			null,
			previous,
			NotificacioResource.Fields.organGestor,
			organGestor,
			Map.of(),
			new String[]{},
			target
		);
		assertFalse(target.isProcedimentRequired());
	}

	// =====================================================
	// onChange (caducitat)
	// =====================================================

	@Test
	void caducitatOnChangeShouldCalculateDays() {
		NotificacioResource previous = new NotificacioResource();
		NotificacioResource target = new NotificacioResource();
		Date futureDate = Date.from(
			java.time.LocalDate.now().plusDays(5)
				.atStartOfDay(java.time.ZoneId.systemDefault())
				.toInstant()
		);
		var processor = new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor();
		processor.onChange(
			null,
			previous,
			NotificacioResource.Fields.caducitat,
			futureDate,
			Map.of(),
			new String[]{},
			target
		);
		assertNotNull(target.getCaducitatDiesNaturals());
	}

	@Test
	void caducitatOnChangeShouldCalculateDate() {
		NotificacioResource previous = new NotificacioResource();
		NotificacioResource target = new NotificacioResource();
		var processor = new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor();
		processor.onChange(
			null,
			previous,
			NotificacioResource.Fields.caducitatDiesNaturals,
			5,
			Map.of(),
			new String[]{},
			target
		);
		assertNotNull(target.getCaducitat());
	}

}
