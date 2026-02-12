package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de procediments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ProcedimentResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<ProcedimentResource, ProcedimentResourceEntity>
	implements ProcedimentResourceService {

	private final AclHelper aclHelper;

	public ProcedimentResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper,
		AclHelper aclHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
		this.aclHelper = aclHelper;
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {
		resource.setAclEntryCount(
			aclHelper.count(AclHelper.PROCEDIMENT_CLASS, entity.getId(), null));
	}

}
