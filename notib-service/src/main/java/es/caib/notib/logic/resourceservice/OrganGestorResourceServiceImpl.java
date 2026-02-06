package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.helper.OrganGestorSyncHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

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
	private final OrganGestorSyncHelper organGestorSyncHelper;
	private final EntitatResourceRepository entitatResourceRepository;

	@PostConstruct
	public void init() {
		register(OrganGestorResource.DIR3_SYNC_ACTION_CODE, new Dir3SyncActionExecutor());
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (isRoleSuper) {
			return null;
		}
		var currentEntitatId = userSessionHelper.getCurrentEntitatId();
		return currentEntitatId != null ? "entitat.id:" + currentEntitatId : "entitat.id is null";
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

	@Component
	@RequiredArgsConstructor
	public class Dir3SyncActionExecutor implements ActionExecutor<OrganGestorResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, OrganGestorDir3Sync> {
		@Override
		public OrganGestorDir3Sync exec(String code, OrganGestorResourceEntity entity, OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {
			Optional<EntitatResourceEntity> entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
			if (entitat.isPresent()) {
				return organGestorSyncHelper.sincronitzar(
					entitat.get(),
					params.getSimular() != null && params.getSimular());
			} else {
				throw new ActionExecutionException(
					OrganGestorResource.class,
					null,
					code,
					"Couldn't find current entitat in user session");
			}
		}
		@Override
		public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
		}
	}

}
