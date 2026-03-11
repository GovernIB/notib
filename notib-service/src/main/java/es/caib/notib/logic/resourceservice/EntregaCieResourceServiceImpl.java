package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.EntregaCieResource;
import es.caib.notib.logic.intf.resourceservice.EntregaCieResourceService;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei d'entregues CIE
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class EntregaCieResourceServiceImpl extends BaseMutableResourceService<EntregaCieResource, Long, EntregaCieResourceEntity> implements EntregaCieResourceService {

}
