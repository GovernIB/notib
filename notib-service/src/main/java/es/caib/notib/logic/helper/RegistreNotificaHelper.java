/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
//	@Autowired
//	private NotificacioMassivaHelper notificacioMassivaHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;


	public boolean realitzarProcesRegistrar(NotificacioEntity notificacioEntity) throws RegistreNotificaException {

		log.info(" [REG-NOT] Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		if (notificacioEntity == null || notificacioEntity.getEntitat() == null) {
			log.error("Error realitzant proces de registrar.");
			return false;
		}
		configHelper.setEntitatCodi(notificacioEntity.getEntitat().getCodi());
		var enviarANotifica = false;
		var isComunicacio = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioEntity.getEnviamentTipus());
		long t0 = System.currentTimeMillis();
		var desc = "Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]";
		var tipusEnv = new AccioParam("Tipus enviament: ", notificacioEntity.getEnviamentTipus().name());
		var sirActivat = new AccioParam("Sir activat", String.valueOf(isSirActivat()));
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, desc, IntegracioAccioTipusEnumDto.ENVIAMENT, tipusEnv, sirActivat);
		info.setCodiEntitat(notificacioEntity.getEntitat().getCodi());
		var codiDir3 = notificacioEntity.getEntitat().getDir3CodiReg() != null  && !notificacioEntity.getEntitat().getDir3CodiReg().isEmpty() ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi();
		var totsAdministracio = isAllEnviamentsAAdministracio(notificacioEntity);
		long startTime;
		double elapsedTime;

		for(var enviament : notificacioEntity.getEnviaments() ) {
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral de l'enviament: " + enviament.getId()));
			startTime = System.nanoTime();
			if (enviament.getRegistreData() != null) {
				continue;
			}
			try {
				AccioParam accio;
				String msg = "";
				if (isSirActivat()) {
					accio = new AccioParam("Procés descripció: ", " [REG-NOT] " + notificacioEntity.getEnviamentTipus() + ": nou assentament registral + Notifica de la notificació: " + notificacioEntity.getId());
					msg = " [TIMER-REG-NOT] (Sir activat) Creació assentament registrals per notificació [Id: " + notificacioEntity.getId() + "]: ";
				} else {
					//### ASSENTAMENT REGISTRE NORMAL + NOTIFIC@
					log.info(" [REG-NOT] Assentament sortida (registre) + Notifica");
					accio = new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral normal de la notificació: " + notificacioEntity.getId());
					msg = " [TIMER-REG-NOT] Creació assentament registrals normal per notificació [Id: " + notificacioEntity.getId() + "]: ";
				}
				info.getParams().add(accio);
				// Registre SIR
				if (isSirActivat() && isComunicacio && totsAdministracio) {
					log.info(" [REG-NOT] Realitzant nou assentament registral per SIR");
					info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral per SIR"));
					crearAssentamentRegistralEnviamentComunicacioSIR(notificacioEntity, codiDir3, totsAdministracio, enviament, info, t0);
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					log.info(" [TIMER-REG-NOT] (Sir activat) Creació assentament registrals d'enviament de comunicació [NotId: " +
							notificacioEntity.getId() + ", encId: " + enviament.getId()+ "]: " + elapsedTime + " ms");
					continue;
				}

				// Registre NO SIR
				crearAssentamentRegistralPerNotificacio(notificacioEntity, codiDir3, isComunicacio, isSirActivat(), info, t0, enviament);

				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				log.info(msg + elapsedTime + " ms");
				log.info(" [REG-NOT] Fi procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
			} catch (Exception ex) {
				var errorDescripcio = "Hi ha hagut un error registrant l'enviament + " + enviament.getId();
				log.error(errorDescripcio, ex);
				integracioHelper.addAccioError(info, errorDescripcio, ex);
				throw new RegistreNotificaException(ex.getMessage(), ex);
			}
		}
		if (enviamentsRegistrats(notificacioEntity.getEnviaments())) {
			var isSir = notificacioEntity.isComunicacioSir();
			notificacioEntity.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIADA : NotificacioEstatEnumDto.REGISTRADA);
			notificacioTableHelper.actualitzarRegistre(notificacioEntity);
			enviarANotifica = !isSir;
		}
		integracioHelper.addAccioOk(info);
		return enviarANotifica;
	}

	private void crearAssentamentRegistralPerNotificacio(NotificacioEntity notificacioEntity, String dir3Codi, boolean isComunicacio, boolean isSirActivat,
														IntegracioInfo info, long t0, NotificacioEnviamentEntity enviament) throws RegistrePluginException {

		//Crea assentament registral + Notific@
		log.info(" >>> Nou assentament registral...");
		RespostaConsultaRegistre arbResposta;
		try {
			var inclouDocuments = isInclouDocuments(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(notificacioEntity));
			var generarJustificant = isGenerarJustificant(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(notificacioEntity));
			Set<NotificacioEnviamentEntity> enviamentSet = new HashSet<>();
			enviamentSet.add(enviament);
			AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(notificacioEntity, enviamentSet, inclouDocuments);
			Long op = isSirActivat ? (isComunicacio ? 2L : 1L) : null; //### [SIR-DESACTIVAT = registre normal, SIR-ACTIVAT = notificació/comunicació]
			arbResposta = pluginHelper.crearAsientoRegistral(dir3Codi, arb, op, notificacioEntity.getId(), enviament.getId() + "", generarJustificant);
		} catch (Exception e) {
			arbResposta = new RespostaConsultaRegistre();
			arbResposta.setErrorCodi("ERROR");
			arbResposta.setErrorDescripcio(e.getMessage());
		}
		//Registrar event
		if(arbResposta.getErrorCodi() != null) {
			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			updateEventWithError(arbResposta, notificacioEntity, null);
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			log.info(" >>> ... OK");
			finalitzaRegistre(arbResposta, notificacioEntity, new HashSet<>(Arrays.asList(enviament)), false);
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
			info.getParams().add(new AccioParam("Procés descripció: ", " Procedim a enviar la notificació a Notific@"));
		}
		auditNotificacioHelper.updateRegistreNouEnviament(notificacioEntity, pluginHelper.getRegistreReintentsPeriodeProperty());
	}

	private void crearAssentamentRegistralEnviamentComunicacioSIR(NotificacioEntity notificacioEntity, String dir3Codi, boolean totsAdministracio,
															NotificacioEnviamentEntity enviament, IntegracioInfo info, long t0) throws RegistrePluginException {

		log.info(" >>> Nou assentament registral SIR...");
		RespostaConsultaRegistre arbResposta;
		try {
			var generarJustificant =  isGenerarJustificant(true, true, isAnyEnviamentsAAdministracio(notificacioEntity));
			var inclouDocuments = isInclouDocuments(true, true, enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO));
			var arb = pluginHelper.notificacioToAsientoRegistralBean( notificacioEntity, enviament, inclouDocuments, true); // Es comunicacio SIR: Este método crearAssentamentRegistralPerEnviament solo se llama para comunicaciones SIR.
			arbResposta = pluginHelper.crearAsientoRegistral(dir3Codi, arb,2L, notificacioEntity.getId(), String.valueOf(enviament.getId()), generarJustificant);
		} catch (Exception e) {
			arbResposta = new RespostaConsultaRegistre();
			arbResposta.setErrorCodi("ERROR");
			arbResposta.setErrorDescripcio(e.getMessage());
		}
		//Registrar event
		if(arbResposta.getErrorCodi() != null) {
			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			updateEventWithError(arbResposta, notificacioEntity, enviament);
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre " +
					"(temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			log.info(" >>> ... OK");
			finalitzaRegistre(arbResposta, notificacioEntity, new HashSet<>(Arrays.asList(enviament)), totsAdministracio);
			//Comunicació + administració (SIR)
			if (totsAdministracio) {
				log.debug("Comunicació SIR --> actualitzar estat...");
				auditNotificacioHelper.updateNotificacioEnviada(notificacioEntity);
				registreHelper.enviamentUpdateDatat(arbResposta.getEstat(), arbResposta.getRegistreData(), arbResposta.getSirRecepecioData(),
													arbResposta.getSirRegistreDestiData(), arbResposta.getRegistreNumeroFormatat(), enviament);
			}
			var t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
		}
		auditNotificacioHelper.updateRegistreNouEnviament(notificacioEntity, pluginHelper.getRegistreReintentsPeriodeProperty());
	}

	private boolean isGenerarJustificant(boolean isComunicacio, boolean isSirActivat, boolean aAdministracio) {
		return isGenerarJustificantActive() || (isComunicacio && isSirActivat && aAdministracio);
	}

	private boolean isInclouDocuments(boolean isComunicacio, boolean isSirActivat, boolean aAdministracio) {
		return isSendDocumentsActive() || (isSirActivat && isComunicacio && aAdministracio);
	}

	private boolean isAllEnviamentsAAdministracio(NotificacioEntity notificacioEntity) {

		for(var enviament : notificacioEntity.getEnviaments()) {
			if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return false;
			}
		}
		return true;
	}

	private boolean isAnyEnviamentsAAdministracio(NotificacioEntity notificacioEntity) {

		for(var enviament : notificacioEntity.getEnviaments()) {
			if(enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateEventWithError(RespostaConsultaRegistre arbResposta,NotificacioEntity notificacioEntity, NotificacioEnviamentEntity enviament) {

		String errorDescripcio = "";
		if (arbResposta != null) {
			errorDescripcio = "intent " + notificacioEntity.getRegistreEnviamentIntent() + ": \n";
			errorDescripcio += "Codi error: " + (arbResposta.getErrorCodi() != null ? arbResposta.getErrorCodi() : "Codi no proporcionat") + "\n";
			errorDescripcio += arbResposta.getErrorDescripcio() != null ? arbResposta.getErrorDescripcio() : "El registre no aporta cap descripció de l'error";
		}
		notificacioEventHelper.addNotificaRegistreEvent(notificacioEntity, enviament, errorDescripcio, NotificacioErrorTipusEnumDto.ERROR_REGISTRE);
	}

	private void finalitzaRegistre(RespostaConsultaRegistre arbResposta, NotificacioEntity notificacioEntity, Set<NotificacioEnviamentEntity> enviaments, boolean totsAdministracio) {

		if (arbResposta == null) {
			return;
		}
//		if (enviamentsRegistrats(notificacioEntity.getEnviaments())) {
//			auditNotificacioHelper.updateNotificacioRegistre(arbResposta, notificacioEntity);
//		}
//			// Actualitzar progrés notificació massiva.
//			if (notificacioEntity.getNotificacioMassivaEntity() != null) {
//				notificacioMassivaHelper.updateProgress(notificacioEntity.getNotificacioMassivaEntity().getId());
//			}
		var registreNum = arbResposta.getRegistreNumeroFormatat();
		var registreData = arbResposta.getRegistreData();
		var registreEstat = arbResposta.getEstat();
		//Crea un nou event
		notificacioEventHelper.addEnviamentRegistreOKEvent(notificacioEntity, registreNum, registreData, registreEstat, enviaments, totsAdministracio);
	}

	private boolean enviamentsRegistrats(Set<NotificacioEnviamentEntity> enviaments) {

		var it = enviaments.iterator();
		while (it.hasNext()) {
			if (it.next().getRegistreData() == null) {
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
