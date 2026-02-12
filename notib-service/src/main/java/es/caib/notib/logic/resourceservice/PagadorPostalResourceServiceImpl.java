package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import es.caib.notib.logic.intf.resourceservice.PagadorPostalResourceService;
import es.caib.notib.persist.resourceentity.PagadorPostalResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de pagadors postals.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PagadorPostalResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<PagadorPostalResource, PagadorPostalResourceEntity>
	implements PagadorPostalResourceService {

	public PagadorPostalResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
	}

}
