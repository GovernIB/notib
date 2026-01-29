package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.AplicacioResource;
import es.caib.notib.logic.intf.resourceservice.AplicacioResourceService;
import es.caib.notib.persist.resourceentity.AplicacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió d'aplicacions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AplicacioResourceServiceImpl extends BaseMutableResourceService<AplicacioResource, Long, AplicacioResourceEntity> implements AplicacioResourceService {

	private final EntitatPermissionHelper entitatPermissionHelper;

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return entitatPermissionHelper.additionalSpringFilter("entitat.id");
	}

	@Override
	protected void beforeCreateEntity(
		AplicacioResourceEntity entity,
		AplicacioResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			null,
			resource.getEntitat().getId(),
			BasePermission.CREATE);
	}

	@Override
	protected void beforeUpdateEntity(
		AplicacioResourceEntity entity,
		AplicacioResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			resource.getEntitat().getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		AplicacioResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getEntitat().getId(),
			BasePermission.DELETE);
	}

}
