package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.model.NotificacioEnviamentAuditResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentAuditResourceService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentAuditResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei d'auditoria d'enviaments d'una notificacio
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacioEnviamentAuditResourceServiceImpl extends BaseMutableResourceService<NotificacioEnviamentAuditResource, Long, NotificacioEnviamentAuditResourceEntity> implements NotificacioEnviamentAuditResourceService {

}
