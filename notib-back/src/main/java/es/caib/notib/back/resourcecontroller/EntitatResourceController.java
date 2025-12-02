package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.EntitatResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/entitats")
public class EntitatResourceController extends BaseMutableResourceController<EntitatResource, Long> {

}
