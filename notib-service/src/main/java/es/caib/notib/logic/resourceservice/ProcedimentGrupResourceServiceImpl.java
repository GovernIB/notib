package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.model.GrupResource;
import es.caib.notib.logic.intf.model.ProcedimentGrupResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentGrupResourceService;
import es.caib.notib.persist.resourceentity.ProcedimentGrupResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de relacions procediment - grup.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcedimentGrupResourceServiceImpl extends BaseMutableResourceService<ProcedimentGrupResource, Long, ProcedimentGrupResourceEntity> implements ProcedimentGrupResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;
	private final EntitatPermissionHelper entitatPermissionHelper;

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
			if (currentEntitatId != null) {
				return "procediment.entitat.id:" + currentEntitatId;
			} else {
				return "procediment.entitat.id is null";
			}
		} else {
			return null;
		}
	}

	@Override
	protected void beforeCreateSave(
		ProcedimentGrupResourceEntity entity,
		ProcedimentGrupResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
		if (!entity.getProcediment().getEntitat().getId().equals(currentEntitatId)) {
			throw new ResourceNotCreatedException(
				GrupResource.class,
				"Couldn't create because procediment entitat does not coincide with the entitat in session");
		}
		if (!entity.getGrup().getEntitat().getId().equals(currentEntitatId)) {
			throw new ResourceNotCreatedException(
				GrupResource.class,
				"Couldn't create because grup entitat does not coincide with the entitat in session");
		}
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			entity.getProcediment().getEntitat().getId(),
			BasePermission.CREATE);
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			entity.getGrup().getEntitat().getId(),
			BasePermission.CREATE);
	}

	@Override
	protected void beforeUpdateEntity(
		ProcedimentGrupResourceEntity entity,
		ProcedimentGrupResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			entity.getProcediment().getEntitat().getId(),
			BasePermission.WRITE);
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			entity.getGrup().getEntitat().getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		ProcedimentGrupResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getProcediment().getEntitat().getId(),
			BasePermission.DELETE);
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getGrup().getEntitat().getId(),
			BasePermission.DELETE);
	}

}
