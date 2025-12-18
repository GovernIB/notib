package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.EntitatTipusDocumentResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió de tipus de documents associats a una entitat.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/entitatTipusDocuments")
public class EntitatTipusDocumentResourceController extends BaseMutableResourceController<EntitatTipusDocumentResource, Long> {

}
