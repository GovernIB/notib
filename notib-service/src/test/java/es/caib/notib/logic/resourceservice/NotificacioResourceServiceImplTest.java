package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
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
import org.springframework.context.ApplicationEventPublisher;
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
	@Mock private ApplicationEventPublisher eventPublisher;
	@Mock private ConfigHelper configHelper;
	@Mock private NotificacioEstatAsyncHelper notificacioEstatAsyncHelper;
	@Mock private NotificacioListHelper notificacioListHelper;

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
	void processSortShouldMapCalculatedColumns() {
		var sort = org.springframework.data.domain.Sort.by(
				org.springframework.data.domain.Sort.Order.desc("estatString"),
				org.springframework.data.domain.Sort.Order.asc("enviadaDate"),
				org.springframework.data.domain.Sort.Order.asc("registreNums"),
				org.springframework.data.domain.Sort.Order.desc("titular"),
				org.springframework.data.domain.Sort.Order.asc("concepte"));

		var result = service.processSort(sort);

		assertEquals(org.springframework.data.domain.Sort.by(
				org.springframework.data.domain.Sort.Order.desc("estat"),
				org.springframework.data.domain.Sort.Order.asc("taula.enviadaDate"),
				org.springframework.data.domain.Sort.Order.asc("taula.registreNums"),
				org.springframework.data.domain.Sort.Order.desc("taula.titular"),
				org.springframework.data.domain.Sort.Order.asc("concepte")), result);
	}

	@Test
	@SuppressWarnings("unchecked")
	void additionalSpecificationShouldInnerJoinTaula() {
		var spec = service.additionalSpecification(null, false);
		var root = (javax.persistence.criteria.Root<NotificacioResourceEntity>) mock(javax.persistence.criteria.Root.class);
		var cb = mock(javax.persistence.criteria.CriteriaBuilder.class);

		// Consulta de la pàgina: fetch (la taula es carrega a la mateixa consulta)
		var query = mock(javax.persistence.criteria.CriteriaQuery.class);
		when(query.getResultType()).thenReturn((Class) NotificacioResourceEntity.class);
		assertNull(spec.toPredicate(root, query, cb));
		verify(root).fetch("taula", javax.persistence.criteria.JoinType.INNER);

		// Consulta de COUNT: sense JOIN
		var countQuery = mock(javax.persistence.criteria.CriteriaQuery.class);
		when(countQuery.getResultType()).thenReturn((Class) Long.class);
		spec.toPredicate(root, countQuery, cb);
		verify(root, never()).join(anyString(), any(javax.persistence.criteria.JoinType.class));
		verify(root, times(1)).fetch(anyString(), any(javax.persistence.criteria.JoinType.class));
	}

	@Test
	void additionalSpecificationShouldBeNull_whenSingleResult() {
		assertNull(service.additionalSpecification(null, true));
	}

	@Test
	void additionalSpringFilterShouldReturnNoResults_whenNoPermissions() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>()));
		String result = service.additionalSpringFilter("", null, false);
		assertEquals("entitat.id:1 and createdBy:'usuari1'", result);
	}

	@Test
	void additionalSpringFilterShouldReturnOrganGestorCondition_whenPermissionsExist() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(List.of(10L)),
				new ArrayList<>(),
				new ArrayList<>()));
		String result = service.additionalSpringFilter("", null, false);
		assertEquals("entitat.id:1 and (organGestor.id in (10))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnProcedimentNoComuCondition_whenAnyProcedimentServeiNoComuPermission() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(),
				new ArrayList<>(List.of(5L)),
				new ArrayList<>()));
		String result = service.additionalSpringFilter("", null, false);
		assertEquals("entitat.id:1 and (procediment.id in (5))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnProcedimentComuOrganGestorCondition_whenAnyProcedimentServeiComuOrganGestorPermission() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(List.of(7L, 8L))));
		String result = service.additionalSpringFilter("", null, false);
		assertEquals("entitat.id:1 and (procedimentOrganGestor.id in (7,8))", result);
	}

	@Test
	void additionalSpringFilterShouldReturnAllConditionsWithOr_whenAllPermissionsGranted() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(List.of(10L)),
				new ArrayList<>(List.of(20L)),
				new ArrayList<>(List.of(30L))));
		String result = service.additionalSpringFilter("", null, false);
		assertEquals("entitat.id:1 and (organGestor.id in (10) or procediment.id in (20) or procedimentOrganGestor.id in (30))", result);
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
		when(notibPermissionHelper.getOrganGestorNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM4);
		when(notibPermissionHelper.getProcedimentNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM5);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(ExtendedPermission.PERM4, ExtendedPermission.PERM5)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(List.of(1L)),
				new ArrayList<>(),
				new ArrayList<>()));
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
		when(notibPermissionHelper.getOrganGestorNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM4);
		when(notibPermissionHelper.getProcedimentNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM5);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(ExtendedPermission.PERM4, ExtendedPermission.PERM5)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(List.of(1L)),
				new ArrayList<>(),
				new ArrayList<>()));
		service.beforeCreateSave(entity, resource, Map.of());
		assertNotNull(entity.getDocument());
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenNoPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.getOrganGestorNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM4);
		when(notibPermissionHelper.getProcedimentNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM5);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(ExtendedPermission.PERM4, ExtendedPermission.PERM5)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>()));
		assertThrows(ResourceNotCreatedException.class, () -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenOrganGestorPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.getOrganGestorNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM4);
		when(notibPermissionHelper.getProcedimentNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM5);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(ExtendedPermission.PERM4, ExtendedPermission.PERM5)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(List.of(1L)),
				new ArrayList<>(),
				new ArrayList<>()));
		assertDoesNotThrow(() -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenProcedimentServeiNoComuPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.getOrganGestorNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM4);
		when(notibPermissionHelper.getProcedimentNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM5);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(ExtendedPermission.PERM4, ExtendedPermission.PERM5)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(),
				new ArrayList<>(List.of(2L)),
				new ArrayList<>()));
		assertDoesNotThrow(() -> {
			service.beforeCreateSave(entity, resource, Map.of());
		});
	}

	@Test
	void beforeCreateSaveShouldThrowResourceNotCreatedException_whenProcedimentServeiComuOrganGestorPermissionGranted() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		when(notibPermissionHelper.getOrganGestorNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM4);
		when(notibPermissionHelper.getProcedimentNotificacioCreatePermission(any())).thenReturn(ExtendedPermission.PERM5);
		when(notibPermissionHelper.getIdsToCheckNotificacioPermission(ExtendedPermission.PERM4, ExtendedPermission.PERM5)).thenReturn(
			new NotibPermissionHelper.IdsToCheckNotificacioPermission(
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(List.of(3L))));
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
		saved.setNotificacio(entity);
		when(enviamentRepo.saveAndFlush(any())).thenReturn(saved);
		when(personaRepo.save(any())).thenReturn(new PersonaResourceEntity());
		service.afterCreateSave(entity, resource, Map.of(), false);
		verify(enviamentRepo, times(2)).saveAndFlush(any());
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
		saved.setNotificacio(entity);
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


	// =====================================================
	// afterConversion (columna estat)
	// =====================================================

	@Test
	void afterConversionShouldCalculateEstatsInOneCall_whenNotAsync() {
		var e1 = entitatAmbEstat(10L, "vell1", true);
		var e2 = entitatAmbEstat(11L, "actual", false);
		var e3 = entitatAmbEstat(12L, "vell3", true);
		var r1 = resourceAmbEstat("vell1");
		var r2 = resourceAmbEstat("actual");
		var r3 = resourceAmbEstat("vell3");
		when(legacyHelper.actualitzarColumnesEstat(Set.of(10L, 12L))).thenReturn(Map.of(10L, "nou1", 12L, "nou3"));

		service.afterConversion(List.of(e1, e2, e3), List.of(r1, r2, r3));

		verify(legacyHelper, times(1)).actualitzarColumnesEstat(any());
		verifyNoInteractions(notificacioEstatAsyncHelper);
		assertEquals("nou1", r1.getEstatString());
		assertEquals("actual", r2.getEstatString());
		assertEquals("nou3", r3.getEstatString());
		assertFalse(r1.isEstatPendent());
	}

	@Test
	void afterConversionShouldKeepPreviousEstat_whenCalculationFails() {
		var e1 = entitatAmbEstat(10L, "vell1", true);
		var r1 = resourceAmbEstat("vell1");
		when(legacyHelper.actualitzarColumnesEstat(any())).thenThrow(new RuntimeException("error"));

		assertDoesNotThrow(() -> service.afterConversion(List.of(e1), List.of(r1)));
		assertEquals("vell1", r1.getEstatString());
	}

	@Test
	void afterConversionShouldDeferEstats_whenListAndAsyncEnabled() throws Exception {
		var e1 = entitatAmbEstat(10L, "vell1", true);
		var e2 = entitatAmbEstat(11L, "actual", false);
		var r1 = resourceAmbEstat("vell1");
		var r2 = resourceAmbEstat("actual");
		when(configHelper.getConfigAsBoolean(NotificacioEstatAsyncHelper.PROPERTY_ESTAT_ASINCRON, true)).thenReturn(true);
		when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");

		consultaLlistat().set(true);
		try {
			service.afterConversion(List.of(e1, e2), List.of(r1, r2));
		} finally {
			consultaLlistat().remove();
		}

		verify(notificacioEstatAsyncHelper).calcularEstatsAsync(Set.of(10L), "usuari1");
		verify(legacyHelper, never()).actualitzarColumnesEstat(any());
		assertTrue(r1.isEstatPendent());
		assertEquals("vell1", r1.getEstatString());
		assertFalse(r2.isEstatPendent());
	}

	@Test
	void afterConversionShouldCalculateSynchronously_whenListAndAsyncDisabled() throws Exception {
		var e1 = entitatAmbEstat(10L, "vell1", true);
		var r1 = resourceAmbEstat("vell1");
		when(configHelper.getConfigAsBoolean(NotificacioEstatAsyncHelper.PROPERTY_ESTAT_ASINCRON, true)).thenReturn(false);
		when(legacyHelper.actualitzarColumnesEstat(Set.of(10L))).thenReturn(Map.of(10L, "nou1"));

		consultaLlistat().set(true);
		try {
			service.afterConversion(List.of(e1), List.of(r1));
		} finally {
			consultaLlistat().remove();
		}

		verifyNoInteractions(notificacioEstatAsyncHelper);
		assertEquals("nou1", r1.getEstatString());
		assertFalse(r1.isEstatPendent());
	}

	@Test
	void afterConversionSingleShouldCalculateSynchronously() {
		var e1 = entitatAmbEstat(10L, "vell1", true);
		var r1 = resourceAmbEstat("vell1");
		when(legacyHelper.actualitzarColumnesEstat(Set.of(10L))).thenReturn(Map.of(10L, "nou1"));

		service.afterConversion(e1, r1);

		verifyNoInteractions(notificacioEstatAsyncHelper);
		assertEquals("nou1", r1.getEstatString());
	}

	@Test
	void afterConversionShouldSetPermisProcessar_onlyForFinalitzadaWithProcedimentOrOrganPermission() {
		var e1 = entitatFinalitzable(1L, NotificacioEstatEnumDto.FINALITZADA, "PROC_OK", "ORG_NO");
		var e2 = entitatFinalitzable(2L, NotificacioEstatEnumDto.FINALITZADA, "PROC_NO", "ORG_OK");
		var e3 = entitatFinalitzable(3L, NotificacioEstatEnumDto.FINALITZADA, null, "ORG_OK");
		var e4 = entitatFinalitzable(4L, NotificacioEstatEnumDto.FINALITZADA, "PROC_NO", "ORG_NO");
		var e5 = entitatFinalitzable(5L, NotificacioEstatEnumDto.ENVIADA, "PROC_OK", "ORG_OK");
		var resources = List.of(new NotificacioResource(), new NotificacioResource(), new NotificacioResource(), new NotificacioResource(), new NotificacioResource());
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(7L);
		when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
		when(notificacioListHelper.getCodisProcedimentsAndOrgansAmpPermisProcessar(7L, "usuari1")).thenReturn(List.of("PROC_OK", "ORG_OK"));

		service.afterConversion(List.of(e1, e2, e3, e4, e5), resources);

		assertTrue(resources.get(0).isPermisProcessar());
		assertTrue(resources.get(1).isPermisProcessar());
		assertTrue(resources.get(2).isPermisProcessar());
		assertFalse(resources.get(3).isPermisProcessar());
		assertFalse(resources.get(4).isPermisProcessar());
		// Els codis amb permís es calculen un sol cop per pàgina
		verify(notificacioListHelper, times(1)).getCodisProcedimentsAndOrgansAmpPermisProcessar(any(), any());
	}

	@Test
	void afterConversionShouldNotQueryPermisProcessar_whenNoFinalitzada() {
		var e1 = entitatFinalitzable(1L, NotificacioEstatEnumDto.ENVIADA, "PROC_OK", "ORG_OK");

		service.afterConversion(List.of(e1), List.of(new NotificacioResource()));

		verifyNoInteractions(notificacioListHelper);
	}

	@Test
	void afterConversionSingleShouldSetPermisProcessar() {
		var e1 = entitatFinalitzable(1L, NotificacioEstatEnumDto.FINALITZADA, "PROC_OK", "ORG_NO");
		var r1 = new NotificacioResource();
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(7L);
		when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
		when(notificacioListHelper.getCodisProcedimentsAndOrgansAmpPermisProcessar(7L, "usuari1")).thenReturn(List.of("PROC_OK"));

		service.afterConversion(e1, r1);

		assertTrue(r1.isPermisProcessar());
	}

	private NotificacioResourceEntity entitatFinalitzable(Long id, NotificacioEstatEnumDto estat, String procedimentCodi, String organCodi) {
		var e = entitatAmbEstat(id, "estat", false);
		e.setEstat(estat);
		if (procedimentCodi != null) {
			var procediment = new ProcedimentResourceEntity();
			procediment.setCodi(procedimentCodi);
			e.setProcediment(procediment);
		}
		var organ = new OrganGestorResourceEntity();
		organ.setCodi(organCodi);
		e.setOrganGestor(organ);
		return e;
	}

	private NotificacioResourceEntity entitatAmbEstat(Long id, String estatString, boolean perActualitzar) {
		var taula = new NotificacioTableResourceEntity();
		taula.setEstatString(estatString);
		taula.setPerActualitzar(perActualitzar);
		var e = new NotificacioResourceEntity();
		e.setId(id);
		e.setTaula(taula);
		return e;
	}

	private NotificacioResource resourceAmbEstat(String estatString) {
		var r = new NotificacioResource();
		r.setEstatString(estatString);
		return r;
	}

	@SuppressWarnings("unchecked")
	private static ThreadLocal<Boolean> consultaLlistat() throws Exception {
		var field = NotificacioResourceServiceImpl.class.getDeclaredField("consultaLlistat");
		field.setAccessible(true);
		return (ThreadLocal<Boolean>) field.get(null);
	}

}
