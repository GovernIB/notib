package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.EventResource;
import es.caib.notib.logic.intf.resourceservice.EventResourceService;
import es.caib.notib.persist.resourceentity.EventResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'events.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventResourceServiceImpl extends BaseMutableResourceService<EventResource, Long, EventResourceEntity> implements EventResourceService {


}
