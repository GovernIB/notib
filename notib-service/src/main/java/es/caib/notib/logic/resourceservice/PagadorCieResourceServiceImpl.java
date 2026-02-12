package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.GrupResource;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.logic.intf.resourceservice.GrupResourceService;
import es.caib.notib.logic.intf.resourceservice.PagadorCieResourceService;
import es.caib.notib.persist.resourceentity.GrupResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.PagadorCieResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de pagadors CIE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PagadorCieResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<PagadorCieResource, PagadorCieResourceEntity>
	implements PagadorCieResourceService {

	public PagadorCieResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
	}

}
