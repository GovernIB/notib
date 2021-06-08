package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;


/**
 * Helper per a convertir notificació entity i enviament entity a notificacioEnviamentDto.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class AuditEnviamentHelper {

	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.CREATE)
	public NotificacioEnviamentEntity desaEnviament(
			EntitatEntity entitat,
			NotificacioEntity notificacioEntity,
			Enviament enviament,
			ServeiTipusEnumDto serveiTipus,
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			PersonaEntity titular,
			List<PersonaEntity> destinataris,
			EntregaPostalViaTipusEnum viaTipus) {
		return notificacioEnviamentRepository.saveAndFlush(NotificacioEnviamentEntity.
				getBuilderV2(
						enviament,
						entitat.isAmbEntregaDeh(),
						numeracioTipus, 
						tipusConcret, 
						serveiTipus, 
						notificacioEntity, 
						titular, 
						destinataris).domiciliViaTipus(toEnviamentViaTipusEnum(viaTipus)).build());
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.CREATE)
	public NotificacioEnviamentEntity desaEnviamentAmbReferencia(
			EntitatEntity entitat,
			NotificacioEntity notificacioGuardada,
			Enviament enviament,
			ServeiTipusEnumDto serveiTipus,
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			PersonaEntity titular,
			List<PersonaEntity> destinataris,
			EntregaPostalViaTipusEnum viaTipus) {
		NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
				NotificacioEnviamentEntity.getBuilderV2(
						enviament, 
						entitat.isAmbEntregaDeh(),
						numeracioTipus, 
						tipusConcret, 
						serveiTipus, 
						notificacioGuardada, 
						titular, 
						destinataris)
				.domiciliViaTipus(toEnviamentViaTipusEnum(viaTipus)).build());
		log.debug(">> [ALTA] enviament creat");
		
		String referencia;
		try {
			referencia = notificaHelper.xifrarId(enviamentSaved.getId());
			log.debug(">> [ALTA] referencia creada");
		} catch (GeneralSecurityException ex) {
			log.debug(">> [ALTA] Error creant referència");
			throw new RuntimeException(
					"No s'ha pogut crear la referencia per al destinatari",
					ex);
		}
		enviamentSaved.updateNotificaReferencia(referencia);
		return enviamentSaved;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity updateEnviament(
			EntitatEntity entitat,
			NotificacioEntity notificacioEntity,
			Enviament enviament,
			ServeiTipusEnumDto serveiTipus,
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			PersonaEntity titular,
			EntregaPostalViaTipusEnum viaTipus) {
		NotificacioEnviamentEntity enviamentEntity = notificacioEnviamentRepository.findOne(enviament.getId());
		enviamentEntity.update(
				enviament,
				entitat.isAmbEntregaDeh(),
				numeracioTipus, 
				tipusConcret, 
				serveiTipus, 
				notificacioEntity, 
				titular, 
				toEnviamentViaTipusEnum(viaTipus));
		return enviamentEntity;
	}

	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity resetConsultaNotifica(NotificacioEnviamentEntity enviament) {
		enviament.refreshNotificaConsulta();
		return enviament;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity resetConsultaSir(NotificacioEnviamentEntity enviament) {
		enviament.refreshSirConsulta();
		return enviament;
	}

	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity updateEnviamentEnviat(
			NotificacioEntity notificacio,
			NotificacioEventEntity event,
			String identificadorResultat,
			NotificacioEnviamentEntity enviament) {
		enviament.updateNotificaEnviada(identificadorResultat);

		//Registrar event per enviament
		log.info(" >>> Canvi estat a ENVIADA ");
		event.setEnviament(enviament);
		notificacio.updateEventAfegir(event);
		notificacioEventRepository.save(event);

		return enviament;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity updateErrorNotifica(
			NotificacioEnviamentEntity enviament, 
			boolean notificaError, 
			NotificacioEventEntity event) {
		enviament.updateNotificaError(
				notificaError, 
				event);
		notificacioEnviamentRepository.saveAndFlush(enviament);
		return enviament;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity updateRegistreEnviament(
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			String registreNum,
			Date registreData,
			NotificacioRegistreEstatEnumDto registreEstat,
			boolean totsAdministracio,
			NotificacioEventEntity event) {
		enviament.setRegistreNumeroFormatat(registreNum);
		enviament.setRegistreData(registreData);
		enviament.updateRegistreEstat(registreEstat);
		
		//Comunicació + administració (SIR)
		if (totsAdministracio) {
			enviament.setNotificaEstat(NotificacioEnviamentEstatEnumDto.ENVIAT_SIR);
		}
		
		event.setEnviament(enviament);
		notificacioEntity.updateEventAfegir(event);
		notificacioEventRepository.saveAndFlush(event);
		return enviament;
	}
	
	private NotificaDomiciliViaTipusEnumDto toEnviamentViaTipusEnum(
			EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return NotificaDomiciliViaTipusEnumDto.valueOf(viaTipus.name());
	}

	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.DELETE)
	public NotificacioEnviamentEntity deleteEnviament(NotificacioEnviamentEntity enviament) {
		notificacioEventRepository.deleteByEnviament(enviament);
		notificacioEnviamentRepository.delete(enviament);
		notificacioEnviamentRepository.flush();
		return enviament;
	}
}
