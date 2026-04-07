package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.BackGroundTaskResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/tasks")
@Tag(name = "Tasques en segon plà", description = "Monitorització de tasques en segon plà (super-admin)")
public class BackGroundTaskResourceController extends BaseMutableResourceController<BackGroundTaskResource, String> {}
