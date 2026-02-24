package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.MonitorIntegracioParamResource;
import es.caib.notib.logic.intf.resourceservice.MonitorIntegracioParamResourceService;
import es.caib.notib.persist.resourceentity.MonitorIntegracioParamResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de monitor d'integracions paràmetres
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorIntegracioParamResourceServiceImpl extends BaseMutableResourceService<MonitorIntegracioParamResource, Long, MonitorIntegracioParamResourceEntity> implements MonitorIntegracioParamResourceService {


}
