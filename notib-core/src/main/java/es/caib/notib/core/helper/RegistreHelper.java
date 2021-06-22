/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.aspect.UpdateEnviamentTable;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

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
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired 
	private EmailNotificacioHelper emailNotificacioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;

	@UpdateEnviamentTable
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	public NotificacioEnviamentEntity enviamentRefrescarEstatRegistre(Long enviamentId) {
		long startTime;
		double elapsedTime;
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacio().getId());
		logger.info(" [SIR] Inici actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		String descripcio;
		logger.debug("Comunicació SIR --> consular estat...");

		startTime = System.nanoTime();
		enviament.updateSirNovaConsulta(pluginHelper.getConsultaSirReintentsPeriodeProperty());
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		logger.info(" [TIMER-SIR] Actualizar SIR nova consulta (updateSirNovaConsulta)  [Id: " + enviamentId + "]: " + elapsedTime + " ms");
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb Registre (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"registreNumeroFormatat=" + enviament.getRegistreNumeroFormatat() + ")";
		
		try {
			if (enviament.getRegistreNumeroFormatat() == null) {
				logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				throw new ValidationException(
						enviament,
						NotificacioEnviamentEntity.class,
						"L'enviament no té número de registre SIR");
			}

			logger.debug("Comunicació SIR --> número registre formatat: " + enviament.getRegistreNumeroFormatat());
			logger.debug("Comunicació SIR --> consulant estat...");

			startTime = System.nanoTime();
			RespostaConsultaRegistre resposta = pluginHelper.obtenerAsientoRegistral(
					notificacio.getEntitat().getDir3Codi(),
					enviament.getRegistreNumeroFormatat(),
					2L,  //registre sortida
					false);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			logger.info(" [TIMER-SIR] Obtener asiento registral  [Id: " + enviamentId + "]: " + elapsedTime + " ms");

			logger.debug("Comunicació SIR --> creació event...");
			if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().isEmpty()) {
				startTime = System.nanoTime();
				//Crea un nou event
				notificacioEventHelper.addRegistreCallBackEstatEvent(notificacio, enviament, resposta.getErrorDescripcio(), true);

				if (enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty()) {
					notificacioEventHelper.addNotificaConsultaSirErrorEvent(notificacio, enviament);
				}
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-SIR] Creació nou event error [Id: " + enviamentId + "]: " + elapsedTime + " ms");
			} else {
				startTime = System.nanoTime();
				enviamentUpdateDatat(
						resposta.getEstat(),
						resposta.getRegistreData(),
						resposta.getSirRecepecioData(),
						resposta.getSirRegistreDestiData(),
						resposta.getRegistreNumeroFormatat(),
						enviament);
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-SIR] Actualitzar estat comunicació SIR [Id: " + enviamentId + "]: " + elapsedTime + " ms");

				logger.debug("Comunicació SIR --> nou estat: " + resposta.getEstat() != null ? resposta.getEstat().name() : "");
				if (resposta.getEstat() != null)
					descripcio = resposta.getEstat().name();
				else
					descripcio = resposta.getRegistreNumeroFormatat();

				//Crea un nou event
				notificacioEventHelper.addRegistreCallBackEstatEvent(notificacio, enviament, descripcio, false);
				logger.debug("Comunicació SIR --> enviar correu si és aplicació...");
				if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
					startTime = System.nanoTime();
					emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					logger.info(" [TIMER-SIR] Preparar enviament mail notificació [Id: " + enviamentId + "]: " + elapsedTime + " ms");
				}
				enviament.refreshSirConsulta();
			}
			logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacio, enviament, ExceptionUtils.getStackTrace(ex));
			if (enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty()) {
				notificacioEventHelper.addNotificaConsultaSirErrorEvent(notificacio, enviament);
			}
			logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
//			return false;
		}
		return enviament;
	}



	public void enviamentUpdateDatat(
			NotificacioRegistreEstatEnumDto registreEstat,
			Date registreEstatData,
			Date sirRecepcioData,
			Date sirRegistreDestiData,
			String registreNumeroFormatat,
			NotificacioEnviamentEntity enviament) {
		logger.debug("Estat actual: " + registreEstat.name());
		enviament.updateRegistreEstat(
				registreEstat,
				registreEstatData,
				sirRecepcioData,
				sirRegistreDestiData,
				registreNumeroFormatat);
		
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
			auditNotificacioHelper.updateEstatAFinalitzada(registreEstat.name(), enviament.getNotificacio());

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
