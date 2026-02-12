package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorCieFormatSobreResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió de formats de sobre de pagadors CIE.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/pagadorsCieFormatsSobre")
public class PagadorCieFormatSobreResourceController extends BaseMutableResourceController<PagadorCieFormatSobreResource, Long> {

}
