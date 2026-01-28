package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.GrupResource;
import es.caib.notib.logic.intf.resourceservice.GrupResourceService;
import es.caib.notib.persist.resourceentity.GrupResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'avisos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrupResourceServiceImpl extends BaseMutableResourceService<GrupResource, Long, GrupResourceEntity> implements GrupResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;

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

}
