/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;

/**
 * Helper per a interactuar amb el servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RegistreHelper {
	
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired 
	private EmailHelper emailHelper;
	
	public boolean enviamentRefrescarEstatRegistre(Long enviamentId) {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		logger.info(" [SIR] Inici actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
		enviament.setNotificacio(notificacio);
		NotificacioEventEntity.Builder eventBuilder  = null;
		String descripcio;
		logger.debug("Comunicació SIR --> consular estat...");
		
		enviament.updateSirNovaConsulta(pluginHelper.getConsultaSirReintentsPeriodeProperty());
		
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb Registre (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"registreNumeroFormatat=" + enviament.getRegistreNumeroFormatat() + ")";
		
		try {
			if (enviament.getRegistreNumeroFormatat() != null) {
				logger.debug("Comunicació SIR --> número registre formatat: " + enviament.getRegistreNumeroFormatat());
				logger.debug("Comunicació SIR --> consulant estat...");
				
				RespostaConsultaRegistre resposta = pluginHelper.obtenerAsientoRegistral(
						notificacio.getEntitat().getDir3Codi(),
						enviament.getRegistreNumeroFormatat(), 
						2L,  //registre sortida
						false);
				
//				if (resposta != null) {
				
				logger.debug("Comunicació SIR --> creació event...");
				if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().isEmpty()) {
					//Crea un nou event
					eventBuilder = NotificacioEventEntity.getBuilder(
							NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
							enviament.getNotificacio()).
							error(true).
							errorDescripcio(resposta.getErrorDescripcio()).
							enviament(enviament);
					
					if (enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
						eventBuilder.callbackInicialitza();
					NotificacioEventEntity event = eventBuilder.build();
					
					notificacio.updateEventAfegir(event);
					enviament.updateNotificaError(true, event);
					notificacioEventRepository.save(event);
					if (enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty()) {
						NotificacioEventEntity.Builder eventReintentsBuilder  = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR,
								notificacio).
								enviament(enviament).
								error(true).
								errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a SIR");
						if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
							eventReintentsBuilder.callbackInicialitza();
							
						NotificacioEventEntity eventReintents = eventReintentsBuilder.build();
						
						notificacio.updateEventAfegir(eventReintents);
						notificacioEventRepository.save(eventReintents);
						notificacio.updateNotificaError(
								NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR,
								eventReintents);
					}
				} else {
					enviamentUpdateDatat(
							resposta.getEstat(),
							resposta.getRegistreData(), 
							resposta.getSirRecepecioData(),
							resposta.getSirRegistreDestiData(),
							resposta.getRegistreNumeroFormatat(), 
							enviament);
					
					logger.debug("Comunicació SIR --> nou estat: " + resposta.getEstat() != null ? resposta.getEstat().name() : "");
					if (resposta.getEstat() != null)
						descripcio = resposta.getEstat().name();
					else
						descripcio = resposta.getRegistreNumeroFormatat();
					
					//Crea un nou event
					eventBuilder = NotificacioEventEntity.getBuilder(
							NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT,
							enviament.getNotificacio()).
							enviament(enviament).
							descripcio(descripcio);
					
					if (enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
						eventBuilder.callbackInicialitza();
					NotificacioEventEntity event = eventBuilder.build();
					
					notificacio.updateEventAfegir(event);
					enviament.updateNotificaError(false, null);
					notificacioEventRepository.save(event);
					logger.debug("Comunicació SIR --> enviar correu si és aplicació...");
					if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
						emailHelper.prepararEnvioEmailNotificacio(notificacio);
					}
					enviament.refreshSirConsulta();
				}
//				}
				logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				return true;
			} else {
				logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				throw new ValidationException(
						enviament,
						NotificacioEnviamentEntity.class,
						"L'enviament no té número de registre SIR");
//				return false;
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.REGISTRE_CONSULTA_INFO,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			notificacioEventRepository.save(event);
			enviament.updateNotificaError(
					true,
					event);
			if (enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty()) {
				NotificacioEventEntity.Builder eventReintentsBuilder  = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("S'han esgotat els reintents de consulta de canvi d'estat a SIR");
				
				if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
					eventReintentsBuilder.callbackInicialitza();
					
				NotificacioEventEntity eventReintents = eventReintentsBuilder.build();
				
				notificacio.updateEventAfegir(eventReintents);
				notificacioEventRepository.save(eventReintents);
				notificacio.updateNotificaError(
						NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR,
						eventReintents);
			}
			logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			return false;
		}
	}
	public void enviamentUpdateDatat(
			NotificacioRegistreEstatEnumDto registreEstat,
			Date registreEstatData,
			Date sirRecepcioData,
			Date sirRegistreDestiData,
			String registreNumeroFormatat,
			NotificacioEnviamentEntity enviament) {
		logger.debug("Actualitzant estat comunicació SIR...");
		boolean estatFinal = 
				NotificacioRegistreEstatEnumDto.REBUTJAT.equals(registreEstat) ||
				NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(registreEstat);
		
		logger.debug("Estat actual: " + registreEstat.name());
		enviament.updateRegistreEstat(
				registreEstat,
				registreEstatData,
				sirRecepcioData,
				sirRegistreDestiData,
				registreNumeroFormatat,
				estatFinal);
		
		boolean estatsEnviamentsFinals = true;
		Set<NotificacioEnviamentEntity> enviaments = enviament.getNotificacio().getEnviaments();
		for (NotificacioEnviamentEntity env: enviaments) {
			if (env.getId().equals(enviament.getId())) {
				env = enviament;
			}
			if (!env.isRegistreEstatFinal()) {
				estatsEnviamentsFinals = false;
				break;
			}
		}
		logger.debug("Estat final: " + estatsEnviamentsFinals);
		if (estatsEnviamentsFinals) {
			enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.FINALITZADA);
			enviament.getNotificacio().updateMotiu(registreEstat.name());

			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
				enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.PROCESSADA);
				enviament.getNotificacio().updateMotiu(registreEstat.name());
				enviament.getNotificacio().updateEstatDate(new Date());
			}
		}
		logger.debug("L'estat de la comunicació SIR s'ha actualitzat correctament.");
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
