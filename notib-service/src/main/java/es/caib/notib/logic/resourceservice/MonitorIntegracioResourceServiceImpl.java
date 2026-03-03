package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.MonitorIntegracioResource;
import es.caib.notib.logic.intf.resourceservice.MonitorIntegracioResourceService;
import es.caib.notib.persist.resourceentity.MonitorIntegracioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de monitor d'integracions
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorIntegracioResourceServiceImpl extends BaseMutableResourceService<MonitorIntegracioResource, Long, MonitorIntegracioResourceEntity> implements MonitorIntegracioResourceService {

}
