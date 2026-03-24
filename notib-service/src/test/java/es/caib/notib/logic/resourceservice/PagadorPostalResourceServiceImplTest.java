package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test bàsic per a PagadorPostalResourceServiceImpl.
 * <p>
 * Aquesta classe no té lògica pròpia, només es comprova:
 *  - creació correcta
 *  - no null
 */
@ExtendWith(MockitoExtension.class)
class PagadorPostalResourceServiceImplTest {

	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;

	@Test
	void shouldCreateInstance() {
		PagadorPostalResourceServiceImpl service =
			new PagadorPostalResourceServiceImpl(
				userSessionHelper,
				authenticationHelper,
				notibPermissionHelper
			);
		assertNotNull(service);
	}

}
