package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió de notificacions massives.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/notificacio/massiva")
public class NotificacioMassivaResourceController extends BaseMutableResourceController<NotificacioMassivaResource, Long> {

}
