package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.service.AuditService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.auditoria.*;
import es.caib.notib.core.repository.auditoria.*;
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
	
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void audita(
			Object objecteAuditar,
			TipusOperacio tipusOperacio,
			TipusEntitat tipusEntitat,
			TipusObjecte tipusObjecte,
			String joinPoint) {

		switch (tipusEntitat) {
		case APLICACIO:
			auditaAplicacio(
					objecteAuditar, 
					tipusOperacio, 
					joinPoint);
			break;
		case ENTITAT:
			auditaEntitat(
					objecteAuditar, 
					tipusOperacio, 
					joinPoint);
			break;
		case GRUP:
			auditaGrup(
					objecteAuditar, 
					tipusOperacio, 
					joinPoint);
			break;
		case PROCEDIMENT:
			auditaProcediment(
					objecteAuditar, 
					tipusOperacio, 
					tipusObjecte, 
					joinPoint);
			break;
		case PROCEDIMENT_GRUP:
			auditaProcedimentGrup(
					objecteAuditar, 
					tipusOperacio, 
					joinPoint);
			break;
		case NOTIFICACIO:
			auditaNotificacio(
					objecteAuditar, 
					tipusOperacio, 
					joinPoint);
			break;
		case ENVIAMENT:
			auditaEnviament(
					objecteAuditar, 
					tipusOperacio, 
					joinPoint);
			break;
		default:
			log.error(
					"Error auditoria: No s'ha informat el tipus d'entitat a auditar per: " +
							(objecteAuditar != null ? objecteAuditar.toString() : "null"));
			break;
		}

	}

	private void auditaAplicacio(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			String joinPoint) {
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
			audit = AplicacioAudit.getBuilder(
					(AplicacioDto)objecteAuditar, 
					tipusOperacio,
					joinPoint).build();
			aplicacioAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaEntitat(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			String joinPoint) {
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
			audit = EntitatAudit.getBuilder(
					(EntitatDto)objecteAuditar, 
					tipusOperacio,
					joinPoint).build();
			entitatAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaGrup(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			String joinPoint) {
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
			audit = GrupAudit.getBuilder(
					(GrupDto)objecteAuditar, 
					tipusOperacio,
					joinPoint).build();
			grupAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaProcediment(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			TipusObjecte tipusObjecte, 
			String joinPoint) {
		ProcedimentAudit audit = null;
		boolean isAuditar = true;
		
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: PROCEDIMENT");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof ProcedimentEntity) && !(objecteAuditar instanceof ProcedimentDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: ProcedimentEntity || ProcedimentDto");
			isAuditar = false;
		}
		if (isAuditar) {
			if (tipusObjecte == null || TipusObjecte.ENTITAT.equals(tipusObjecte)) {
				audit = ProcedimentAudit.getBuilder(
						(ProcedimentEntity)objecteAuditar, 
						tipusOperacio,
						joinPoint).build();
			} else {
				audit = ProcedimentAudit.getBuilder(
						(ProcedimentDto)objecteAuditar, 
						tipusOperacio,
						joinPoint).build();
			}
			procedimentAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaProcedimentGrup(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		GrupProcedimentAudit audit = null;
		boolean isAuditar = true;
		
		if (objecteAuditar == null) {
			log.error("Error auditoria: No s'ha l'objecte a auditar de tipus: PROCEDIMENT_GRUP");
			isAuditar = false;
		} else if (!(objecteAuditar instanceof ProcedimentGrupDto)) {
			log.error("Error auditoria: L'objecte a auditar no és del tipus correcte: ProcedimentGrupDto");
			isAuditar = false;
		}
		if (isAuditar) {
			audit = GrupProcedimentAudit.getBuilder(
					(ProcedimentGrupDto)objecteAuditar, 
					tipusOperacio,
					joinPoint).build();
			grupProcedimentAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaNotificacio(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			String joinPoint) {
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
			audit = NotificacioAudit.getBuilder(
					(NotificacioEntity)objecteAuditar, 
					tipusOperacio,
					joinPoint).build();
			NotificacioAudit lastAudit = notificacioAuditRepository.findLastAudit(audit.getNotificacioId());
			if (lastAudit == null || !audit.getTipusOperacio().equals(lastAudit.getTipusOperacio()) || !audit.equals(lastAudit))
				notificacioAuditRepository.saveAndFlush(audit);
		}
	}

	private void auditaEnviament(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			String joinPoint) {
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
			audit = NotificacioEnviamentAudit.getBuilder(
					(NotificacioEnviamentEntity)objecteAuditar, 
					tipusOperacio,
					joinPoint).build();
			NotificacioEnviamentAudit lastAudit = notificacioEnviamentAuditRepository.findLastAudit(audit.getEnviamentId());
			if (lastAudit == null || !audit.getTipusOperacio().equals(lastAudit.getTipusOperacio()) || !audit.equals(lastAudit))
				notificacioEnviamentAuditRepository.saveAndFlush(audit);
		}
	}

}
