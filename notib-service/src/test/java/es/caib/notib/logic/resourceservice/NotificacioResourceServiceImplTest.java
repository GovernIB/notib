package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import es.caib.notib.persist.resourcerepository.DocumentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per NotificacioResourceServiceImpl.
 * <p>
 * Cobertura:
 *  - beforeCreateSave
 *  - afterCreateSave
 *  - saveDocuments
 *  - saveEnviaments (parcial)
 *  - onChange caducitat
 */
@ExtendWith(MockitoExtension.class)
class NotificacioResourceServiceImplTest {

	@Mock private UserSessionHelper userSessionHelper;
	@Mock private AuthenticationHelper authenticationHelper;
	@Mock private NotificacioEnviamentResourceRepository enviamentRepo;
	@Mock private DocumentResourceRepository documentRepo;
	@Mock private PersonaResourceRepository personaRepo;
	@Mock private LegacyHelper legacyHelper;
	@Mock private NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository;

	@InjectMocks
	private NotificacioResourceServiceImpl service;

	private NotificacioResourceEntity entity;
	private NotificacioResource resource;

	@BeforeEach
	void setUp() {
		entity = new NotificacioResourceEntity();
		resource = new NotificacioResource();
	}

	// =====================================================
	// beforeCreateSave
	// =====================================================

	@Test
	void beforeCreateSaveShouldSetBasicFields() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		EntitatResourceEntity entitat = new EntitatResourceEntity();
		entitat.setDir3Codi("DIR3");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(entitat);
		service.beforeCreateSave(entity, resource, Map.of());
		assertEquals("user", entity.getUsuariCodi());
		assertEquals(entitat, entity.getEntitat());
		assertEquals("DIR3", entity.getEmisorDir3Codi());
		assertNotNull(entity.getReferencia());
		assertEquals(NotificacioEstatEnumDto.PENDENT, entity.getEstat());
	}

	@Test
	void beforeCreateSaveShouldSaveDocuments() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user");
		when(userSessionHelper.getCurrentEntitat()).thenReturn(new EntitatResourceEntity());
		DocumentResource doc = new DocumentResource();
		doc.setAttachment(
			new FileReference(
				"document.txt",
				new byte[] { 1 },
				"text/plain",
				1));
		resource.setDocumentsInfo(List.of(doc));
		when(legacyHelper.notificacioAdjuntCreate(any())).thenReturn("fileId");
		when(documentRepo.save(any())).thenAnswer(i -> i.getArgument(0));
		service.beforeCreateSave(entity, resource, Map.of());
		assertNotNull(entity.getDocument());
	}

	// =====================================================
	// afterCreateSave
	// =====================================================

	@Test
	void afterCreateSaveShouldSaveEnviaments() {
		NotificacioEnviamentResource enviament = new NotificacioEnviamentResource();
		enviament.setTitularInfo(new PersonaResource());
		resource.setEnviamentsInfo(List.of(enviament));
		NotificacioEnviamentResourceEntity saved = new NotificacioEnviamentResourceEntity();
		when(enviamentRepo.save(any())).thenReturn(saved);
		when(personaRepo.save(any())).thenReturn(new PersonaResourceEntity());
		service.afterCreateSave(entity, resource, Map.of(), false);
		verify(enviamentRepo).save(any());
	}

	@Test
	void afterCreateSaveShouldCallLegacyHelper() {
		NotificacioResourceEntity entity = new NotificacioResourceEntity();
		NotificacioResource resource = new NotificacioResource();
		Map<String, AnswerRequiredException.AnswerValue> answers = Map.of();
		// Afegim enviaments ficticis per veure que saveEnviaments també es crida
		NotificacioEnviamentResource enviament = new NotificacioEnviamentResource();
		PersonaResource titular = new PersonaResource();
		enviament.setTitularInfo(titular);
		resource.setEnviamentsInfo(List.of(enviament));
		NotificacioEnviamentResourceEntity saved = new NotificacioEnviamentResourceEntity();
		when(enviamentRepo.save(any())).thenReturn(saved);
		service.afterCreateSave(entity, resource, answers, false);
		// Comprovem que s’ha cridat el legacyHelper amb la mateixa entitat
		verify(legacyHelper).altaNotificacio(entity);
	}

	// =====================================================
	// onChange (caducitat)
	// =====================================================

	@Test
	void caducitatOnChangeShouldCalculateDays() {
		NotificacioResource previous = new NotificacioResource();
		NotificacioResource target = new NotificacioResource();
		Date futureDate = Date.from(
			java.time.LocalDate.now().plusDays(5)
				.atStartOfDay(java.time.ZoneId.systemDefault())
				.toInstant()
		);
		// cridam el mètode via processor
		var processor = new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor();
		processor.onChange(
			null,
			previous,
			NotificacioResource.Fields.caducitat,
			futureDate,
			Map.of(),
			new String[]{},
			target
		);
		assertNotNull(target.getCaducitatDiesNaturals());
	}

	@Test
	void caducitatOnChangeShouldCalculateDate() {
		NotificacioResource previous = new NotificacioResource();
		NotificacioResource target = new NotificacioResource();
		var processor = new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor();
		processor.onChange(
			null,
			previous,
			NotificacioResource.Fields.caducitatDiesNaturals,
			5,
			Map.of(),
			new String[]{},
			target
		);
		assertNotNull(target.getCaducitat());
	}

}
