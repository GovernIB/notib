package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentResourceService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió d'enviaments d'una notificació.
 * Aquest servei només s'ha implementat perquè feia falta per a poder consultar els fields d'aquest recurs al formulari
 * del front.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class NotificacioEnviamentResourceServiceImpl
	extends BaseMutableResourceService<NotificacioEnviamentResource, Long, NotificacioEnviamentResourceEntity>
	implements NotificacioEnviamentResourceService {

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que no es retorni mai cap
	 * resultat.
	 */
	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		//return "id is null";
		return null;
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que si s'intenta crear un
	 * recurs llençam una excepció.
	 */
	@Override
	protected void beforeCreateSave(
		NotificacioEnviamentResourceEntity entity,
		NotificacioEnviamentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(getResourceClass(), "Create is not allowed");
	}

	@Override
	protected void afterConversion(
		NotificacioEnviamentResourceEntity entity,
		NotificacioEnviamentResource resource) {
		OrganGestorResourceEntity organGestor = entity.getNotificacio().getOrganGestor();
		ProcedimentResourceEntity procediment = entity.getNotificacio().getProcediment();
		resource.setNotificacioOrganGestor(ResourceReference.toResourceReference(
			organGestor.getId(),
			organGestor.getCodiNom()));
		resource.setNotificacioProcediment(
			ResourceReference.toResourceReference(
				procediment.getId(),
				procediment.getNom())
		);
	}

}
