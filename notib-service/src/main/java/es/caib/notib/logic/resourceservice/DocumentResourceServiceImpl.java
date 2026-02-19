package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.resourceservice.DocumentResourceService;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de documents adjunts d'una notificació.
 * Aquest servei només s'ha implementat perquè feia falta per a poder consultar els fields d'aquest recurs al formulari
 * del front.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class DocumentResourceServiceImpl
	extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity>
	implements DocumentResourceService {

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que no es retorni mai cap
	 * resultat.
	 */
	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return "id is null";
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que si s'intenta crear un
	 * recurs llençam una excepció.
	 */
	@Override
	protected void beforeCreateSave(
		DocumentResourceEntity entity,
		DocumentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(getResourceClass(), "Create is not allowed");
	}

}
