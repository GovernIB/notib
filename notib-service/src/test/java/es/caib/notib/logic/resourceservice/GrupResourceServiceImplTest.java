package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests unitaris per GrupResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class GrupResourceServiceImplTest {

	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;
	@InjectMocks
	private GrupResourceServiceImpl service;

	@Test
	void additionalSpringFilterShouldReturnNullByDefault() {
		when(userSessionHelper.getCurrentEntitatId()).thenReturn(1L);
		String filter = service.additionalSpringFilter(null, null);
		assertEquals("entitat.id:" + 1L, filter);
	}

}
