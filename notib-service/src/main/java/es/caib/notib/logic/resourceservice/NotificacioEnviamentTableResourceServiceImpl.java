package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.NotificacioEnviamentTableResource;
import es.caib.notib.logic.intf.model.NotificacioTableResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentTableResourceService;
import es.caib.notib.logic.intf.resourceservice.NotificacioTableResourceService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentTableResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioTableResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de la taula optimitzada d'enviaments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class NotificacioEnviamentTableResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<NotificacioEnviamentTableResource, NotificacioEnviamentTableResourceEntity>
	implements NotificacioEnviamentTableResourceService {

	public NotificacioEnviamentTableResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
	}

}
