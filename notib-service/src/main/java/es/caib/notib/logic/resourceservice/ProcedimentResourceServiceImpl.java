package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de procediments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcedimentResourceServiceImpl extends BaseMutableResourceService<ProcedimentResource, Long, ProcedimentResourceEntity> implements ProcedimentResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;
	private final AclHelper aclHelper;
	private final EntitatPermissionHelper entitatPermissionHelper;

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
			if (currentEntitatId != null) {
				return "entitat.id:" + currentEntitatId;
			} else {
				return "entitat.id is null";
			}
		} else {
			return null;
		}
	}

	@Override
	protected void beforeCreateSave(
		ProcedimentResourceEntity entity,
		ProcedimentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		entity.setEntitat(currentEntitat);
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			null,
			currentEntitat.getId(),
			BasePermission.CREATE);
	}

	@Override
	protected void beforeUpdateEntity(
		ProcedimentResourceEntity entity,
		ProcedimentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			entity.getEntitat().getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		ProcedimentResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getEntitat().getId(),
			BasePermission.DELETE);
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {
		resource.setAclEntryCount(
			aclHelper.count(AclHelper.PROCEDIMENT_CLASS, entity.getId(), null));
	}

}
