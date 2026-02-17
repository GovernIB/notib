package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PersonaResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió de persones.
 * Aquest servei REST només s'ha implementat perquè feia falta per a poder consultar els fields d'aquest recurs al
 * formulari del front.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/persones")
public class PersonaResourceController extends BaseMutableResourceController<PersonaResource, Long> {

}
