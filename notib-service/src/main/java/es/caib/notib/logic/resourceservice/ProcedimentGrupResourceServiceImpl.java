package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.model.ProcedimentGrupResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentGrupResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentGrupResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Implementació del servei de gestió de relacions procediment - grup.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ProcedimentGrupResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<ProcedimentGrupResource, ProcedimentGrupResourceEntity>
	implements ProcedimentGrupResourceService {

	public ProcedimentGrupResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
	}

	@Override
	protected void beforeCreateSave(
		ProcedimentGrupResourceEntity entity,
		ProcedimentGrupResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		// Crida el mètode beforeCreateSave de BaseAdminEntitatResourceServiceImpl que comprova l'entitat del
		// procediment.
		entity.setEntitat(entity.getProcediment().getEntitat());
		super.beforeCreateSave(entity, resource, answers);
		// Repeteix les mateixes comprovacions per a l'entitat del grup.
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		if (Objects.equals(entity.getGrup().getEntitat(), currentEntitat)) {
			entitatPermissionHelper.checkEntitatAdminPermission(
				getResourceClass(),
				null,
				entity.getGrup().getEntitat().getId(),
				BasePermission.CREATE);
		} else {
			throw new ResourceNotCreatedException(
				getResourceClass(),
				"Not allowed to create a " + getResourceClass() + " belonging to a different entitat than the one " +
					"selected in the session (sessionEntitatId=" + currentEntitat.getId() + ")");
		}
	}

	@Override
	protected void beforeUpdateEntity(
		ProcedimentGrupResourceEntity entity,
		ProcedimentGrupResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		// Crida el mètode beforeUpdateEntity de BaseAdminEntitatResourceServiceImpl que comprova l'entitat del
		// procediment.
		super.beforeUpdateEntity(entity, resource, answers);
		// Repeteix les mateixes comprovacions per a l'entitat del grup.
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		if (Objects.equals(entity.getGrup().getEntitat(), currentEntitat)) {
			entitatPermissionHelper.checkEntitatAdminPermission(
				getResourceClass(),
				entity.getId(),
				entity.getGrup().getEntitat().getId(),
				BasePermission.WRITE);
		} else {
			throw new ResourceNotUpdatedException(
				getResourceClass(),
				"" + entity.getId(),
				"Not allowed to update a " + getResourceClass() + " belonging to a different entitat than the one " +
					"selected in the session (sessionEntitatId=" + currentEntitat.getId() + ")");
		}
	}

	@Override
	protected void beforeDelete(
		ProcedimentGrupResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		// Crida el mètode beforeDelete de BaseAdminEntitatResourceServiceImpl que comprova l'entitat del procediment.
		super.beforeDelete(entity, answers);
		// Repeteix les mateixes comprovacions per a l'entitat del grup.
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		if (Objects.equals(entity.getGrup().getEntitat(), currentEntitat)) {
			entitatPermissionHelper.checkEntitatAdminPermission(
				getResourceClass(),
				entity.getId(),
				entity.getGrup().getEntitat().getId(),
				BasePermission.DELETE);
		} else {
			throw new ResourceNotDeletedException(
				getResourceClass(),
				"" + entity.getId(),
				"Not allowed to delete a " + getResourceClass() + " belonging to a different entitat than the one " +
					"selected in the session (sessionEntitatId=" + currentEntitat.getId() + ")");
		}
	}

}
