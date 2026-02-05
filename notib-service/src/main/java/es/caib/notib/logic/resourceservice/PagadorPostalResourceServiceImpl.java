package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import es.caib.notib.logic.intf.resourceservice.PagadorPostalResourceService;
import es.caib.notib.persist.resourceentity.PagadorPostalResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Implementació del servei de gestió de pagadors postals.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PagadorPostalResourceServiceImpl extends BaseMutableResourceService<PagadorPostalResource, Long, PagadorPostalResourceEntity> implements PagadorPostalResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;
	private final AclHelper aclHelper;
	private final EntitatPermissionHelper entitatPermissionHelper;

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (isRoleSuper) {
			return null;
		}
		var currentEntitatId = userSessionHelper.getCurrentEntitatId();
		return currentEntitatId != null ? "entitat.id:" + currentEntitatId : "entitat.id is null";
	}

}
