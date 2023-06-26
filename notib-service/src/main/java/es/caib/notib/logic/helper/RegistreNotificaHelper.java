/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class RegistreNotificaHelper {
	
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private AuditHelper auditHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private CallbackHelper callbackHelper;

	public static final String PROCES_DESC_PARAM = "Procés descripció: ";


	public boolean realitzarProcesRegistrar(NotificacioEntity notificacioEntity) throws RegistreNotificaException {

		log.info(" [REG-NOT] Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		if (notificacioEntity == null || notificacioEntity.getEntitat() == null) {
			log.error("Error realitzant proces de registrar.");
			return false;
		}
		ConfigHelper.setEntitatCodi(notificacioEntity.getEntitat().getCodi());
		var enviarANotifica = false;
		var isComunicacio = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioEntity.getEnviamentTipus());
		var t0 = System.currentTimeMillis();
		var desc = "Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]";
		var tipusEnv = new AccioParam("Tipus enviament: ", notificacioEntity.getEnviamentTipus().name());
		var sirActivat = new AccioParam("Sir activat", String.valueOf(isSirActivat()));
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, desc, IntegracioAccioTipusEnumDto.ENVIAMENT, tipusEnv, sirActivat);
		info.setCodiEntitat(notificacioEntity.getEntitat().getCodi());
		var codiDir3 = notificacioEntity.getEntitat().getDir3CodiReg() != null  && !notificacioEntity.getEntitat().getDir3CodiReg().isEmpty() ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi();
		var totsAdministracio = isAllEnviamentsAAdministracio(notificacioEntity);
		notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
		for(var enviament : notificacioEntity.getEnviaments() ) {
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Realitzant nou assentament registral de l'enviament: " + enviament.getId()));
			if (enviament.getRegistreData() != null) {
				continue;
			}
			try {
				registrarEnviament(enviament, t0, info, isComunicacio, codiDir3, totsAdministracio);
			} catch (Exception ex) {
				var errorDescripcio = "Hi ha hagut un error registrant l'enviament + " + enviament.getId();
				log.error(errorDescripcio, ex);
				integracioHelper.addAccioError(info, errorDescripcio, ex);
				throw new RegistreNotificaException(ex.getMessage(), ex);
			}
		}
		for (var env: notificacioEntity.getEnviaments()) {
			enviamentTableHelper.actualitzarRegistre(env);
		}
		if (enviamentsRegistrats(notificacioEntity.getEnviaments())) {
			var isSir = notificacioEntity.isComunicacioSir();
			notificacioEntity.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIADA : NotificacioEstatEnumDto.REGISTRADA);
			enviarANotifica = !isSir;
		}
		notificacioTableHelper.actualitzarRegistre(notificacioEntity);
		auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.UPDATE, "RegistreNotificaHelper.realitzarProcesRegistrar");
		integracioHelper.addAccioOk(info);
		return enviarANotifica;
	}
	// TODO: Reinitents per enviament, no per notificació!!--> Posar reintents de notificació després del for

	private void registrarEnviament(NotificacioEnviamentEntity enviament, long t0, IntegracioInfo info, boolean isComunicacio, String codiDir3, boolean totsAdministracio) throws RegistrePluginException {

		var notificacioEntity = enviament.getNotificacio();
		AccioParam accio;
		var msg = "";
		var isSirActivat = isSirActivat();
		if (isSirActivat) {
			accio = new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] " + notificacioEntity.getEnviamentTipus() + ": nou assentament registral + Notifica de la notificació: " + notificacioEntity.getId());
			msg = " [TIMER-REG-NOT] (Sir activat) Creació assentament registrals per notificació [Id: " + notificacioEntity.getId() + "]: ";
		} else {
			//### ASSENTAMENT REGISTRE NORMAL + NOTIFIC@
			log.info(" [REG-NOT] Assentament sortida (registre) + Notifica");
			accio = new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Realitzant nou assentament registral normal de la notificació: " + notificacioEntity.getId());
			msg = " [TIMER-REG-NOT] Creació assentament registrals normal per notificació [Id: " + notificacioEntity.getId() + "]: ";
		}
		info.getParams().add(accio);
		long startTime = System.nanoTime();
		double elapsedTime;
		// Registre SIR
		if (isSirActivat && isComunicacio && totsAdministracio) {
			log.info(" [REG-NOT] Realitzant nou assentament registral per SIR");
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Realitzant nou assentament registral per SIR"));
//			crearAssentamentRegistralEnviamentComunicacioSIR(notificacioEntity, codiDir3, totsAdministracio, enviament, info, t0);
			crearAssentamentRegistral(notificacioEntity, enviament, true, isComunicacio, codiDir3, info, t0, true, totsAdministracio);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			var txt = " [TIMER-REG-NOT] (Sir activat) Creació assentament registrals d'enviament de comunicació [NotId: ";
			log.info(txt + notificacioEntity.getId() + ", encId: " + enviament.getId()+ "]: " + elapsedTime + " ms");
			return;
		}
		// Registre NO SIR
