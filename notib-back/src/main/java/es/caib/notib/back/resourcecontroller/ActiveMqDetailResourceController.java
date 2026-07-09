package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ActiveMqDetailResource;
import es.caib.notib.logic.intf.model.ActiveMqResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta missatges d'una cua de l'ActiveMQ
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/activemqDetail")
public class ActiveMqDetailResourceController extends BaseMutableResourceController<ActiveMqDetailResource, String> {

}
