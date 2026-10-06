package es.caib.notib.logic.helper;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.security.acls.model.Sid;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests de NotibPermissionHelper.isCurrentUserAdminEntitat.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotibPermissionHelperAdminEntitatTest {

	@Mock private AclHelper aclHelper;
	@Mock private UserSessionHelper userSessionHelper;
	@Mock private AuthenticationHelper authenticationHelper;

	@InjectMocks
	private NotibPermissionHelper helper;

	@BeforeEach
	void setUp() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(5L);
		when(aclHelper.getCurrentUserSids()).thenReturn(List.of(mock(Sid.class)));
	}

	@Test
	void shouldBeAdmin_whenRoleGrantedSelectedAndEntitatPermission() {
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
		when(userSessionHelper.getCurrentRol()).thenReturn(BaseConfig.ROLE_ADMIN);
		when(aclHelper.anyPermissionGranted(any(), eq(5L), any(), any())).thenReturn(true);

		assertTrue(helper.isCurrentUserAdminEntitat());
	}

	@Test
	void shouldNotBeAdmin_whenAnotherRoleIsSelected() {
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
		when(userSessionHelper.getCurrentRol()).thenReturn(BaseConfig.ROLE_USER);
		when(aclHelper.anyPermissionGranted(any(), eq(5L), any(), any())).thenReturn(true);

		assertFalse(helper.isCurrentUserAdminEntitat());
	}

	@Test
	void shouldNotBeAdmin_whenRoleHeaderForgedWithoutGrantedRole() {
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(false);
		when(userSessionHelper.getCurrentRol()).thenReturn(BaseConfig.ROLE_ADMIN);
		when(aclHelper.anyPermissionGranted(any(), eq(5L), any(), any())).thenReturn(true);

		assertFalse(helper.isCurrentUserAdminEntitat());
	}

	@Test
	void shouldNotBeAdmin_whenNoAdminPermissionOnEntitat() {
		when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
		when(userSessionHelper.getCurrentRol()).thenReturn(BaseConfig.ROLE_ADMIN);
		when(aclHelper.anyPermissionGranted(any(), eq(5L), any(), any())).thenReturn(false);

		assertFalse(helper.isCurrentUserAdminEntitat());
	}

}
