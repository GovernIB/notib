package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.PagadorCieFormatSobreResource;
import es.caib.notib.logic.intf.resourceservice.PagadorCieFormatSobreResourceService;
import es.caib.notib.persist.resourceentity.PagadorCieFormatSobreResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de formats de sobre de pagadors CIE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PagadorCieFormatSobreResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<PagadorCieFormatSobreResource, PagadorCieFormatSobreResourceEntity>
	implements PagadorCieFormatSobreResourceService {

	public PagadorCieFormatSobreResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
	}

}
