package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.AplicacioResource;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.persist.resourceentity.AplicacioResourceEntity;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.BasePermission;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitari per a AplicacioResourceServiceImpl.
 * <p>
 * Cobreix:
 *  - additionalSpringFilter: delegació a NotibPermissionHelper
 *  - beforeCreateEntity, beforeUpdateEntity, beforeDelete: crida a permisos correctes
 */
@ExtendWith(MockitoExtension.class)
class AplicacioResourceServiceImplTest {

	@Mock
	private NotibPermissionHelper notibPermissionHelper;
	@InjectMocks
	private AplicacioResourceServiceImpl service;

	private AplicacioResourceEntity entity;
	private AplicacioResource resource;

	@BeforeEach
	void setUp() {
		resource = new AplicacioResource();
		resource.setId(10L);
		EntitatResource entitat = new EntitatResource();
		entitat.setId(5L);
		resource.setEntitat(ResourceReference.toResourceReference(entitat.getId()));
		EntitatResourceEntity entitatEntity = EntitatResourceEntity.builder().
			resource(entitat).
			build();
		entitatEntity.setId(entitat.getId());
		entity = AplicacioResourceEntity.builder().
			resource(resource).
			entitat(entitatEntity).
			build();
		entity.setId(resource.getId());
	}

	@Test
	void additionalSpringFilterShouldReturnHelperValue() {
		when(notibPermissionHelper.entitatAdditionalSpringFilter("entitat.id"))
			.thenReturn("customFilter");
		String result = service.additionalSpringFilter("currentFilter", new String[]{});
		assertEquals("customFilter", result);
		verify(notibPermissionHelper).entitatAdditionalSpringFilter("entitat.id");
	}

	@Test
	void beforeCreateEntityShouldCallPermissionCheck() {
		service.beforeCreateEntity(entity, resource, Map.of());
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			any(),
			isNull(),
			eq(5L),
			eq(BasePermission.CREATE)
		);
	}

	@Test
	void beforeUpdateEntityShouldCallPermissionCheck() {
		service.beforeUpdateEntity(entity, resource, Map.of());
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			any(),
			eq(10L),
			eq(5L),
			eq(BasePermission.WRITE)
		);
	}

	@Test
	void beforeDeleteShouldCallPermissionCheck() {
		entity.setId(20L);
		service.beforeDelete(entity, Map.of());
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			any(),
			eq(20L),
			eq(5L),
			eq(BasePermission.DELETE)
		);
	}

}
