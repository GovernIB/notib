package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.NotificacioAuditResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioAuditResourceService;
import es.caib.notib.persist.resourceentity.NotificacioAuditResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei d'auditoria d'una notificacio
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacioAuditResourceServiceImpl extends BaseMutableResourceService<NotificacioAuditResource, Long, NotificacioAuditResourceEntity> implements NotificacioAuditResourceService {

}
