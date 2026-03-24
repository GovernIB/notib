package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test bàsic per a PagadorCieFormatFullaResourceServiceImpl.
 * <p>
 * Classe sense lògica → només comprovam instanciació.
 */
@ExtendWith(MockitoExtension.class)
class PagadorCieFormatFullaResourceServiceImplTest {

	@Mock
	private UserSessionHelper userSessionHelper;
	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private NotibPermissionHelper notibPermissionHelper;

	@Test
	void shouldCreateInstance() {
		PagadorCieFormatFullaResourceServiceImpl service =
			new PagadorCieFormatFullaResourceServiceImpl(
				userSessionHelper,
				authenticationHelper,
				notibPermissionHelper
			);
		assertNotNull(service);
	}

}
