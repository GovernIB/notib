package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.NotificacioEnviamentAuditResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió d'enviaments d'una notificació.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/historicEnviaments")
public class NotificacioEnviamentAuditResourceController extends BaseMutableResourceController<NotificacioEnviamentAuditResource, Long> {

}
