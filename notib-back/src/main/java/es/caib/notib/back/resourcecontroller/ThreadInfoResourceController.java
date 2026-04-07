package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ThreadInfoResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/threadInfo")
@Tag(name = "Threads", description = "Monitorització de sistema i fils de execució (super-admin)")
public class ThreadInfoResourceController extends BaseMutableResourceController<ThreadInfoResource, Long> {

}
