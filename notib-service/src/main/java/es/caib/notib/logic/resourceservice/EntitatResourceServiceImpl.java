package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity> implements EntitatResourceService {

}
