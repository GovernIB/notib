/**
 *
 */
package es.caib.notib.logic.helper;

import es.caib.comanda.model.management.AvisTipus;
import es.caib.notib.logic.comanda.ComandaListener;
import es.caib.notib.logic.email.EmailConstants;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotTableUpdate;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CallbackHelper callbackHelper;
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
    @Autowired
    private AccioMassivaHelper accioMassivaHelper;
    @Autowired
    private ComandaListener comandaListener;
	@Autowired
	private RegistreSmHelper registreSmHelper;


    public NotificacioEnviamentEntity enviamentRefrescarEstatRegistre(ConsultaSirRequest consulta) {

		var enviamentId = consulta.getId();
		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		var notificacio = enviament.getNotificacio();
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
				var msg = "L'enviament no té número de registre SIR";
				logTimeHelper.infoWithoutTime(fiActEstatRegistreText + enviament.getId() + estatText + enviament.getNotificaEstat() + "]. " + msg);
				if (consulta.getAccioMassivaId() != null) {
					accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviamentId, msg, "");
				}
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, msg);
			}
			logTimeHelper.debugWithoutTime("Comunicació SIR --> número registre formatat: " + enviament.getRegistreNumeroFormatat());
			// Consulta al registre
			EntitatEntity entitat = notificacio.getEntitat();
			String codiDir3 = entitat.getDir3CodiReg() != null  && !entitat.getDir3CodiReg().isEmpty() ? entitat.getDir3CodiReg() : entitat.getDir3Codi();
			var resposta = pluginHelper.obtenerAsientoRegistral(codiDir3, enviament.getRegistreNumeroFormatat(), 2L,  /*registre sortida*/ false);
			logTimeHelper.info(" [TIMER-SIR] Obtener asiento registral  [Id: " + enviamentId + "]");
			// Consulta retorna error
			if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().isEmpty()) {
				throw new RegistreNotificaException(resposta.getErrorDescripcio());
			}
			// Consulta retorna correctement
			if (consulta.getAccioMassivaId() != null) {
				accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviamentId, "", "");
			}
			canviEstat = !enviament.getRegistreEstat().equals(resposta.getEstat());
			enviamentUpdateDatat(resposta, enviament);
			if (canviEstat) {
				comandaListener.enviarAvis(enviament, AvisTipus.INFO);
			}
			logTimeHelper.info(" [TIMER-SIR] Actualitzar estat comunicació SIR [Id: " + enviamentId + "]: ");
			if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA && canviEstat) {
				try {
					jmsTemplate.convertAndSend(EmailConstants.CUA_EMAIL_NOTIFICACIO, enviament.getId());
				} catch (JmsException ex) {
					var msg = "Hi ha hagut un error al intentar enviar el correu electronic de l'enviament " + enviament.getId() + " de la notificacio amb id: ." + notificacio.getId();
					log.error(msg, ex);
					if (consulta.getAccioMassivaId() != null) {
						accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviamentId, msg, "");
					}
				}
				logTimeHelper.info(" [TIMER-SIR] Preparar enviament mail enviament [Id: " + enviamentId + "]");
			}
