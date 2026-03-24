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
 * Test bàsic per a PagadorCieFormatSobreResourceServiceImpl.
 * <p>
 * No té lògica pròpia → només es valida que es pot instanciar.
 */
@ExtendWith(MockitoExtension.class)
class PagadorCieFormatSobreResourceServiceImplTest {

	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;

	@Test
	void shouldCreateInstance() {
		PagadorCieFormatSobreResourceServiceImpl service =
			new PagadorCieFormatSobreResourceServiceImpl(
				userSessionHelper,
				authenticationHelper,
				notibPermissionHelper
			);
		assertNotNull(service);
	}

}
