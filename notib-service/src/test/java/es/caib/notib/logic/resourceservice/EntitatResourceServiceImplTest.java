package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.helper.ResourceEntityMappingHelper;
import es.caib.notib.logic.base.helper.ResourceReferenceToEntityHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitari per a EntitatResourceServiceImpl.
 * <p>
 * Comprova els mètodes afterConversion, beforeCreate, beforeUpdate, beforeDelete i
 * additionalSpringFilter.
 */
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
	@Mock
	private EntitatResourceRepository entitatResourceRepository;
	@Mock
	private ResourceEntityMappingHelper resourceEntityMappingHelper;
	@Mock
	private ResourceReferenceToEntityHelper resourceReferenceToEntityHelper;
	@InjectMocks
	private EntitatResourceServiceImpl service;

	@BeforeEach
	void setUp() throws IllegalAccessException {
		Field entityRepositoryField = ReflectionUtils.findField(
			service.getClass(),
			"entityRepository");
		if (entityRepositoryField != null) {
			entityRepositoryField.setAccessible(true);
			entityRepositoryField.set(service, entitatResourceRepository);
		}
		Field resourceEntityMappingHelperField = ReflectionUtils.findField(
			service.getClass(),
			"resourceEntityMappingHelper");
		if (resourceEntityMappingHelperField != null) {
			resourceEntityMappingHelperField.setAccessible(true);
			resourceEntityMappingHelperField.set(service, resourceEntityMappingHelper);
		}
		Field resourceReferenceToEntityHelperField = ReflectionUtils.findField(
			service.getClass(),
			"resourceReferenceToEntityHelper");
		if (resourceReferenceToEntityHelperField != null) {
			resourceReferenceToEntityHelperField.setAccessible(true);
			resourceReferenceToEntityHelperField.set(service, resourceReferenceToEntityHelper);
		}
		service.init();
	}

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

	@Test
	void permisosPerspectiveApplicatorShouldSetAllFalseIfNotRoleUser() throws PerspectiveApplicationException {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		when(entitatResourceRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
		when(resourceEntityMappingHelper.entityToResource(any(), any())).thenReturn(new EntitatResource());
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER)).thenReturn(false);
		EntitatResource resource = service.getOne(
			1L,
			new String[] { EntitatResource.PERSPECTIVE_PERMISSIONS });
		assertFalse(resource.isCrearNotificacions());
		assertFalse(resource.isCrearComunicacions());
		assertFalse(resource.isCrearSir());
	}

	@Test
	void permisosPerspectiveApplicatorShouldSetFlagsTrueWhenUserAndPermissionsExist() throws PerspectiveApplicationException {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		when(entitatResourceRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
		when(resourceEntityMappingHelper.entityToResource(any(), any())).thenReturn(new EntitatResource());
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER)).thenReturn(true);
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any()))
			.thenReturn(List.of(1L));
		when(notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(any(), any()))
			.thenReturn(List.of(1L));
		EntitatResource resource = service.getOne(
			1L,
			new String[] { EntitatResource.PERSPECTIVE_PERMISSIONS });
		assertTrue(resource.isCrearNotificacions());
		assertTrue(resource.isCrearComunicacions());
		assertTrue(resource.isCrearSir());
	}

	@Test
	void logoCapsaleraFieldFileManagerRead_whenContentExists_returnsFileReference() {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		byte[] content = "image-data".getBytes();
		entity.setLogoCapsalera(content);
		when(entitatResourceRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
		when(resourceEntityMappingHelper.entityToResource(any(), any())).thenReturn(new EntitatResource());
		EntitatResource resource = service.getOne(1L, null);
		FileReference result = resource.getLogoCapsalera();
		assertNotNull(result);
		assertEquals("logo.jpg", result.getName());
		assertEquals("image/jpeg", result.getContentType());
		assertArrayEquals(content, result.getContent());
		assertEquals(content.length, result.getContentLength());
	}

	@Test
	void logoCapsaleraFieldFileManagerRead_whenContentIsNull_returnsNull() {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		entity.setLogoCapsalera(null);
		when(entitatResourceRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
		when(resourceEntityMappingHelper.entityToResource(any(), any())).thenReturn(new EntitatResource());
		EntitatResource resource = service.getOne(1L, null);
		FileReference result = resource.getLogoCapsalera();
		assertNull(result);
	}

	@Test
	void logoCapsaleraFieldFileManagerSave_whenFileReferenceNotNull_setsContent() {
		EntitatResourceEntity saved = new EntitatResourceEntity();
		when(entitatResourceRepository.saveAndFlush(any())).thenReturn(saved);
		when(resourceEntityMappingHelper.entityToResource(any(), any())).thenReturn(new EntitatResource());
		when(entitatResourceRepository.merge(any())).thenReturn(saved);
		EntitatResource resource = new EntitatResource();
		byte[] content = "abc".getBytes();
		resource.setLogoCapsalera(
			new FileReference(
				"logo.jpg",
				content,
				"image/jpeg",
				content.length));
		EntitatResource created = service.create(resource, null);
		assertNotNull(created.getLogoCapsalera());
		assertArrayEquals(content, created.getLogoCapsalera().getContent());
	}

	@Test
	void logoCapsaleraFieldFileManagerSave_whenFileReferenceIsNull_setsContentToNull() {
		EntitatResourceEntity saved = new EntitatResourceEntity();
		saved.setLogoCapsalera("abc".getBytes());
		when(entitatResourceRepository.findOne(any(Specification.class))).thenReturn(Optional.of(saved));
		when(entitatResourceRepository.saveAndFlush(any())).thenReturn(saved);
		when(resourceEntityMappingHelper.entityToResource(any(), any())).thenReturn(new EntitatResource());
		when(entitatResourceRepository.merge(any())).thenReturn(saved);
		EntitatResource resource = new EntitatResource();
		EntitatResource updated = service.update(1L, resource, null);
		ArgumentCaptor<EntitatResourceEntity> captor = ArgumentCaptor.forClass(EntitatResourceEntity.class);
		verify(entitatResourceRepository).detach(captor.capture());
		assertNull(captor.getValue().getLogoCapsalera());
	}

	/*@Test
	void logoCapsaleraFieldFileManagerDelete_always_setsContentToNull() {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		entity.setLogoCapsalera("something".getBytes());
		service.delete(1L, null);
		assertNull(entity.getLogoCapsalera());
	}*/

}
