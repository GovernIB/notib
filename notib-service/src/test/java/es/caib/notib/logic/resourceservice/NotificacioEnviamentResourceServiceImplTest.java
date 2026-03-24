package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaris per NotificacioEnviamentResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class NotificacioEnviamentResourceServiceImplTest {

	@InjectMocks
	private NotificacioEnviamentResourceServiceImpl service;

	@Test
	void beforeCreateSaveShouldThrowException() {
		NotificacioEnviamentResourceEntity entity = new NotificacioEnviamentResourceEntity();
		NotificacioEnviamentResource resource = new NotificacioEnviamentResource();
		Map<String, AnswerRequiredException.AnswerValue> answers = Map.of();
		ResourceNotCreatedException exception = assertThrows(ResourceNotCreatedException.class, () -> {
			service.beforeCreateSave(entity, resource, answers);
		});
		assertEquals(ResourceNotCreatedException.class, exception.getClass());
		assertEquals("Create is not allowed", exception.getReason());
	}

	@Test
	void afterConversionShouldPopulateResourceReferences() {
		// Given
		OrganGestorResourceEntity organGestor = new OrganGestorResourceEntity();
		organGestor.setId(1L);
		organGestor.setCodiNom("ORG-123");
		ProcedimentResourceEntity procediment = new ProcedimentResourceEntity();
		procediment.setId(2L);
		procediment.setNom("Proc A");
		NotificacioResourceEntity notificacio = new NotificacioResourceEntity();
		notificacio.setOrganGestor(organGestor);
		notificacio.setProcediment(procediment);
		NotificacioEnviamentResourceEntity enviamentEntity = new NotificacioEnviamentResourceEntity();
		enviamentEntity.setNotificacio(notificacio);
		NotificacioEnviamentResource resource = new NotificacioEnviamentResource();
		// When
		service.afterConversion(enviamentEntity, resource);
		// Then
		assertNotNull(resource.getNotificacioOrganGestor());
		assertEquals(1L, resource.getNotificacioOrganGestor().getId());
		assertEquals("ORG-123", resource.getNotificacioOrganGestor().getDescription());
		assertNotNull(resource.getNotificacioProcediment());
		assertEquals(2L, resource.getNotificacioProcediment().getId());
		assertEquals("Proc A", resource.getNotificacioProcediment().getDescription());
	}

}
