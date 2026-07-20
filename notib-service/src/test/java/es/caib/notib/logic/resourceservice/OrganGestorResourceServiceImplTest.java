package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.OrganGestorSyncHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.*;
import es.caib.notib.persist.resourcerepository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per a OrganGestorResourceServiceImpl.
 * <p>
 * Cobertura:
 *  - additionalSpringFilter
 *  - afterConversion
 *  - beforeUpdateSave
 *  - Dir3SyncActionExecutor
 */
@ExtendWith(MockitoExtension.class)
class OrganGestorResourceServiceImplTest {

	@Mock private UserSessionHelper userSessionHelper;
	@Mock private AuthenticationHelper authenticationHelper;
	@Mock private NotibPermissionHelper notibPermissionHelper;
	@Mock private AclHelper aclHelper;
	@Mock private OrganGestorSyncHelper syncHelper;
	@Mock private EntitatResourceRepository entitatRepo;
	@Mock private OrganGestorResourceRepository organGestorResourceRepository;
	@Mock private PagadorPostalResourceRepository pagadorPostalRepo;
	@Mock private PagadorCieResourceRepository pagadorCieRepo;
	@Mock private EntregaCieResourceRepository entregaCieRepo;
	@Mock private OrganGestorService organGestorService;

	private OrganGestorResourceServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new OrganGestorResourceServiceImpl(
			userSessionHelper,
			authenticationHelper,
			notibPermissionHelper,
			aclHelper,
			syncHelper,
			entitatRepo,
			organGestorResourceRepository,
			pagadorPostalRepo,
			pagadorCieRepo,
			entregaCieRepo,
			organGestorService
		);
	}

	// =====================================================
	// additionalSpringFilter
	// =====================================================

	@Test
	void shouldReturnSuperFilterIfAdmin() {
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER)).thenReturn(false);
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
		String result = service.additionalSpringFilter("base", new String[]{});
		assertNotNull(result);
	}

	@Test
	void shouldAddFilterWhenNotAdminAndPermissionsExist() {
		when(authenticationHelper.isCurrentUserInRole(any())).thenReturn(false);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any()))
			.thenReturn(List.of(1L, 2L));
		String result = service.additionalSpringFilter(
			"base",
			new String[]{OrganGestorResource.NAMED_QUERY_PERM_READ}
		);
		assertTrue(result.contains("id in"));
	}

	// =====================================================
	// afterConversion
	// =====================================================

	@Test
	void afterConversionShouldSetAclCount() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		entity.setId(1L);
		OrganGestorResource resource = new OrganGestorResource();
		when(aclHelper.count(any(), any(), any())).thenReturn(5);
		service.afterConversion(entity, resource);
		assertEquals(5, resource.getAclEntryCount());
	}

	@Test
	void afterConversionShouldMapEntregaCie() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		OrganGestorResource resource = new OrganGestorResource();
		EntregaCieResourceEntity entrega = new EntregaCieResourceEntity();
		PagadorCieResourceEntity cie = new PagadorCieResourceEntity();
		cie.setId(1L);
		cie.setNom("cie");
		PagadorPostalResourceEntity postal = new PagadorPostalResourceEntity();
		postal.setId(2L);
		postal.setNomContracteNum("postal");
		entrega.setPagadorCie(cie);
		entrega.setPagadorPostal(postal);
		entity.setEntregaCie(entrega);
		when(aclHelper.count(any(), any(), any())).thenReturn(1);
		service.afterConversion(entity, resource);
		assertNotNull(resource.getEntregaCiePagadorCie());
		assertNotNull(resource.getEntregaCiePagadorPostal());
	}

	@Test
	void afterConversionShouldHandleNullEntregaCie() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		OrganGestorResource resource = new OrganGestorResource();
		when(aclHelper.count(any(), any(), any())).thenReturn(1);
		service.afterConversion(entity, resource);
		assertNull(resource.getEntregaCiePagadorCie());
		assertNull(resource.getEntregaCiePagadorPostal());
	}

	// =====================================================
	// beforeUpdateSave
	// =====================================================

	@Test
	void shouldClearEntregaCieIfNotActive() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		entity.setEntregaCie(new EntregaCieResourceEntity());
		OrganGestorResource resource = new OrganGestorResource();
		resource.setEntregaCieActiva(false);
		service.beforeUpdateSave(entity, resource, Map.of());
		assertNull(entity.getEntregaCie());
	}

	@Test
	void shouldDoNothingIfReferencesAreNull() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		OrganGestorResource resource = new OrganGestorResource();
		resource.setEntregaCieActiva(true);
		service.beforeUpdateSave(entity, resource, Map.of());
		verifyNoInteractions(pagadorCieRepo, pagadorPostalRepo);
	}

	@Test
	void shouldDoNothingIfRepositoriesReturnEmpty() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		OrganGestorResource resource = new OrganGestorResource();
		resource.setEntregaCieActiva(true);
		resource.setEntregaCiePagadorCie(ResourceReference.toResourceReference(1L, "cie"));
		resource.setEntregaCiePagadorPostal(ResourceReference.toResourceReference(2L, "postal"));
		when(pagadorCieRepo.findById(1L)).thenReturn(Optional.empty());
		when(pagadorPostalRepo.findById(2L)).thenReturn(Optional.empty());
		service.beforeUpdateSave(entity, resource, Map.of());
		verify(entregaCieRepo, never()).save(any());
	}

	@Test
	void shouldCreateAndSaveEntregaCie() {
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		OrganGestorResource resource = new OrganGestorResource();
		resource.setEntregaCieActiva(true);
		resource.setEntregaCiePagadorCie(ResourceReference.toResourceReference(1L, "cie"));
		resource.setEntregaCiePagadorPostal(ResourceReference.toResourceReference(2L, "postal"));
		PagadorCieResourceEntity cie = new PagadorCieResourceEntity();
		PagadorPostalResourceEntity postal = new PagadorPostalResourceEntity();
		when(pagadorCieRepo.findById(1L)).thenReturn(Optional.of(cie));
		when(pagadorPostalRepo.findById(2L)).thenReturn(Optional.of(postal));
		when(entregaCieRepo.save(any())).thenReturn(new EntregaCieResourceEntity());
		service.beforeUpdateSave(entity, resource, Map.of());
		verify(entregaCieRepo).save(any());
		assertNotNull(entity.getEntregaCie());
	}

	// =====================================================
	// Dir3SyncActionExecutor
	// =====================================================

	@Test
	void shouldExecuteSyncSuccessfully() throws Exception {
		var executor = service.new Dir3SyncActionExecutor();
		OrganGestorResourceEntity entity = new OrganGestorResourceEntity();
		OrganGestorResource.OrganGestorDir3SyncForm form =
			new OrganGestorResource.OrganGestorDir3SyncForm();
		form.setSimular(true);
		EntitatResourceEntity entitat = new EntitatResourceEntity();
		OrganGestorDir3Sync organGestorDir3Sync = new OrganGestorDir3Sync(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			false,
			false);
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(entitatRepo.findById(1L)).thenReturn(Optional.of(entitat));
		when(syncHelper.sincronitzar(any(), eq(true))).thenReturn(organGestorDir3Sync);
		var result = executor.exec("code", entity, form);
		assertNotNull(result);
	}

	@Test
	void shouldThrowExceptionIfEntitatNotFound() {
		var executor = service.new Dir3SyncActionExecutor();
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(entitatRepo.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ActionExecutionException.class,
			() -> executor.exec(
				"code",
				new OrganGestorResourceEntity(),
				new OrganGestorResource.OrganGestorDir3SyncForm()
			));
	}

}
