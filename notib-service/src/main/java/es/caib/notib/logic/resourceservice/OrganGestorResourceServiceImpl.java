package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

/**
 * Implementació del servei de gestió d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganGestorResourceServiceImpl extends BaseMutableResourceService<OrganGestorResource, Long, OrganGestorResourceEntity> implements OrganGestorResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;
	private final EntitatPermissionHelper entitatPermissionHelper;

	@PostConstruct
	public void init() {
		register(OrganGestorResource.DIR3_SYNC_ACTION_CODE, new Dir3SyncActionExecutor());
	}

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
		if (currentEntitatId != null) {
			return "entitat.id:" + currentEntitatId;
		} else {
			return "entitat.id is null";
		}
	}

	@Override
	protected void beforeCreateSave(
		OrganGestorResourceEntity entity,
		OrganGestorResource resource,
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
		OrganGestorResourceEntity entity,
		OrganGestorResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			entity.getEntitat().getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		OrganGestorResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getEntitat().getId(),
			BasePermission.DELETE);
	}

	public static class Dir3SyncActionExecutor implements ActionExecutor<OrganGestorResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, OrganGestorResource.OrganGestorDir3SyncResult> {
		@Override
		public OrganGestorResource.OrganGestorDir3SyncResult exec(
			String code,
			OrganGestorResourceEntity entity,
			OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {
			boolean real = params.getReal() != null ? params.getReal() : false;
			if (real) {
				System.out.println(">>> " + code + " real");
				return new OrganGestorResource.OrganGestorDir3SyncResult(0, 1, 2, 3, false);
			} else {
				System.out.println(">>> " + code + " simulat");
				return new OrganGestorResource.OrganGestorDir3SyncResult(0, 1, 2, 3, true);
			}
		}
		@Override
		public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
		}
	}

}