//		crearAssentamentRegistralPerNotificacio(notificacioEntity, codiDir3, isComunicacio, isSirActivat, info, t0, enviament);
		crearAssentamentRegistral(notificacioEntity, enviament, isSirActivat, isComunicacio, codiDir3, info, t0, false, totsAdministracio);
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(msg + elapsedTime + " ms");
		log.info(" [REG-NOT] Fi procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
	}

	private void crearAssentamentRegistral(NotificacioEntity not, NotificacioEnviamentEntity env, boolean isSirActivat, boolean isComunicacio,
										   String dir3Codi, IntegracioInfo info, long t0, boolean isComSir, boolean totsAdministracio) {

		log.info(" >>> Nou assentament registral...");
		RespostaConsultaRegistre arbResposta;
		try {
			var inclouDocuments = isInclouDocuments(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(not));
			var generarJustificant = isGenerarJustificant(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(not));
			Set<NotificacioEnviamentEntity> enviamentSet = new HashSet<>();
			enviamentSet.add(env);
			// Es comunicacio SIR: Este método crearAssentamentRegistralPerEnviament solo se llama para comunicaciones SIR.
			var arb = isComSir ? pluginHelper.notificacioToAsientoRegistralBean(not, env, inclouDocuments, true)
						: pluginHelper.notificacioEnviamentsToAsientoRegistralBean(not, enviamentSet, inclouDocuments);
			var op = isComSir ? 2L : isSirActivat ? (isComunicacio ? 2L : 1L) : null; //### [SIR-DESACTIVAT = registre normal, SIR-ACTIVAT = notificació/comunicació]
			arbResposta = pluginHelper.crearAsientoRegistral(dir3Codi, arb, op, not.getId(), env.getId() + "", generarJustificant);
		} catch (Exception ex) {
			arbResposta = new RespostaConsultaRegistre();
			arbResposta.setErrorCodi("ERROR");
			arbResposta.setErrorDescripcio(ex.getMessage());
		}
		//Registrar event
		var error = false;
		String errorDescripcio = null;
		var errorMaxReintents = false;
		if(arbResposta.getErrorCodi() != null) {
			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			error = true;
			errorDescripcio = getErrorDescripcio(arbResposta.getErrorCodi(), arbResposta.getErrorDescripcio(), not.getRegistreEnviamentIntent());
			errorMaxReintents = not.getRegistreEnviamentIntent() >= pluginHelper.getRegistreReintentsMaxProperty();
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			log.info(" >>> ... OK");
			finalitzaRegistre(arbResposta, env, isComSir ? totsAdministracio : false);
			//Comunicació + administració (SIR)
			if (isComSir && totsAdministracio) {
				log.debug("Comunicació SIR --> actualitzar estat...");
				not.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				registreHelper.enviamentUpdateDatat(arbResposta.getEstat(), arbResposta.getRegistreData(), arbResposta.getSirRecepecioData(),
					arbResposta.getSirRegistreDestiData(), arbResposta.getRegistreNumeroFormatat(), env);
			} else {
				info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " Procedim a enviar la notificació a Notific@"));
			}
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
		}
		if (isComSir) {
			notificacioEventHelper.addSirEnviamentEvent(env, error, errorDescripcio, errorMaxReintents);
		} else {
			notificacioEventHelper.addRegistreEnviamentEvent(env, error, errorDescripcio, errorMaxReintents);
		}
		callbackHelper.crearCallback(not, env, error, errorDescripcio);
	}

