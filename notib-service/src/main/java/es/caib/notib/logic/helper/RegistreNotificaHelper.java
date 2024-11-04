/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.objectes.AssentamentRegistralParams;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
		var isComunicacio = !EnviamentTipus.NOTIFICACIO.equals(notificacioEntity.getEnviamentTipus());
		var isSir = notificacioEntity.isComunicacioSir();
		var t0 = System.currentTimeMillis();
		var desc = "Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]";
		var tipusEnv = new AccioParam("Tipus enviament: ", notificacioEntity.getEnviamentTipus().name());
		var sirActivat = new AccioParam("Sir activat", String.valueOf(isSirActivat()));
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, desc, IntegracioAccioTipusEnumDto.ENVIAMENT, tipusEnv, sirActivat);
		info.setCodiEntitat(notificacioEntity.getEntitat().getCodi());
		var codiDir3 = notificacioEntity.getEntitat().getDir3CodiReg() != null  && !notificacioEntity.getEntitat().getDir3CodiReg().isEmpty() ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi();
		var totsAdministracio = isAllEnviamentsAAdministracio(notificacioEntity);
		notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
		AssentamentRegistralParams params;
		var isSirActivat = isSirActivat();
		var maxReintents = pluginHelper.getRegistreReintentsMaxProperty();
		for(var enviament : notificacioEntity.getEnviaments() ) {
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Realitzant nou assentament registral de l'enviament: " + enviament.getId()));
			if (enviament.getRegistreData() != null) {
				continue;
			}
			try {
				params = AssentamentRegistralParams.builder()
						.not(notificacioEntity)
						.env(enviament)
						.isSirActivat(isSirActivat).t0(t0).info(info)
						.isComunicacio(isComunicacio)
						.isComSir(isSir)
						.dir3Codi(codiDir3)
						.totsAdministracio(totsAdministracio)
						.errorMaxReintentsProperty(maxReintents).build();
				registrarEnviament(params);
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
			notificacioEntity.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIADA : NotificacioEstatEnumDto.REGISTRADA);
			enviarANotifica = !isSir;
		}
		notificacioTableHelper.actualitzarRegistre(notificacioEntity);
		auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.UPDATE, "RegistreNotificaHelper.realitzarProcesRegistrar");
		integracioHelper.addAccioOk(info);
		return enviarANotifica;
	}
	// TODO: Reinitents per enviament, no per notificació!!--> Posar reintents de notificació després del for

	private void registrarEnviament(AssentamentRegistralParams params) throws RegistrePluginException {

		var notificacioEntity = params.getNot();
		AccioParam accio;
		var msg = "";
		params.setSirActivat(isSirActivat());
		if (params.isSirActivat()) {
			accio = new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] " + notificacioEntity.getEnviamentTipus() + ": nou assentament registral + Notifica de la notificació: " + notificacioEntity.getId());
			msg = " [TIMER-REG-NOT] (Sir activat) Creació assentament registrals per notificació [Id: " + notificacioEntity.getId() + "]: ";
		} else {
			//### ASSENTAMENT REGISTRE NORMAL + NOTIFIC@
			log.info(" [REG-NOT] Assentament sortida (registre) + Notifica");
			accio = new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Realitzant nou assentament registral normal de la notificació: " + notificacioEntity.getId());
			msg = " [TIMER-REG-NOT] Creació assentament registrals normal per notificació [Id: " + notificacioEntity.getId() + "]: ";
		}
		params.getInfo().getParams().add(accio);
		long startTime = System.nanoTime();
		double elapsedTime;
		// Registre SIR
		if (params.isSirActivat() && params.isComSir() && params.isTotsAdministracio()) {
			log.info(" [REG-NOT] Realitzant nou assentament registral per SIR");
			params.getInfo().getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Realitzant nou assentament registral per SIR"));
			crearAssentamentRegistral(params);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			var txt = " [TIMER-REG-NOT] (Sir activat) Creació assentament registrals d'enviament de comunicació [NotId: ";
			log.info(txt + notificacioEntity.getId() + ", encId: " + params.getEnv().getId()+ "]: " + elapsedTime + " ms");
			return;
		}
		// Registre NO SIR
		params.setComSir(false);
		crearAssentamentRegistral(params);
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(msg + elapsedTime + " ms");
		log.info(" [REG-NOT] Fi procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
	}

	private void crearAssentamentRegistral(AssentamentRegistralParams params) {

		log.info(" >>> Nou assentament registral...");
		var not = params.getNot();
		var env = params.getEnv();
		var isSirActivat = params.isSirActivat();
		RespostaConsultaRegistre arbResposta;
		var isComSir = params.isComSir();
		try {

			var inclouDocuments = isInclouDocuments(params.isComunicacio(), params.isSirActivat(), isAnyEnviamentsAAdministracio(params.getNot()));
			var generarJustificant = isGenerarJustificant(params.isComunicacio(), isSirActivat, isAnyEnviamentsAAdministracio(not));
			Set<NotificacioEnviamentEntity> enviamentSet = new HashSet<>();
			enviamentSet.add(env);
			// Es comunicacio SIR: Este método crearAssentamentRegistralPerEnviament solo se llama para comunicaciones SIR.
			var arb = isComSir ? pluginHelper.notificacioToAsientoRegistralBean(not, env, inclouDocuments, true)
						: pluginHelper.notificacioEnviamentsToAsientoRegistralBean(not, enviamentSet, inclouDocuments);
			var op = isComSir ? 2L : isSirActivat ? (params.isComunicacio() ? 2L : 1L) : null; //### [SIR-DESACTIVAT = registre normal, SIR-ACTIVAT = notificació/comunicació]
			arbResposta = pluginHelper.crearAsientoRegistral(params.getDir3Codi(), arb, op, not.getId(), env.getId() + "", generarJustificant);
		} catch (Exception ex) {
			arbResposta = new RespostaConsultaRegistre();
			arbResposta.setErrorCodi("ERROR");
			arbResposta.setErrorDescripcio(ex.getMessage());
		}
		params.setArbResposta(arbResposta);
		processarRespostaAsientoRegistral(params);
		var data = arbResposta.getRegistreData() != null ? arbResposta.getRegistreData() : new Date();
		var eventInfo = NotificacioEventHelper.EventInfo.builder().data(data).enviament(env).error(params.isError()).errorDescripcio(params.getErrorDescripcio()).fiReintents(params.isErrorMaxReintents()).build();

		if (isComSir) {
//			notificacioEventHelper.addSirEnviamentEvent(env, params.isError(), params.getErrorDescripcio(), params.isErrorMaxReintents());
			notificacioEventHelper.addSirEnviamentEvent(eventInfo);
		} else {
//			notificacioEventHelper.addRegistreEnviamentEvent(env, params.isError(), params.getErrorDescripcio(), params.isErrorMaxReintents());
			notificacioEventHelper.addRegistreEnviamentEvent(eventInfo);
		}
		callbackHelper.crearCallback(not, env, params.isError(), params.getErrorDescripcio());
	}

	private void processarRespostaAsientoRegistral(AssentamentRegistralParams params) {

		var not = params.getNot();
		var env = params.getEnv();
		var info = params.getInfo();
		var arbResposta = params.getArbResposta();
		if(arbResposta.getErrorCodi() != null) {
			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			params.setError(true);
			params.setErrorDescripcio(getErrorDescripcio(arbResposta.getErrorCodi(), arbResposta.getErrorDescripcio()));
			params.setErrorMaxReintents(not.getRegistreEnviamentIntent() >= params.getErrorMaxReintentsProperty());
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - params.getT0()) + "ms): " + arbResposta.getErrorDescripcio()));
			return;
		}
		log.info(" >>> ... OK");
		var totsAdministracio = params.isTotsAdministracio();
		finalitzaRegistre(arbResposta, env, params.isComSir() && totsAdministracio);
		//Comunicació + administració (SIR)
		if (params.isComSir() && totsAdministracio) {
			log.debug("Comunicació SIR --> actualitzar estat...");
			not.updateEstat(NotificacioEstatEnumDto.ENVIAT_SIR);
			registreHelper.enviamentUpdateDatat(arbResposta.getEstat(), arbResposta.getRegistreData(), arbResposta.getSirRecepecioData(),
					arbResposta.getSirRegistreDestiData(), arbResposta.getRegistreNumeroFormatat(), env);
		} else {
			info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " Procedim a enviar la notificació a Notific@"));
		}
		var t1 = System.currentTimeMillis();
		info.getParams().add(new AccioParam(PROCES_DESC_PARAM, " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - params.getT0()) + "ms)"));
	}

	private String getErrorDescripcio(String codi, String descripcio) {

		var errorDescripcio = "Codi error: " + (codi != null ? codi : "Codi no proporcionat") + "\n";
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
