package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.EntitatTipusDocumentResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.EntitatTipusDocumentResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.BasePermission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaris per EntitatTipusDocumentResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class EntitatTipusDocumentResourceServiceImplTest {

	@Mock
	private NotibPermissionHelper notibPermissionHelper;
	@InjectMocks
	private EntitatTipusDocumentResourceServiceImpl service;

	@Test
	void additionalSpringFilterShouldCallPermissionHelper() {
		String expectedFilter = "filter123";
		when(notibPermissionHelper.entitatAdditionalSpringFilter("entitat.id")).thenReturn(expectedFilter);
		String result = service.additionalSpringFilter(null, null);
		assertEquals(expectedFilter, result);
		verify(notibPermissionHelper).entitatAdditionalSpringFilter("entitat.id");
	}

	@Test
	void beforeCreateEntityShouldCheckCreatePermission() {
		EntitatTipusDocumentResource resource = new EntitatTipusDocumentResource();
		resource.setEntitat(ResourceReference.toResourceReference(10L));
		service.beforeCreateEntity(
			EntitatTipusDocumentResourceEntity.builder().resource(resource).build(),
			resource,
			null);
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			EntitatTipusDocumentResource.class,
			null,
			10L,
			BasePermission.CREATE);
	}

	@Test
	void beforeUpdateEntityShouldCheckWritePermission() {
		EntitatTipusDocumentResource resource = new EntitatTipusDocumentResource();
		resource.setId(5L);
		resource.setEntitat(ResourceReference.toResourceReference(20L));
		service.beforeUpdateEntity(
			EntitatTipusDocumentResourceEntity.builder().resource(resource).build(),
			resource,
			null);
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			EntitatTipusDocumentResource.class,
			5L,
			20L,
			BasePermission.WRITE);
	}

	@Test
	void beforeDeleteShouldCheckDeletePermission() {
		EntitatResource entitatResource = new EntitatResource();
		entitatResource.setId(30L);
		EntitatTipusDocumentResource resource = new EntitatTipusDocumentResource();
		resource.setId(7L);
		resource.setEntitat(ResourceReference.toResourceReference(entitatResource.getId()));
		EntitatResourceEntity entitatResourceEntity = EntitatResourceEntity.builder().resource(entitatResource).build();
		entitatResourceEntity.setId(entitatResource.getId());
		EntitatTipusDocumentResourceEntity entity = EntitatTipusDocumentResourceEntity.builder().
			resource(resource).
			entitat(entitatResourceEntity).
			build();
		entity.setId(resource.getId());
		service.beforeDelete(entity, null);
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			EntitatTipusDocumentResource.class,
			7L,
			30L,
			BasePermission.DELETE);
	}

}
