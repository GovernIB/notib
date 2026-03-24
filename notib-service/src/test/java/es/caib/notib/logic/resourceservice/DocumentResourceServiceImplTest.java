package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test unitari per a DocumentResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class DocumentResourceServiceImplTest {

	@InjectMocks
	private DocumentResourceServiceImpl service;

	@Test
	void additionalSpringFilterShouldReturnIdIsNull() {
		String filter = service.additionalSpringFilter(null, null);
		assertEquals("id is null", filter);
	}

	@Test
	void beforeCreateSaveShouldThrowException() {
		ResourceNotCreatedException exception = assertThrows(
			ResourceNotCreatedException.class,
			() -> service.beforeCreateSave(
				new DocumentResourceEntity(),
				new DocumentResource(),
				null));
		assertEquals(DocumentResource.class, exception.getClazz());
		assertEquals("Create is not allowed", exception.getReason());
	}

}
