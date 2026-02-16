package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioResourceService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de gestió de notificacions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class NotificacioResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<NotificacioResource, NotificacioResourceEntity>
	implements NotificacioResourceService {

	private final NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository;
	private final PersonaResourceRepository personaResourceRepository;

	public NotificacioResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper,
		NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository,
		PersonaResourceRepository personaResourceRepository) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
		this.notificacioEnviamentResourceRepository = notificacioEnviamentResourceRepository;
		this.personaResourceRepository = personaResourceRepository;
	}

	@Override
	public void afterCreateSave(
		NotificacioResourceEntity entity,
		NotificacioResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers,
		boolean anyOrderChanged) {
		if (resource.getEnviaments() != null) {
			saveEnviaments(entity, resource.getEnviaments());
		}
	}

	private void saveEnviaments(
		NotificacioResourceEntity notificacio,
		List<NotificacioEnviamentResource> enviaments) {
		// Crea els enviaments associats amb la notificació a la base de dades.
		enviaments.forEach(e -> {
			NotificacioEnviamentResourceEntity enviament = notificacioEnviamentResourceRepository.save(
				NotificacioEnviamentResourceEntity.builder().
					resource(e).
					notificacio(notificacio).
					build());
			PersonaResourceEntity titular = saveDestinatari(enviament, e.getTitularInfo());
			enviament.setTitular(titular);
			if (e.getRepresentantsInfo() != null) {
				e.getRepresentantsInfo().forEach(r -> saveDestinatari(enviament, r));
			}
		});
	}

	private PersonaResourceEntity saveDestinatari(
		NotificacioEnviamentResourceEntity enviament,
		PersonaResource destinatari) {
		// Crea el destinatari a la base de dades.
		return personaResourceRepository.save(
			PersonaResourceEntity.builder().
				resource(destinatari).
				enviament(enviament).
				build());
	}

}
