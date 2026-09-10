package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.EntregaCieResource;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/entregaPostal")
public class EntregaPostalResourceController extends BaseMutableResourceController<EntregaPostalResource, Long> {

}
