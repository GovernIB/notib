package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ServeiEntity;
import es.caib.notib.persist.entity.auditoria.AplicacioAudit;
import es.caib.notib.persist.entity.auditoria.EntitatAudit;
import es.caib.notib.persist.entity.auditoria.GrupAudit;
import es.caib.notib.persist.entity.auditoria.GrupProcedimentAudit;
import es.caib.notib.persist.entity.auditoria.NotificacioAudit;
import es.caib.notib.persist.entity.auditoria.NotificacioEnviamentAudit;
import es.caib.notib.persist.entity.auditoria.ProcedimentAudit;
import es.caib.notib.persist.repository.auditoria.AplicacioAuditRepository;
import es.caib.notib.persist.repository.auditoria.EntitatAuditRepository;
import es.caib.notib.persist.repository.auditoria.GrupAuditRepository;
import es.caib.notib.persist.repository.auditoria.GrupProcedimentAuditRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioEnviamentAuditRepository;
import es.caib.notib.persist.repository.auditoria.ProcedimentAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuditServiceImpl implements AuditService {

	@Autowired
	private AplicacioAuditRepository aplicacioAuditRepository;
	@Autowired
	private EntitatAuditRepository entitatAuditRepository;
	@Autowired
	private GrupAuditRepository grupAuditRepository;
	@Autowired
	private GrupProcedimentAuditRepository grupProcedimentAuditRepository;
	@Autowired
	private NotificacioAuditRepository notificacioAuditRepository;
	@Autowired
	private NotificacioEnviamentAuditRepository notificacioEnviamentAuditRepository;
	@Autowired
	private ProcedimentAuditRepository procedimentAuditRepository;
	@Autowired
	private NotificacioHelper notificacioHelper;

	@Override
	@Transactional(propagation = Propagation.REQUIRED) // A JBoss ha de ser Propagation.MANDATORY, a tomcat Propagation.REQUIRED
	public void audita(Object objecteAuditar, TipusOperacio tipusOperacio, TipusEntitat tipusEntitat, TipusObjecte tipusObjecte, String joinPoint) {

		switch (tipusEntitat) {
		case APLICACIO:
			auditaAplicacio(objecteAuditar, tipusOperacio, joinPoint);
			break;
		case ENTITAT:
			auditaEntitat(objecteAuditar, tipusOperacio, joinPoint);
			break;
		case GRUP:
			auditaGrup(objecteAuditar, tipusOperacio, joinPoint);
			break;
		case PROCEDIMENT:
			auditaProcediment(objecteAuditar, tipusOperacio, tipusObjecte, joinPoint);
			break;
		case SERVEI:
			auditaServei(objecteAuditar, tipusOperacio, tipusObjecte, joinPoint);
			break;
		case PROCEDIMENT_GRUP:
			auditaProcedimentGrup(objecteAuditar, tipusOperacio, joinPoint);
			break;
		case NOTIFICACIO:
			auditaNotificacio(objecteAuditar, tipusOperacio, joinPoint);
			break;
		case ENVIAMENT:
			auditaEnviament(objecteAuditar, tipusOperacio, joinPoint);
			break;
		default:
			log.error("Error auditoria: No s'ha informat el tipus d'entitat a auditar per: " + (objecteAuditar != null ? objecteAuditar.toString() : "null"));
			break;
		}
	}

	private void auditaAplicacio(Object objecteAuditar, TipusOperacio tipusOperacio, String joinPoint) {

		AplicacioAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: APLICACIO");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof AplicacioDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: AplicacioDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = AplicacioAudit.getBuilder((AplicacioDto)objecteAuditar, tipusOperacio, joinPoint).build();
			aplicacioAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaEntitat(Object objecteAuditar, TipusOperacio tipusOperacio, String joinPoint) {

		EntitatAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: ENTITAT");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof EntitatDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: EntitatDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = EntitatAudit.getBuilder((EntitatDto)objecteAuditar, tipusOperacio, joinPoint).build();
			entitatAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaGrup(Object objecteAuditar, TipusOperacio tipusOperacio, String joinPoint) {

		GrupAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: GRUP");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof GrupDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: GrupDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = GrupAudit.getBuilder((GrupDto)objecteAuditar, tipusOperacio, joinPoint).build();
			grupAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaProcediment(Object objecteAuditar, TipusOperacio tipusOperacio, TipusObjecte tipusObjecte, String joinPoint) {

		ProcedimentAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: PROCEDIMENT");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof ProcedimentEntity) && !(objecteAuditar instanceof ProcSerDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: ProcedimentEntity || ProcedimentDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = tipusObjecte == null || TipusObjecte.ENTITAT.equals(tipusObjecte) ?
					ProcedimentAudit.getBuilder((ProcedimentEntity)objecteAuditar, tipusOperacio, joinPoint).build()
					: ProcedimentAudit.getBuilder((ProcSerDto)objecteAuditar, tipusOperacio, joinPoint).build();
			procedimentAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaServei(Object objecteAuditar, TipusOperacio tipusOperacio, TipusObjecte tipusObjecte, String joinPoint) {

		ProcedimentAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha trobat l'objecte a auditar de tipus: SERVEI");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof ServeiEntity) && !(objecteAuditar instanceof ProcSerDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: ServeiEntity || ServeiDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = tipusObjecte == null || TipusObjecte.ENTITAT.equals(tipusObjecte) ?
					ProcedimentAudit.getBuilder((ProcSerEntity)objecteAuditar, tipusOperacio, joinPoint).build()
					: ProcedimentAudit.getBuilder((ProcSerDto)objecteAuditar, tipusOperacio, joinPoint).build();
			procedimentAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaProcedimentGrup(Object objecteAuditar, TipusOperacio tipusOperacio, String joinPoint) {

		GrupProcedimentAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: PROCEDIMENT_GRUP");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof ProcSerGrupDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: ProcedimentGrupDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = GrupProcedimentAudit.getBuilder((ProcSerGrupDto)objecteAuditar, tipusOperacio, joinPoint).build();
			grupProcedimentAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaNotificacio(Object objecteAuditar, TipusOperacio tipusOperacio, String joinPoint) {

		NotificacioAudit audit = null;
		boolean isAuditar = true;
		
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: NOTIFICACIO");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof NotificacioEntity)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: NotificacioEntity");
			isAuditar = false;
		}
		if (isAuditar) {
//			notificacioAuditRepository.flush();
			NotificacioEventEntity lastErrorEvent = notificacioHelper.getNotificaErrorEvent((NotificacioEntity)objecteAuditar);
			audit = new NotificacioAudit((NotificacioEntity)objecteAuditar, lastErrorEvent, tipusOperacio, joinPoint);
			NotificacioAudit lastAudit = notificacioAuditRepository.findLastAudit(audit.getNotificacioId());
			if (lastAudit == null || !audit.getTipusOperacio().equals(lastAudit.getTipusOperacio()) || !audit.equals(lastAudit)) {
				notificacioAuditRepository.saveAndFlush(audit);
			}
		}
	}

	private void auditaEnviament(Object objecteAuditar, TipusOperacio tipusOperacio, String joinPoint) {

		NotificacioEnviamentAudit audit = null;
		boolean isAuditar = true;
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: ENVIAMENT");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof NotificacioEnviamentEntity)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: NotificacioEnviamentEntity");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = NotificacioEnviamentAudit.getBuilder((NotificacioEnviamentEntity)objecteAuditar, tipusOperacio, joinPoint).build();
			NotificacioEnviamentAudit lastAudit = notificacioEnviamentAuditRepository.findLastAudit(audit.getEnviamentId());
			if (lastAudit == null || !audit.getTipusOperacio().equals(lastAudit.getTipusOperacio()) || !audit.equals(lastAudit)) {
				notificacioEnviamentAuditRepository.saveAndFlush(audit);
			}
		}
	}
}
