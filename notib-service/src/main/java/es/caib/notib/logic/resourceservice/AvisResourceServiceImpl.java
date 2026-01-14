package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.resourceservice.AvisResourceService;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'avisos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvisResourceServiceImpl extends BaseMutableResourceService<AvisResource, Long, AvisResourceEntity> implements AvisResourceService {

}
