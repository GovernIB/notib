package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.EntitatTipusDocumentResource;
import es.caib.notib.logic.intf.resourceservice.EntitatTipusDocumentResourceService;
import es.caib.notib.persist.resourceentity.EntitatTipusDocumentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de tipus de documents associats a una entitat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatTipusDocumentResourceServiceImpl extends BaseMutableResourceService<EntitatTipusDocumentResource, Long, EntitatTipusDocumentResourceEntity> implements EntitatTipusDocumentResourceService {

}
