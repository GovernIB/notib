package es.caib.notib.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;


/**
 * Helper per a convertir notificació entity i enviament entity a notificacioEnviamentDto.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AuditNotificacioHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateEstatNotificacio(
			NotificacioEnviamentEstatEnumDto notificaEstat,
			NotificacioEntity notificacio) {
		notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacio.updateMotiu(notificaEstat.name());
		return notificacio;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateLastCallbackError(
			NotificacioEntity notificacio,
			boolean error) {
		notificacio.updateLastCallbackError(error);
		return notificacio;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.CREATE)
	public NotificacioEntity desaNotificacio(NotificacioEntity notificacioEntity) {
		notificacioEntity = notificacioRepository.saveAndFlush(notificacioEntity);
		return notificacioEntity;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioProcessada(NotificacioEntity notificacioEntity, String motiu) {
		notificacioEntity.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
		notificacioEntity.updateEstatDate(new Date());
		notificacioEntity.updateMotiu(motiu);
		return notificacioEntity;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity netejarErrorsNotifica(NotificacioEntity notificacio) {
		notificacio.cleanNotificaError();
		return notificacio;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioErrorSir(NotificacioEntity notificacio, NotificacioEventEntity eventReintents) {
		notificacio.updateNotificaError(
				NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR,
				eventReintents);
		return notificacio;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioErrorRegistre(NotificacioEntity notificacioEntity, NotificacioEventEntity event) {
		notificacioEntity.updateNotificaError(
				NotificacioErrorTipusEnumDto.ERROR_REGISTRE,
				event);
		notificacioEntity.updateEventAfegir(event);
		return notificacioEntity;	
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioRegistre(RespostaConsultaRegistre arbResposta, NotificacioEntity notificacioEntity) {
		notificacioEntity.updateRegistreNumero(Integer.parseInt(arbResposta.getRegistreNumero()));
		notificacioEntity.updateRegistreNumeroFormatat(arbResposta.getRegistreNumeroFormatat());
		notificacioEntity.updateRegistreData(arbResposta.getRegistreData());
		notificacioEntity.updateEstat(NotificacioEstatEnumDto.REGISTRADA);
		return notificacioEntity;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public void updateNotificacioEnviada(NotificacioEntity notificacioEntity) {
		notificacioEntity.updateEstat(NotificacioEstatEnumDto.ENVIADA);
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity refreshRegistreNotificacio(NotificacioEntity notificacio) {
		notificacio.refreshRegistre();
		notificacio.cleanNotificaError();
		notificacioRepository.saveAndFlush(notificacio);
		return notificacio;
	}
}
