package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseReadonlyResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.Dir3Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta d'unitats organitzatives directament a DIR3.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/dir3Uos")
public class Dir3ResourceController extends BaseReadonlyResourceController<Dir3Resource, String> {

}
