package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.procSer.ComuOnChangeLogicProcessor;
import es.caib.notib.logic.service.ProcedimentServiceImpl;
import es.caib.notib.logic.service.ServeiServiceImpl;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import es.caib.notib.persist.resourceentity.PagadorCieResourceEntity;
import es.caib.notib.persist.resourceentity.PagadorPostalResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.notib.persist.resourcerepository.EntregaCieResourceRepository;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorPostalResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per a ProcedimentResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ProcedimentResourceServiceImplTest {

	@Mock
	private AclHelper aclHelper;
	@Mock
	private OrganGestorResourceRepository organGestorResourceRepository;
	@Mock
	private ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;
	@Mock
	private PagadorPostalResourceRepository pagadorPostalRepository;
	@Mock
	private PagadorCieResourceRepository pagadorCieRepository;
	@Mock
	private ProcedimentResourceRepository procedimentResourceRepository;
	@Mock
	private EntregaCieResourceRepository entregaCieRepository;
	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;
	private ProcedimentResourceServiceImpl service;
	@Mock
	private PaginacioHelper paginacioHelper;
	@Mock
	private ProcedimentServiceImpl procedimentService;
	@Mock
	private ServeiServiceImpl serveiService;

	@BeforeEach
	void setUp() {
		service = new ProcedimentResourceServiceImpl(
			userSessionHelper,
			authenticationHelper,
			notibPermissionHelper,
			aclHelper,
			pagadorPostalRepository,
			pagadorCieRepository,
			entregaCieRepository,
			procedimentResourceRepository,
			organGestorResourceRepository,
			procedimentOrganGestorResourceRepository,
			paginacioHelper,
			procedimentService,
			serveiService
		);
	}

	@Test
	void afterConversionShouldSetAclCount() {
		ProcedimentResourceEntity entity = new ProcedimentResourceEntity();
		entity.setId(1L);
		ProcedimentResource resource = new ProcedimentResource();
		when(aclHelper.count(any(), eq(1L), isNull())).thenReturn(3);
		service.afterConversion(entity, resource);
		assertEquals(3, resource.getAclEntryCount());
	}

	@Test
	void afterConversionShouldMapEntregaCieIfExists() {
		ProcedimentResourceEntity entity = new ProcedimentResourceEntity();
		entity.setId(1L);
		PagadorCieResourceEntity pagadorCie = new PagadorCieResourceEntity();
		pagadorCie.setId(10L);
		pagadorCie.setNom("CIE");
		PagadorPostalResourceEntity pagadorPostal = new PagadorPostalResourceEntity();
		pagadorPostal.setId(20L);
		pagadorPostal.setNomContracteNum("POSTAL");
		EntregaCieResourceEntity entrega = new EntregaCieResourceEntity();
		entrega.setPagadorCie(pagadorCie);
		entrega.setPagadorPostal(pagadorPostal);
		entity.setEntregaCie(entrega);
		ProcedimentResource resource = new ProcedimentResource();
		when(aclHelper.count(any(), any(), any())).thenReturn(1);
		service.afterConversion(entity, resource);
		assertNotNull(resource.getEntregaCiePagadorCie());
		assertNotNull(resource.getEntregaCiePagadorPostal());
	}

	@Test
	void beforeUpdateSaveShouldRemoveEntregaCieIfNotActive() {
		ProcedimentResourceEntity entity = new ProcedimentResourceEntity();
		entity.setEntregaCie(new EntregaCieResourceEntity());
		ProcedimentResource resource = new ProcedimentResource();
		resource.setEntregaCieActiva(false);
		service.beforeUpdateSave(entity, resource, Map.of());
		assertNull(entity.getEntregaCie());
	}

	@Test
	void beforeUpdateSaveShouldDoNothingIfMissingReferences() {
		ProcedimentResourceEntity entity = new ProcedimentResourceEntity();
		ProcedimentResource resource = new ProcedimentResource();
		resource.setEntregaCieActiva(true);
		resource.setEntregaCiePagadorPostal(null);
		service.beforeUpdateSave(entity, resource, Map.of());
		verifyNoInteractions(pagadorPostalRepository);
	}

	@Test
	void beforeUpdateSaveShouldCreateAndSaveEntregaCie() {
		ProcedimentResourceEntity entity = new ProcedimentResourceEntity();
		ProcedimentResource resource = new ProcedimentResource();
		resource.setEntregaCieActiva(true);
		resource.setEntregaCiePagadorPostal(
			ResourceReference.toResourceReference(1L, "postal"));
		resource.setEntregaCiePagadorCie(
			ResourceReference.toResourceReference(2L, "cie"));
		PagadorPostalResourceEntity postal = new PagadorPostalResourceEntity();
		PagadorCieResourceEntity cie = new PagadorCieResourceEntity();
		when(pagadorPostalRepository.findById(1L)).thenReturn(Optional.of(postal));
		when(pagadorCieRepository.findById(2L)).thenReturn(Optional.of(cie));
		service.beforeUpdateSave(entity, resource, Map.of());
		assertNotNull(entity.getEntregaCie());
		verify(entregaCieRepository).save(any());
	}

	@Test
	void comuOnChangeShouldSetFieldsWhenTrue() {
		ProcedimentResource target = new ProcedimentResource();
		var processor = new ComuOnChangeLogicProcessor(organGestorResourceRepository, userSessionHelper);
		processor.onChange(
			null,
			null,
			"comu",
			true,
			Map.of(),
			new String[]{},
			target
		);
		assertTrue(target.isFieldEntregaCieHidden());
		assertTrue(target.isFieldOrganGestorDisabled());
		assertNotNull(target.getOrganGestor());
	}

	@Test
	void comuOnChangeShouldResetFieldsWhenFalse() {
		ProcedimentResource target = new ProcedimentResource();
		var processor = new ComuOnChangeLogicProcessor(organGestorResourceRepository, userSessionHelper);
		processor.onChange(
			null,
			null,
			"comu",
			false,
			Map.of(),
			new String[]{},
			target
		);
		assertFalse(target.isFieldEntregaCieHidden());
		assertFalse(target.isFieldOrganGestorDisabled());
		assertNull(target.getOrganGestor());
	}

}
