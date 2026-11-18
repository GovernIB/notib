package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.ExcepcioLogResource;
import es.caib.notib.logic.intf.resourceservice.ExcepcioLogResourceService;
import es.caib.notib.persist.resourceentity.ExcepcioLogResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'excepcions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseMutableResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogResourceService {


}
