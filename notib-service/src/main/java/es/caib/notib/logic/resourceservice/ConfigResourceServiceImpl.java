package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.ConfigResource;
import es.caib.notib.logic.intf.model.ConfigTypeResource;
import es.caib.notib.logic.intf.resourceservice.ConfigResourceService;
import es.caib.notib.logic.intf.resourceservice.ConfigTypeResourceService;
import es.caib.notib.persist.resourceentity.ConfigResourceEntity;
import es.caib.notib.persist.resourceentity.ConfigTypeResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de propietats de configuració.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigResourceServiceImpl extends BaseMutableResourceService<ConfigResource, Long, ConfigResourceEntity> implements ConfigResourceService {

}
