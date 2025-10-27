package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.EnviamentResource;
import es.caib.notib.logic.intf.resourceservice.EnviamentResourceService;
import es.caib.notib.persist.entity.resourceentity.EnviamentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei d'enviaments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnviamentResourceServiceImpl extends BaseMutableResourceService<EnviamentResource, Long, EnviamentResourceEntity> implements EnviamentResourceService {

}
