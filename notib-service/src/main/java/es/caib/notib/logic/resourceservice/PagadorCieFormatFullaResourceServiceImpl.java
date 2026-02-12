package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.PagadorCieFormatFullaResource;
import es.caib.notib.logic.intf.resourceservice.PagadorCieFormatFullaResourceService;
import es.caib.notib.persist.resourceentity.PagadorCieFormatFullaResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de formats de fulla de pagadors CIE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PagadorCieFormatFullaResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<PagadorCieFormatFullaResource, PagadorCieFormatFullaResourceEntity>
	implements PagadorCieFormatFullaResourceService {

	public PagadorCieFormatFullaResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
	}

}
