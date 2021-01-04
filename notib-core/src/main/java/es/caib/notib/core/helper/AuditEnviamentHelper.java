package es.caib.notib.core.helper;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;


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
	public void reiniciaConsultaNotifica(NotificacioEnviamentEntity enviament) {
		enviament.refreshNotificaConsulta();
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity reiniciaConsultaSir(NotificacioEnviamentEntity enviament) {
		enviament.refreshSirConsulta();
		return enviament;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity updateEnviamentEnviat(
			NotificacioEntity notificacio,
			NotificacioEventEntity.Builder eventBulider,
			NotificacioEventEntity event,
			ResultadoEnvio resultadoEnvio,
			NotificacioEnviamentEntity enviament) {
		enviament.updateNotificaEnviada(
				resultadoEnvio.getIdentificador());
		
		//Registrar event per enviament
		log.info(" >>> Canvi estat a ENVIADA ");
		eventBulider.enviament(enviament);
		notificacio.updateEventAfegir(event);
		notificacioEventRepository.save(event);
		
		return enviament;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity actualizaErrorNotifica(
			NotificacioEnviamentEntity enviament, 
			boolean notificaError, 
			NotificacioEventEntity event) {
		enviament.updateNotificaError(
				notificaError, 
				event);
		return enviament;
	}
	
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity actualitzaRegistreEnviament(
			RespostaConsultaRegistre arbResposta,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			boolean totsAdministracio,
			NotificacioEventEntity.Builder eventBulider,
			NotificacioEventEntity event) {
		enviament.setRegistreNumeroFormatat(arbResposta.getRegistreNumeroFormatat());
		enviament.setRegistreData(arbResposta.getRegistreData());
		enviament.setRegistreEstat(arbResposta.getEstat());
		
		//Comunicació + administració (SIR)
		if (totsAdministracio) {
			enviament.setNotificaEstat(NotificacioEnviamentEstatEnumDto.ENVIAT_SIR);
		}
		
		eventBulider.enviament(enviament);
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
	
}