//			enviament.refreshSirConsulta();
			logTimeHelper.infoWithoutTime(fiActEstatRegistreText + enviament.getId() + estatText + enviament.getNotificaEstat() + "]");
		} catch (Exception ex) {
			error = true;
			errorDescripcio = getErrorDescripcio(ex);
			if (consulta.getAccioMassivaId() != null) {
				accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviamentId, ex.getMessage(), Arrays.toString(ex.getStackTrace()));
			}
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


	public void enviamentUpdateDatat(RespostaConsultaRegistre resposta, NotificacioEnviamentEntity enviament) {

		var registreEstat = resposta.getEstat();
		var registreEstatData = resposta.getRegistreData();
		var sirRecepcioData = resposta.getSirRecepecioData();
		var sirRegistreDestiData = resposta.getSirRegistreDestiData();
		var registreNumeroFormatat = resposta.getRegistreNumeroFormatat();
		var motiu = resposta.getMotivo();
		NotibLogger.getInstance().info("Estat actual: " + registreEstat.name(), log, LoggingTipus.SIR);
		enviament.updateRegistreEstat(registreEstat, registreEstatData, sirRecepcioData, sirRegistreDestiData, registreNumeroFormatat, motiu);
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
		NotibLogger.getInstance().info("Estat final: " + estatsEnviamentsFinals, log, LoggingTipus.SIR);
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
		NotibLogger.getInstance().info("L'estat de la comunicació SIR s'ha actualitzat correctament.", log, LoggingTipus.SIR);
	}

	@Transactional
	public boolean enviarRegistre(EnviamentRegistreRequest enviamentRegistreRequest) {
		var enviamentUuid = enviamentRegistreRequest.getEnviamentUuid();
		try {
			var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
			var notificacio = enviament.getNotificacio();
			var numIntent = enviamentRegistreRequest.getNumIntent();
			notificacio.setRegistreEnviamentIntent(numIntent);
			NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> registrant ", log, LoggingTipus.REGISTRE);
			// Registrar enviament
			boolean registreSuccess = registreSmHelper.registrarEnviament(enviament, numIntent);
			NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> registrat ", log, LoggingTipus.REGISTRE);
			// Actualitzar notificació
			if (notificacioEnviamentRepository.areEnviamentsRegistrats(notificacio.getId()) == 1) {
				NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> actualitzant notificacio", log, LoggingTipus.REGISTRE);
				var isSir = notificacio.isComunicacioSir();
				notificacio.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIAT_SIR : NotificacioEstatEnumDto.REGISTRADA);
				// És possible que el registre ja retorni estats finals al registrar SIR?
				if (isSir && notificacio.getEnviaments().stream().allMatch(e -> e.isRegistreEstatFinal())) {
					var nouEstat = NotificacioEstatEnumDto.FINALITZADA;
					//Marcar com a processada si la notificació s'ha fet des de una aplicació
					if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
						nouEstat = NotificacioEstatEnumDto.PROCESSADA;
					}
					notificacio.updateEstat(nouEstat);
					notificacio.updateMotiu(enviament.getRegistreEstat().name());
					notificacio.updateEstatDate(new Date());
					comandaListener.enviarAvis(enviament, AvisTipus.INFO);
				}
			}
			NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> actualitzant registre", log, LoggingTipus.REGISTRE);
			notificacioTableHelper.actualitzarRegistre(notificacio);
			NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> audita notificacio", log, LoggingTipus.REGISTRE);
			auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "RegistreSmHelper.registrarEnviament");
			//            TEST
			//            var registreSuccess = new Random().nextBoolean();
			//            if (registreSuccess) {
			//                enviament.setRegistreData(new Date());
			//                notificacioEnviamentRepository.save(enviament);
			//            }
			NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> is success " + registreSuccess, log, LoggingTipus.REGISTRE);
			return registreSuccess;
		} catch (Exception ex) {
			NotibLogger.getInstance().info("[REGISTRE] Enviament de registre <" + enviamentUuid + "> error ", ex, log, LoggingTipus.REGISTRE);
			var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElse(null);
			if (enviament != null) {
				comandaListener.enviarAvis(enviament, AvisTipus.ERROR);
			}
			return false;
		}
	}

	@Transactional
	public boolean consultaSir(ConsultaSirRequest consultaSirRequest) {
		try {
			// Consultar enviament a SIR
			// notificacioService.enviamentRefrescarEstatRegistre(consultaSirRequest);
			enviamentRefrescarEstatRegistre(consultaSirRequest);
			var enviamentEntity = notificacioEnviamentRepository.findByUuid(consultaSirRequest.getEnviamentUuid()).orElseThrow();
			return enviamentEntity.getSirConsultaIntent() == 0;
		} catch (Exception ex) {
			log.error("Error a la consulta SIR per l'enviament " + consultaSirRequest.getEnviamentUuid());
			return false;
		}
	}

}
