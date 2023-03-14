/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
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

		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacio().getId());

		LogTimeHelper log = new LogTimeHelper(logger);
		log.info(" [SIR] Inici actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		boolean error = false;
		String errorPrefix = "Error al consultar l'estat d'un Registre (" + "notificacioId=" + notificacio.getId() + ", " + "registreNumeroFormatat=" + enviament.getRegistreNumeroFormatat() + ")";
		String errorDescripcio = null;

		try {
			// Validacions
			if (enviament.getRegistreNumeroFormatat() == null) {
				log.infoWithoutTime(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]. L'enviament no té número de registre SIR");
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, "L'enviament no té número de registre SIR");
			}

			log.debugWithoutTime("Comunicació SIR --> número registre formatat: " + enviament.getRegistreNumeroFormatat());

			// Consulta al registre
			RespostaConsultaRegistre resposta = pluginHelper.obtenerAsientoRegistral(
					notificacio.getEntitat().getDir3Codi(),
					enviament.getRegistreNumeroFormatat(),
					2L,  //registre sortida
					false);

			log.info(" [TIMER-SIR] Obtener asiento registral  [Id: " + enviamentId + "]");

			// Consulta retorna error
			if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().isEmpty()) {
				throw new RegistreNotificaException(resposta.getErrorDescripcio());

			// Consulta retorna correctement
			} else {
				enviamentUpdateDatat(
						resposta.getEstat(),
						resposta.getRegistreData(),
						resposta.getSirRecepecioData(),
						resposta.getSirRegistreDestiData(),
						resposta.getRegistreNumeroFormatat(),
						enviament);
				log.info(" [TIMER-SIR] Actualitzar estat comunicació SIR [Id: " + enviamentId + "]: ");

				if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
					emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
					log.info(" [TIMER-SIR] Preparar enviament mail notificació [Id: " + enviamentId + "]");
				}
				enviament.refreshSirConsulta();
			}
			log.infoWithoutTime(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			error = true;
			errorDescripcio = getErrorDescripcio(enviament.getSirConsultaIntent(), ex);
			logger.error(errorPrefix, ex);
		}

		boolean errorMaxReintents = false;
		if (error) {
			enviament.updateSirNovaConsulta(pluginHelper.getConsultaSirReintentsPeriodeProperty());
			errorMaxReintents = enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty();
		}

		notificacioEventHelper.addSirConsultaEvent(enviament, error, errorDescripcio, errorMaxReintents);
		logger.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		return enviament;
	}

	private static String getErrorDescripcio(int intent, Exception ex) {
		String errorDescripcio;
		// Generam el missatge d'error
		errorDescripcio = "Intent " + intent + "\n\n";
		if (ex instanceof ValidationException || ex instanceof RegistreNotificaException) {
			errorDescripcio += ex.getMessage();
		} else {
			errorDescripcio += ExceptionUtils.getStackTrace(ex);
		}
		return errorDescripcio;
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
