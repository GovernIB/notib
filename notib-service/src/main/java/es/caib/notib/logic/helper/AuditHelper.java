package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.auditoria.NotificacioAudit;
import es.caib.notib.persist.entity.auditoria.NotificacioEnviamentAudit;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioEnviamentAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditHelper {

    // Dependències
    private final NotificacioAuditRepository notificacioAuditRepository;
    private final NotificacioEventRepository notificacioEventRepository;
    private final NotificacioEnviamentAuditRepository notificacioEnviamentAuditRepository;

    public void auditaNotificacio(NotificacioEntity notificacio, AuditService.TipusOperacio tipusOperacio, String metode) {

//        var lastErrorEvent = NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat()) ? null : notificacioEventRepository.findLastErrorEventByNotificacioId(notificacio.getId());
        var enviaments = notificacio.getEnviaments();
        var lastErrorEvent = NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat()) ? null
                : enviaments != null && !enviaments.isEmpty() ? enviaments.iterator().next().getNotificacioErrorEvent() : null;
        var audit = new NotificacioAudit(notificacio, lastErrorEvent, tipusOperacio, metode);
        var lastAudit = notificacioAuditRepository.findLastAudit(notificacio.getId());
        if (lastAudit == null || !tipusOperacio.equals(lastAudit.getTipusOperacio()) || !audit.equals(lastAudit)) {
            notificacioAuditRepository.saveAndFlush(audit);
        } else {
            audit = null;
        }
    }

    public void auditaEnviament(NotificacioEnviamentEntity enviament, AuditService.TipusOperacio tipusOperacio, String metode) {

        var audit = NotificacioEnviamentAudit.getBuilder(enviament, tipusOperacio, metode).build();
        var lastAudit = notificacioEnviamentAuditRepository.findLastAudit(enviament.getId());
        if (lastAudit == null || !tipusOperacio.equals(lastAudit.getTipusOperacio()) || !audit.equals(lastAudit)) {
            notificacioEnviamentAuditRepository.saveAndFlush(audit);
        } else {
            audit = null;
        }
    }

}
