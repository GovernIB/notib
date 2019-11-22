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

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
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
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
		enviament.setNotificacio(notificacio);
		NotificacioEventEntity.Builder eventBuilder  = null;
		String descripcio;
		
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb Registre (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"registreNumeroFormatat=" + enviament.getRegistreNumeroFormatat() + ")";
		
		try {
			if (enviament.getRegistreNumeroFormatat() != null) {
				RespostaConsultaRegistre resposta = pluginHelper.obtenerAsientoRegistral(
						notificacio.getEntitat().getCodi(), 
						enviament.getRegistreNumeroFormatat(), 
						2L,  //registre sortida
						false);
				
				if (resposta != null) {
					enviamentUpdateDatat(
							resposta.getEstat(),
							resposta.getRegistreData(), 
							resposta.getRegistreNumeroFormatat(), 
							enviament);
					
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
					if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
						emailHelper.prepararEnvioEmailNotificacio(notificacio);
					}
				}
				return true;
			} else {
				return false;
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
			return false;
		}
	}
	public void enviamentUpdateDatat(
			NotificacioRegistreEstatEnumDto registreEstat,
			Date registreEstatData,
			String registreNumeroFormatat,
			NotificacioEnviamentEntity enviament) {
		
		boolean estatFinal = 
				NotificacioRegistreEstatEnumDto.ANULAT.equals(registreEstat) ||
				NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(registreEstat);
		
		enviament.updateRegistreEstat(
				registreEstat,
				registreEstatData,
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
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
