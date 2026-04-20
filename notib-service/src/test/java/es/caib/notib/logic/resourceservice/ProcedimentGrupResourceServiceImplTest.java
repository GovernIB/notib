package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.model.ProcedimentGrupResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.GrupResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentGrupResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.BasePermission;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * Test unitari per a ProcedimentGrupResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ProcedimentGrupResourceServiceImplTest {

	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;
	@InjectMocks
	private ProcedimentGrupResourceServiceImpl service;

	private EntitatResourceEntity sessionEntitat;
	private ProcedimentGrupResourceEntity entity;
	private ProcedimentGrupResource resource;

	@BeforeEach
	void setUp() {
		sessionEntitat = new EntitatResourceEntity();
		sessionEntitat.setId(1L);
		resource = new ProcedimentGrupResource();
		entity = new ProcedimentGrupResourceEntity();
		entity.setId(10L);
		entity.setEntitat(sessionEntitat);
		EntitatResourceEntity grupEntitat = new EntitatResourceEntity();
		grupEntitat.setId(1L);
		GrupResourceEntity grup = new GrupResourceEntity();
		grup.setEntitat(grupEntitat);
		entity.setGrup(grup);
		ProcedimentResourceEntity procediment = new ProcedimentResourceEntity();
		procediment.setEntitat(sessionEntitat);
		entity.setProcediment(procediment);
		when(userSessionHelper.getCurrentEntitat()).thenReturn(sessionEntitat);
	}

	@Test
	void beforeCreateSaveShouldCallPermissionIfEntitatMatches() {
		service.beforeCreateSave(entity, resource, Map.of());
		verify(notibPermissionHelper, atLeastOnce()).entitatCheckAdminPermissionThrows(
			eq(ProcedimentGrupResource.class),
			isNull(),
			eq(sessionEntitat.getId()),
			eq(BasePermission.CREATE)
		);
	}

	@Test
	void beforeCreateSaveShouldThrowIfEntitatDiffers() {
		entity.getGrup().setEntitat(new EntitatResourceEntity());
		entity.getGrup().getEntitat().setId(99L); // diferent
		ResourceNotCreatedException exception = assertThrows(
			ResourceNotCreatedException.class,
			() -> service.beforeCreateSave(entity, resource, Map.of())
		);
		assertTrue(exception.getMessage().contains("Not allowed to create"));
	}

	@Test
	void beforeUpdateEntityShouldCallPermissionIfEntitatMatches() {
		service.beforeUpdateEntity(entity, resource, Map.of());
		verify(notibPermissionHelper, atLeastOnce()).entitatCheckAdminPermissionThrows(
			eq(ProcedimentGrupResource.class),
			eq(entity.getId()),
			eq(sessionEntitat.getId()),
			eq(BasePermission.WRITE)
		);
	}

	@Test
	void beforeUpdateEntityShouldThrowIfEntitatDiffers() {
		entity.getGrup().setEntitat(new EntitatResourceEntity());
		entity.getGrup().getEntitat().setId(99L); // diferent
		ResourceNotUpdatedException exception = assertThrows(
			ResourceNotUpdatedException.class,
			() -> service.beforeUpdateEntity(entity, resource, Map.of())
		);
		assertTrue(exception.getMessage().contains("Not allowed to update"));
	}

	@Test
	void beforeDeleteShouldCallPermissionIfEntitatMatches() {
		service.beforeDelete(entity, Map.of());
		verify(notibPermissionHelper, atLeastOnce()).entitatCheckAdminPermissionThrows(
			eq(ProcedimentGrupResource.class),
			eq(entity.getId()),
			eq(sessionEntitat.getId()),
			eq(BasePermission.DELETE)
		);
	}

	@Test
	void beforeDeleteShouldThrowIfEntitatDiffers() {
		entity.getGrup().setEntitat(new EntitatResourceEntity());
		entity.getGrup().getEntitat().setId(99L); // diferent
		ResourceNotDeletedException exception = assertThrows(
			ResourceNotDeletedException.class,
			() -> service.beforeDelete(entity, Map.of())
		);
		assertTrue(exception.getMessage().contains("Not allowed to delete"));
	}

}
