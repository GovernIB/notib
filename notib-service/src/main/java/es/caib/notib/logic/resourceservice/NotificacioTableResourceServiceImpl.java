package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.NotificacioTableResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioTableResourceService;
import es.caib.notib.persist.resourceentity.NotificacioTableResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de la taula optimitzada de notificacions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class NotificacioTableResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<NotificacioTableResource, NotificacioTableResourceEntity>
	implements NotificacioTableResourceService {

	public NotificacioTableResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
	}

}
