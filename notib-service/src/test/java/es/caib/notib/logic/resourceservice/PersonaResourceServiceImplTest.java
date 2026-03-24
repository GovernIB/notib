package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per a PersonaResourceServiceImpl.
 * <p>
 * Cobreix:
 *  - additionalSpringFilter
 *  - beforeCreateSave
 *  - beforeUpdateSave
 *  - InitOnChangeLogicProcessor
 *  - InteressatTipusOnChangeLogicProcessor
 */
class PersonaResourceServiceImplTest {

	private PersonaResourceServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PersonaResourceServiceImpl();
		service.init();
	}

	// -----------------------------------
	// additionalSpringFilter
	// -----------------------------------
	@Test
	void additionalSpringFilterShouldReturnIdIsNull() {
		String result = service.additionalSpringFilter(null, null);
		assertEquals("id is null", result);
	}

	// -----------------------------------
	// beforeCreateSave
	// -----------------------------------
	@Test
	void beforeCreateSaveShouldThrowException() {
		assertThrows(ResourceNotCreatedException.class, () ->
			service.beforeCreateSave(
				new PersonaResourceEntity(),
				new PersonaResource(),
				Map.of()
			)
		);
	}

	// -----------------------------------
	// beforeUpdateSave
	// -----------------------------------
	@Test
	void beforeUpdateSaveShouldThrowException() {
		PersonaResource resource = new PersonaResource();
		resource.setId(1L);
		assertThrows(ResourceNotUpdatedException.class, () ->
			service.beforeUpdateSave(
				new PersonaResourceEntity(),
				resource,
				Map.of()
			)
		);
	}

	// -----------------------------------
	// InteressatTipusOnChangeLogicProcessor
	// -----------------------------------
	@Test
	void interessatTipusOnChangeFisica() {
		PersonaResource target = new PersonaResource();
		var processor = new PersonaResourceServiceImpl.InteressatTipusOnChangeLogicProcessor();
		processor.onChange(null, null, "interessatTipus",
			InteressatTipus.FISICA,
			Map.of(),
			new String[]{},
			target);
		assertTrue(target.isVisibleNif());
		assertTrue(target.isVisibleNom());
		assertFalse(target.isVisibleRaoSocial());
		assertTrue(target.isRequiredNom());
		assertNull(target.getRaoSocial());
	}

	@Test
	void interessatTipusOnChangeAdministracio() {
		PersonaResource target = new PersonaResource();
		var processor = new PersonaResourceServiceImpl.InteressatTipusOnChangeLogicProcessor();
		processor.onChange(null, null, "interessatTipus",
			InteressatTipus.ADMINISTRACIO,
			Map.of(),
			new String[]{},
			target);
		assertTrue(target.isVisibleDir3Codi());
		assertTrue(target.isRequiredDir3Codi());
		assertFalse(target.isVisibleNom());
		assertNull(target.getNom());
	}

	@Test
	void interessatTipusOnChangeJuridica() {
		PersonaResource target = new PersonaResource();
		var processor = new PersonaResourceServiceImpl.InteressatTipusOnChangeLogicProcessor();
		processor.onChange(null, null, "interessatTipus",
			InteressatTipus.JURIDICA,
			Map.of(),
			new String[]{},
			target);
		assertTrue(target.isVisibleRaoSocial());
		assertTrue(target.isRequiredRaoSocial());
		assertFalse(target.isVisibleNom());
		assertNull(target.getNom());
	}

	@Test
	void interessatTipusOnChangeFisicaSenseNif() {
		PersonaResource target = new PersonaResource();
		var processor = new PersonaResourceServiceImpl.InteressatTipusOnChangeLogicProcessor();
		processor.onChange(null, null, "interessatTipus",
			InteressatTipus.FISICA_SENSE_NIF,
			Map.of(),
			new String[]{},
			target);
		assertTrue(target.isVisibleDocumentTipus());
		assertFalse(target.isVisibleTelefon());
		assertTrue(target.isRequiredEmail());
		assertNull(target.getTelefon());
	}

	// -----------------------------------
	// InitOnChangeLogicProcessor
	// -----------------------------------
	@Test
	void initOnChangeShouldApplyPreviousInteressatTipus() {
		PersonaResource previous = new PersonaResource();
		previous.setInteressatTipus(InteressatTipus.FISICA);
		PersonaResource target = new PersonaResource();
		var processor = new PersonaResourceServiceImpl.InitOnChangeLogicProcessor();
		processor.onChange(null, previous, null, null, Map.of(), new String[]{}, target);
		assertTrue(target.isVisibleNom());
		assertTrue(target.isRequiredNom());
	}

}
