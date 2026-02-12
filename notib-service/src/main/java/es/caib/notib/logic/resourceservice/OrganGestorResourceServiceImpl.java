package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.helper.OrganGestorSyncHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import lombok.extern.slf4j.Slf4j;
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
public class OrganGestorResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<OrganGestorResource, OrganGestorResourceEntity>
	implements OrganGestorResourceService {

	private final AclHelper aclHelper;
	private final OrganGestorSyncHelper organGestorSyncHelper;
	private final EntitatResourceRepository entitatResourceRepository;

	public OrganGestorResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper,
		AclHelper aclHelper,
		OrganGestorSyncHelper organGestorSyncHelper,
		EntitatResourceRepository entitatResourceRepository) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
		this.aclHelper = aclHelper;
		this.organGestorSyncHelper = organGestorSyncHelper;
		this.entitatResourceRepository = entitatResourceRepository;
	}

	@PostConstruct
	public void init() {
		register(OrganGestorResource.DIR3_SYNC_ACTION_CODE, new Dir3SyncActionExecutor());
	}

	@Override
	protected void afterConversion(OrganGestorResourceEntity entity, OrganGestorResource resource) {
		resource.setAclEntryCount(
			aclHelper.count(AclHelper.ENTITAT_CLASS, entity.getId(), null));
	}

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
