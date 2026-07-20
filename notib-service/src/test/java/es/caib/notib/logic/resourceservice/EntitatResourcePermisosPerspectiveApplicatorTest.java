package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test unitari per a EntitatResourcePermisosPerspectiveApplicator.
 *
 * Comprova el cas en que l'usuari te permisos per a crear algun tipus de notificació i el cas
 * en que l'usuari no te permisos.
 */
@ExtendWith(MockitoExtension.class)
public class EntitatResourcePermisosPerspectiveApplicatorTest {

	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;

	private EntitatResourceServiceImpl.EntitatResourcePermisosPerspectiveApplicator applicator;

	@BeforeEach
	void setUp() {
		applicator = new EntitatResourceServiceImpl.EntitatResourcePermisosPerspectiveApplicator(
			authenticationHelper,
			userSessionHelper,
			notibPermissionHelper);
	}

	@Test
	void shouldSetAllFalseIfNotRoleUser() throws PerspectiveApplicationException {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		EntitatResource resource = new EntitatResource();
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER)).thenReturn(false);
		applicator.applySingle("code", entity, resource);
		assertFalse(resource.isCrearNotificacions());
		assertFalse(resource.isCrearComunicacions());
		assertFalse(resource.isCrearSir());
	}

	@Test
	void shouldSetFlagsTrueWhenUserAndPermissionsExist() throws PerspectiveApplicationException {
		EntitatResourceEntity entity = new EntitatResourceEntity();
		EntitatResource resource = new EntitatResource();
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER)).thenReturn(true);
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		// simulam que hi ha permisos
		when(notibPermissionHelper.organGestorIdsWithPermissionRecursive(any()))
			.thenReturn(List.of(1L));
		applicator.applySingle("code", entity, resource);
		assertTrue(resource.isCrearNotificacions());
		assertTrue(resource.isCrearComunicacions());
		assertTrue(resource.isCrearSir());
	}

}
