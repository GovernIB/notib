package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.AplicacioResource;
import es.caib.notib.logic.intf.resourceservice.AplicacioResourceService;
import es.caib.notib.persist.resourceentity.AplicacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'aplicacions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AplicacioResourceServiceImpl extends BaseMutableResourceService<AplicacioResource, Long, AplicacioResourceEntity> implements AplicacioResourceService {

}
