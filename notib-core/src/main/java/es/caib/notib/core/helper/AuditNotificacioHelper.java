package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;


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
	private PluginHelper pluginHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.CREATE)
	public NotificacioEntity desaNotificacio(
			NotificacioDtoV2 notificacio, 
			EntitatEntity entitat,
			GrupEntity grupNotificacio, 
			OrganGestorEntity organGestor, 
			ProcedimentEntity procediment,
			DocumentEntity documentEntity,
			DocumentEntity document2Entity,
			DocumentEntity document3Entity,
			DocumentEntity document4Entity,
			DocumentEntity document5Entity,
			ProcedimentOrganEntity procedimentOrgan) {
		return notificacioRepository.saveAndFlush(NotificacioEntity.
				getBuilderV2(
						entitat,
						notificacio.getEmisorDir3Codi(),
						organGestor,
						pluginHelper.getNotibTipusComunicacioDefecte(),
						notificacio.getEnviamentTipus(), 
						notificacio.getConcepte(),
						notificacio.getDescripcio(),
						notificacio.getEnviamentDataProgramada(),
						notificacio.getRetard(),
						notificacio.getCaducitat(),
						notificacio.getUsuariCodi(),
						procediment != null ? procediment.getCodi() : null,
						procediment,
						grupNotificacio != null ? grupNotificacio.getCodi() : null,
						notificacio.getNumExpedient(),
						TipusUsuariEnumDto.INTERFICIE_WEB,
						procedimentOrgan,
						notificacio.getIdioma())
				.document(documentEntity)
				.document2(document2Entity)
				.document3(document3Entity)
				.document4(document4Entity)
				.document5(document5Entity)
				.build());
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacio(
			NotificacioDtoV2 notificacio, 
			EntitatEntity entitat,
			NotificacioEntity notificacioEntity, 
			GrupEntity grupNotificacio, 
			OrganGestorEntity organGestor,
			ProcedimentEntity procediment, 
			DocumentEntity documentEntity,
			DocumentEntity document2Entity,
			DocumentEntity document3Entity,
			DocumentEntity document4Entity,
			DocumentEntity document5Entity,
			ProcedimentOrganEntity procedimentOrgan) {
		notificacioEntity.update(
				entitat,
				notificacio.getEmisorDir3Codi(),
				organGestor,
				pluginHelper.getNotibTipusComunicacioDefecte(),
				notificacio.getEnviamentTipus(), 
				notificacio.getConcepte(),
				notificacio.getDescripcio(),
				notificacio.getEnviamentDataProgramada(),
				notificacio.getRetard(),
				notificacio.getCaducitat(),
				notificacio.getUsuariCodi(),
				procediment != null ? procediment.getCodi() : null,
				procediment,
				grupNotificacio != null ? grupNotificacio.getCodi() : null,
				notificacio.getNumExpedient(),
				TipusUsuariEnumDto.INTERFICIE_WEB,
				documentEntity,
				document2Entity,
				document3Entity,
				document4Entity,
				document5Entity,
				procedimentOrgan,
				notificacio.getIdioma());
		return notificacioEntity;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.DELETE)
	public NotificacioEntity deleteNotificacio(NotificacioEntity notificacioEntity) {
		notificacioRepository.delete(notificacioEntity);
		return notificacioEntity;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateEstatNotificacio(
			String notificaEstatNom,
			NotificacioEntity notificacio) {
		notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacio.updateMotiu(notificaEstatNom);
		notificacioEventHelper.clearOldUselessEvents(notificacio);
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
	public NotificacioEntity updateNotificacioRegistre(RespostaConsultaRegistre arbResposta,
													   NotificacioEntity notificacioEntity) {
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
