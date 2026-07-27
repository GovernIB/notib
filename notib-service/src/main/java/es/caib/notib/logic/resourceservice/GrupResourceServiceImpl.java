package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.GrupResource;
import es.caib.notib.logic.intf.model.ProcedimentGrupResource;
import es.caib.notib.logic.intf.resourceservice.GrupResourceService;
import es.caib.notib.persist.resourceentity.GrupResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentGrupResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de grups.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class GrupResourceServiceImpl extends BaseAdminEntitatResourceServiceImpl<GrupResource, GrupResourceEntity> implements GrupResourceService {

	public GrupResourceServiceImpl(UserSessionHelper userSessionHelper, AuthenticationHelper authenticationHelper, NotibPermissionHelper notibPermissionHelper) {
		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var superFilter = super.additionalSpringFilter(currentSpringFilter, namedQueries);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			var currentOrganGestorId = userSessionHelper.getCurrentOrganGestorId();
			superFilter += " and organGestor.id: " + currentOrganGestorId;
		}
		return superFilter;
	}

	@Override
	protected void beforeCreateSave(GrupResourceEntity entity, GrupResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		super.beforeCreateSave(entity, resource, answers);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (!isRoleAdminOrgan) {
			return;
		}
		var currentOrganGestor = userSessionHelper.getCurrentOrganGestor();
		entity.setOrganGestor(currentOrganGestor);
	}

}
