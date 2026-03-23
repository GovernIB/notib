package es.caib.notib.back.resourcecontroller;

import es.caib.notib.back.base.controller.BaseMutableResourceController;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ActiveMqResource;
import es.caib.notib.logic.intf.model.CacheResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta de l'ActiveMQ
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/activemq")
public class ActiveMqResourceController extends BaseMutableResourceController<ActiveMqResource, String> {

	@Override
	public ResponseEntity<CacheResource> delete(String id) {

		return null;
	}
}
