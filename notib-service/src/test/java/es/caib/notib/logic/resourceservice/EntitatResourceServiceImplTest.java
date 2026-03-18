package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
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

@ExtendWith(MockitoExtension.class)
public class EntitatResourceServiceImplTest {

	@Mock
	private AclHelper aclHelper;
	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;

	@InjectMocks
	private EntitatResourceServiceImpl service;

	@Test
	void shouldSetAclEntryCountAfterConversion() {
		// given
		EntitatResourceEntity entity = new EntitatResourceEntity();
		entity.setId(1L);
		EntitatResource resource = new EntitatResource();
		when(aclHelper.count(any(), eq(1L), isNull())).thenReturn(5);
		// when
		service.afterConversion(entity, resource);
		// then
		assertEquals(5, resource.getAclEntryCount());
		verify(aclHelper).count(AclHelper.ENTITAT_CLASS, 1L, null);
	}

	@Test
	void shouldCallPermissionCheckBeforeCreate() {
		// given
		EntitatResource resource = new EntitatResource();
		// when
		service.beforeCreateEntity(null, resource, Map.of());
		// then
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			any(),
			isNull(),
			isNull(),
			eq(BasePermission.CREATE)
		);
	}

	@Test
	void shouldCallPermissionCheckBeforeUpdate() {
		// given
		EntitatResource resource = new EntitatResource();
		resource.setId(10L);
		// when
		service.beforeUpdateEntity(null, resource, Map.of());
		// then
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			any(),
			eq(10L),
			eq(10L),
			eq(BasePermission.WRITE)
		);
	}

	@Test
	void shouldCallPermissionCheckBeforeDelete() {
		// given
		EntitatResourceEntity entity = new EntitatResourceEntity();
		entity.setId(20L);
		// when
		service.beforeDelete(entity, Map.of());
		// then
		verify(notibPermissionHelper).entitatCheckAdminPermission(
			any(),
			eq(20L),
			eq(20L),
			eq(BasePermission.DELETE)
		);
	}

	@Test
	void additionalSpringFilterShouldReturnHelperValue() {
		// given
		String expectedFilter = "custom filter";
		when(notibPermissionHelper.entitatAdditionalSpringFilter("id"))
			.thenReturn(expectedFilter);
		// when
		String result = service.additionalSpringFilter("currentFilter", new String[]{});
		// then
		assertEquals(expectedFilter, result);
		verify(notibPermissionHelper).entitatAdditionalSpringFilter("id");
	}

}
