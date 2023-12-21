/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.email.EmailConstants;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotTableUpdate;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

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
	private CallbackHelper callbackHelper;
	@Autowired
	private EmailNotificacioHelper emailNotificacioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private AuditHelper auditHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	@Autowired
	protected JmsTemplate jmsTemplate;


	public NotificacioEnviamentEntity enviamentRefrescarEstatRegistre(Long enviamentId) {

		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		var notificacio = notificacioRepository.findById(enviament.getNotificacio().getId()).orElseThrow();
		var logTimeHelper = new LogTimeHelper(log);
		var estatText =  ", Estat: ";
		var fiActEstatRegistreText = " [SIR] Fi actualitzar estat registre enviament [Id: ";
		logTimeHelper.info(" [SIR] Inici actualitzar estat registre enviament [Id: " + enviament.getId() + estatText + enviament.getNotificaEstat() + "]");
		var error = false;
		var errorPrefix = "Error al consultar l'estat d'un Registre (" + "notificacioId=" + notificacio.getId() + ", " + "registreNumeroFormatat=" + enviament.getRegistreNumeroFormatat() + ")";
		String errorDescripcio = null;
		var canviEstat = false;
		var errorUltimaConsulta = enviament.getSirConsultaIntent() > 0;
		try {
			// Validacions
			if (enviament.getRegistreNumeroFormatat() == null) {
				logTimeHelper.infoWithoutTime(fiActEstatRegistreText + enviament.getId() + estatText + enviament.getNotificaEstat() + "]. L'enviament no té número de registre SIR");
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, "L'enviament no té número de registre SIR");
			}
			logTimeHelper.debugWithoutTime("Comunicació SIR --> número registre formatat: " + enviament.getRegistreNumeroFormatat());
			// Consulta al registre
			var resposta = pluginHelper.obtenerAsientoRegistral(notificacio.getEntitat().getDir3Codi(), enviament.getRegistreNumeroFormatat(), 2L,  /*registre sortida*/ false);
			logTimeHelper.info(" [TIMER-SIR] Obtener asiento registral  [Id: " + enviamentId + "]");
			// Consulta retorna error
			if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().isEmpty()) {
				throw new RegistreNotificaException(resposta.getErrorDescripcio());
			}
			// Consulta retorna correctement
			canviEstat = !enviament.getRegistreEstat().equals(resposta.getEstat());
			enviamentUpdateDatat(resposta.getEstat(), resposta.getRegistreData(), resposta.getSirRecepecioData(), resposta.getSirRegistreDestiData(), resposta.getRegistreNumeroFormatat(), enviament);
			logTimeHelper.info(" [TIMER-SIR] Actualitzar estat comunicació SIR [Id: " + enviamentId + "]: ");
			if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA && canviEstat) {
				try {
					jmsTemplate.convertAndSend(EmailConstants.CUA_EMAIL_NOTIFICACIO, notificacio.getId());
				} catch (JmsException ex) {
					log.error("Hi ha hagut un error al intentar enviar el correu electrònic de la notificació amb id: ." + notificacio.getId(), ex);
				}
				logTimeHelper.info(" [TIMER-SIR] Preparar enviament mail notificació [Id: " + enviamentId + "]");
			}
			enviament.refreshSirConsulta();
			logTimeHelper.infoWithoutTime(fiActEstatRegistreText + enviament.getId() + estatText + enviament.getNotificaEstat() + "]");
		} catch (Exception ex) {
			error = true;
			errorDescripcio = getErrorDescripcio(ex);
			log.error(errorPrefix, ex);
		}
		var errorMaxReintents = false;
		if (error) {
			enviament.updateSirNovaConsulta(pluginHelper.getConsultaSirReintentsPeriodeProperty());
			errorMaxReintents = enviament.getSirConsultaIntent() >= pluginHelper.getConsultaSirReintentsMaxProperty();
		}
		notificacioEventHelper.addSirConsultaEvent(enviament, error, errorDescripcio, errorMaxReintents);
		if (canviEstat || error && !errorUltimaConsulta || !error && errorUltimaConsulta) {
			callbackHelper.updateCallback(enviament, error, errorDescripcio);
		}
		logTimeHelper.info(fiActEstatRegistreText + enviament.getId() + estatText + enviament.getNotificaEstat() + "]");
		notificacioTableHelper.actualitzarRegistre(notificacio);
		enviamentTableHelper.actualitzarRegistre(enviament);
		auditHelper.auditaEnviament(enviament, TipusOperacio.UPDATE, "RegistreHelper.enviamentRefrescarEstatRegistre");
		return enviament;
	}

	private static String getErrorDescripcio(Exception ex) {

		// Generam el missatge d'error
		return ex instanceof ValidationException || ex instanceof RegistreNotificaException ? ex.getMessage(): ExceptionUtils.getStackTrace(ex);
	}


	public void enviamentUpdateDatat(NotificacioRegistreEstatEnumDto registreEstat, Date registreEstatData, Date sirRecepcioData, Date sirRegistreDestiData, String registreNumeroFormatat, NotificacioEnviamentEntity enviament) {

		log.debug("Estat actual: " + registreEstat.name());
		enviament.updateRegistreEstat(registreEstat, registreEstatData, sirRecepcioData, sirRegistreDestiData, registreNumeroFormatat);
		var estatsEnviamentsFinals = true;
		var enviaments = enviament.getNotificacio().getEnviaments();
		for (var env: enviaments) {
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
			var nouEstat = NotificacioEstatEnumDto.FINALITZADA;
			//Marcar com a processada si la notificació s'ha fet des de una aplicació
			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
				nouEstat = NotificacioEstatEnumDto.PROCESSADA;
			}
			enviament.getNotificacio().updateEstat(nouEstat);
			enviament.getNotificacio().updateMotiu(registreEstat.name());
			enviament.getNotificacio().updateEstatDate(new Date());
			notificacioTableHelper.actualitzar(NotTableUpdate.builder().id(enviament.getNotificacio().getId()).estat(nouEstat).estatDate(new Date()).build());
			auditHelper.auditaNotificacio(enviament.getNotificacio(), TipusOperacio.UPDATE, "RegistreHelper.enviamentUpdateDatat");
		}
		log.debug("L'estat de la comunicació SIR s'ha actualitzat correctament.");
	}

//	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
