/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService.TipusEntitat;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.aspect.Audita;
import es.caib.notib.logic.aspect.UpdateEnviamentTable;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * Helper per a interactuar amb el servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacio().getId()).orElseThrow();
		log.info(" [SIR] Inici actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		String descripcio;
		log.debug("Comunicació SIR --> consular estat...");
		startTime = System.nanoTime();
		enviament.updateSirNovaConsulta(pluginHelper.getConsultaSirReintentsPeriodeProperty());
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-SIR] Actualizar SIR nova consulta (updateSirNovaConsulta)  [Id: " + enviamentId + "]: " + elapsedTime + " ms");
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb Registre (notificacioId=" + notificacio.getId() +
								", registreNumeroFormatat=" + enviament.getRegistreNumeroFormatat() + ")";
		try {
			if (enviament.getRegistreNumeroFormatat() == null) {
				log.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, "L'enviament no té número de registre SIR");
			}

			log.debug("Comunicació SIR --> número registre formatat: " + enviament.getRegistreNumeroFormatat());
			log.debug("Comunicació SIR --> consulant estat...");

			startTime = System.nanoTime();
			RespostaConsultaRegistre resposta = pluginHelper.obtenerAsientoRegistral(notificacio.getEntitat().getDir3Codi(), enviament.getRegistreNumeroFormatat(),
																					2L,  /*registre sortida*/ false);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			log.info(" [TIMER-SIR] Obtener asiento registral  [Id: " + enviamentId + "]: " + elapsedTime + " ms");
			log.debug("Comunicació SIR --> creació event...");
			if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().isEmpty()) {
				startTime = System.nanoTime();
				//Crea un nou event
				notificacioEventHelper.addRegistreCallBackEstatEvent(notificacio, enviament, resposta.getErrorDescripcio(), true);
				if (enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty()) {
					notificacioEventHelper.addNotificaConsultaSirErrorEvent(notificacio, enviament);
				}
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				log.info(" [TIMER-SIR] Creació nou event error [Id: " + enviamentId + "]: " + elapsedTime + " ms");
			} else {
				startTime = System.nanoTime();
				enviamentUpdateDatat(resposta.getEstat(), resposta.getRegistreData(), resposta.getSirRecepecioData(), resposta.getSirRegistreDestiData(),
									resposta.getRegistreNumeroFormatat(), enviament);
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				log.info(" [TIMER-SIR] Actualitzar estat comunicació SIR [Id: " + enviamentId + "]: " + elapsedTime + " ms");
				log.debug("Comunicació SIR --> nou estat: " + resposta.getEstat() != null ? resposta.getEstat().name() : "");
				descripcio = resposta.getEstat() != null ? resposta.getEstat().name() : resposta.getRegistreNumeroFormatat();
				//Crea un nou event
				notificacioEventHelper.addRegistreCallBackEstatEvent(notificacio, enviament, descripcio, false);
				log.debug("Comunicació SIR --> enviar correu si és aplicació...");
				if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
					startTime = System.nanoTime();
					emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					log.info(" [TIMER-SIR] Preparar enviament mail notificació [Id: " + enviamentId + "]: " + elapsedTime + " ms");
				}
				enviament.refreshSirConsulta();
			}
			log.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			log.error(errorPrefix, ex);
			notificacioEventHelper.addRegistreConsultaInfoErrorEvent(notificacio, enviament, ExceptionUtils.getStackTrace(ex));
			if (enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty()) {
				notificacioEventHelper.addNotificaConsultaSirErrorEvent(notificacio, enviament);
			}
			log.info(" [SIR] Fi actualitzar estat registre enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
//			return false;
		}
		return enviament;
	}

	public void enviamentUpdateDatat(NotificacioRegistreEstatEnumDto registreEstat, Date registreEstatData, Date sirRecepcioData, Date sirRegistreDestiData,
									 String registreNumeroFormatat, NotificacioEnviamentEntity enviament) {

		log.debug("Estat actual: " + registreEstat.name());
		enviament.updateRegistreEstat(registreEstat, registreEstatData, sirRecepcioData, sirRegistreDestiData, registreNumeroFormatat);
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
		log.debug("Estat final: " + estatsEnviamentsFinals);
		if (estatsEnviamentsFinals) {
			auditNotificacioHelper.updateEstatAFinalitzada(registreEstat.name(), enviament.getNotificacio());
			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
				enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.PROCESSADA);
				enviament.getNotificacio().updateMotiu(registreEstat.name());
				enviament.getNotificacio().updateEstatDate(new Date());
			}
		}
		log.debug("L'estat de la comunicació SIR s'ha actualitzat correctament.");
	}
}