//	private void crearAssentamentRegistralPerNotificacio(NotificacioEntity notificacioEntity, String dir3Codi, boolean isComunicacio, boolean isSirActivat,
//														IntegracioInfo info, long t0, NotificacioEnviamentEntity enviament){
//
//		//Crea assentament registral + Notific@
//		log.info(" >>> Nou assentament registral...");
//		RespostaConsultaRegistre arbResposta;
//		try {
//			var inclouDocuments = isInclouDocuments(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(notificacioEntity));
//			var generarJustificant = isGenerarJustificant(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(notificacioEntity));
//			Set<NotificacioEnviamentEntity> enviamentSet = new HashSet<>();
//			enviamentSet.add(enviament);
//			var arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(notificacioEntity, enviamentSet, inclouDocuments);
//			var op = isSirActivat ? (isComunicacio ? 2L : 1L) : null; //### [SIR-DESACTIVAT = registre normal, SIR-ACTIVAT = notificació/comunicació]
//			arbResposta = pluginHelper.crearAsientoRegistral(dir3Codi, arb, op, notificacioEntity.getId(), enviament.getId() + "", generarJustificant);
//		} catch (Exception e) {
//			arbResposta = new RespostaConsultaRegistre();
//			arbResposta.setErrorCodi("ERROR");
//			arbResposta.setErrorDescripcio(e.getMessage());
//		}
//		//Registrar event
//		var error = false;
//		String errorDescripcio = null;
//		var errorMaxReintents = false;
//		if(arbResposta.getErrorCodi() != null) {
//			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
//			error = true;
//			errorDescripcio = getErrorDescripcio(arbResposta.getErrorCodi(), arbResposta.getErrorDescripcio(), notificacioEntity.getRegistreEnviamentIntent());
//			errorMaxReintents = notificacioEntity.getRegistreEnviamentIntent() >= pluginHelper.getRegistreReintentsMaxProperty();
//			var t1 = System.currentTimeMillis();
//			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
//		} else {
//			log.info(" >>> ... OK");
//			finalitzaRegistre(arbResposta, enviament, false);
//			var t1 = System.currentTimeMillis();
//			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
//			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " Procedim a enviar la notificació a Notific@"));
//		}
//		notificacioEventHelper.addRegistreEnviamentEvent(enviament, error, errorDescripcio, errorMaxReintents);
//		callbackHelper.crearCallback(notificacioEntity, enviament, error, errorDescripcio);
//	}
//
//	private void crearAssentamentRegistralEnviamentComunicacioSIR(NotificacioEntity notificacioEntity, String dir3Codi, boolean totsAdministracio,
//															NotificacioEnviamentEntity enviament, IntegracioInfo info, long t0) throws RegistrePluginException {
//
//		log.info(" >>> Nou assentament registral SIR...");
//		RespostaConsultaRegistre arbResposta;
//		try {
//			var generarJustificant =  isGenerarJustificant(true, true, isAnyEnviamentsAAdministracio(notificacioEntity));
//			var inclouDocuments = isInclouDocuments(true, true, enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO));
//			var arb = pluginHelper.notificacioToAsientoRegistralBean( notificacioEntity, enviament, inclouDocuments, true); // Es comunicacio SIR: Este método crearAssentamentRegistralPerEnviament solo se llama para comunicaciones SIR.
//			arbResposta = pluginHelper.crearAsientoRegistral(dir3Codi, arb,2L, notificacioEntity.getId(), String.valueOf(enviament.getId()), generarJustificant);
//		} catch (Exception e) {
//			arbResposta = new RespostaConsultaRegistre();
//			arbResposta.setErrorCodi("ERROR");
//			arbResposta.setErrorDescripcio(e.getMessage());
//		}
//		//Registrar event
//		var error = false;
//		String errorDescripcio = null;
//		var errorMaxReintents = false;
//		if(arbResposta.getErrorCodi() != null) {
//			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
//			error = true;
//			errorDescripcio = getErrorDescripcio(arbResposta.getErrorCodi(), arbResposta.getErrorDescripcio(), notificacioEntity.getRegistreEnviamentIntent());
//			errorMaxReintents = notificacioEntity.getRegistreEnviamentIntent() >= pluginHelper.getRegistreReintentsMaxProperty();
//			var t1 = System.currentTimeMillis();
//			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Hi ha hagut un error realitzant el procés de registre " +
//					"(temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
//		} else {
//			log.info(" >>> ... OK");
//			finalitzaRegistre(arbResposta, enviament, totsAdministracio);
//			//Comunicació + administració (SIR)
//			if (totsAdministracio) {
//				log.debug("Comunicació SIR --> actualitzar estat...");
//				notificacioEntity.updateEstat(NotificacioEstatEnumDto.ENVIADA);
//				registreHelper.enviamentUpdateDatat(arbResposta.getEstat(), arbResposta.getRegistreData(), arbResposta.getSirRecepecioData(),
//													arbResposta.getSirRegistreDestiData(), arbResposta.getRegistreNumeroFormatat(), enviament);
//			}
//			long t1 = System.currentTimeMillis();
//			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
//		}
//		notificacioEventHelper.addSirEnviamentEvent(enviament, error, errorDescripcio, errorMaxReintents);
//		callbackHelper.crearCallback(notificacioEntity, enviament, error, errorDescripcio);
//	}

	private String getErrorDescripcio(String codi, String descripcio, int intent) {

		var errorDescripcio = "intent " + intent + ": \n\n";
		errorDescripcio += "Codi error: " + (codi != null ? codi : "Codi no proporcionat") + "\n";
		errorDescripcio += descripcio != null ? descripcio : "El registre no aporta cap descripció de l'error";
		return errorDescripcio;
	}

	private boolean isGenerarJustificant(boolean isComunicacio, boolean isSirActivat, boolean aAdministracio) {
		return isGenerarJustificantActive() || (isComunicacio && isSirActivat && aAdministracio);
	}

	private boolean isInclouDocuments(boolean isComunicacio, boolean isSirActivat, boolean aAdministracio) {
		return isSendDocumentsActive() || (isSirActivat && isComunicacio && aAdministracio);
	}

	private boolean isAllEnviamentsAAdministracio(NotificacioEntity notificacioEntity) {

		for (var enviament : notificacioEntity.getEnviaments()) {
			if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return false;
			}
		}
		return true;
	}

	private boolean isAnyEnviamentsAAdministracio(NotificacioEntity notificacioEntity) {

		for (var enviament : notificacioEntity.getEnviaments()) {
			if(enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return true;
			}
		}
		return false;
	}
	
	private void finalitzaRegistre(RespostaConsultaRegistre arbResposta, NotificacioEnviamentEntity enviament, boolean totsAdministracio) {

		if (arbResposta == null) {
			return;
		}
		var registreNum = arbResposta.getRegistreNumeroFormatat();
		var registreData = arbResposta.getRegistreData();
		var registreEstat = arbResposta.getEstat();
		enviament.setRegistreNumeroFormatat(registreNum);
		enviament.setRegistreData(registreData);
		enviament.updateRegistreEstat(registreEstat);
		//Comunicació + administració (SIR)
		if (totsAdministracio) {
			enviament.setNotificaEstat(EnviamentEstat.ENVIAT_SIR);
		}
		enviamentTableHelper.actualitzarRegistre(enviament);
		auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "RegistreNotificaHelper.realitzarProcesRegistrar");
	}

	private boolean enviamentsRegistrats(Set<NotificacioEnviamentEntity> enviaments) {

		for (var enviament : enviaments) {
			if (enviament.getRegistreData() == null) {
				return false;
			}
		}
		return true;
	}

	private boolean isSirActivat() {
		return configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir");
	}

	/**
	 * Indica si els documents s'han d'enviar al registre.
	 * Si es true els documents sempre s'han d'enviar.
	 *
	 * @return boolean
	 */
	public boolean isSendDocumentsActive() {
		return configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.documents.enviar");
	}

	/**
	 * Indica si s'ha de generar el justificant del registre de totes les notificacions.
	 * Si es false només es generen per a comunicacions a administracions (enviaments SIR)
	 *
	 * @return boolean
	 */
	private boolean isGenerarJustificantActive() {
		return configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.generar.justificant");
	}
	
}
