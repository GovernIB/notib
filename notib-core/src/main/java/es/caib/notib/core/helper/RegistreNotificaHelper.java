/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

		logger.info(" [REG-NOT] Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		configHelper.setEntitatCodi(notificacioEntity.getEntitat().getCodi());
		boolean enviarANotifica = false;
		boolean isComunicacio = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioEntity.getEnviamentTipus());
		long t0 = System.currentTimeMillis();
		String desc = "Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]";
		AccioParam tipusEnv = new AccioParam("Tipus enviament: ", notificacioEntity.getEnviamentTipus().name());
		AccioParam sirActivat = new AccioParam("Sir activat", String.valueOf(isSirActivat()));
		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_REGISTRE, desc, IntegracioAccioTipusEnumDto.ENVIAMENT, tipusEnv, sirActivat);
		info.setCodiEntitat(notificacioEntity.getEntitat().getCodi());
		String codiDir3 = notificacioEntity.getEntitat().getDir3CodiReg() != null  && !notificacioEntity.getEntitat().getDir3CodiReg().isEmpty() ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi();
		boolean totsAdministracio = isAllEnviamentsAAdministracio(notificacioEntity);
		long startTime;
		double elapsedTime;

		for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments() ) {
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
					logger.info(" [REG-NOT] Assentament sortida (registre) + Notifica");
					accio = new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral normal de la notificació: " + notificacioEntity.getId());
					msg = " [TIMER-REG-NOT] Creació assentament registrals normal per notificació [Id: " + notificacioEntity.getId() + "]: ";
				}
				info.getParams().add(accio);

				// Registre SIR
				if (isSirActivat() && isComunicacio && totsAdministracio) {
					logger.info(" [REG-NOT] Realitzant nou assentament registral per SIR");
					info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral per SIR"));
					crearAssentamentRegistralEnviamentComunicacioSIR(notificacioEntity, codiDir3, totsAdministracio, enviament, info, t0);
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					logger.info(" [TIMER-REG-NOT] (Sir activat) Creació assentament registrals d'enviament de comunicació [NotId: " +
							notificacioEntity.getId() + ", encId: " + enviament.getId()+ "]: " + elapsedTime + " ms");
					continue;
				}

				// Registre NO SIR
//				boolean ok =
				crearAssentamentRegistralPerNotificacio(notificacioEntity, codiDir3, enviarANotifica, isComunicacio, isSirActivat(), info, t0, enviament);
//				enviarANotifica = enviarANotifica && ok;

				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(msg + elapsedTime + " ms");
				logger.info(" [REG-NOT] Fi procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
			} catch (Exception ex) {
				String errorDescripcio = "Hi ha hagut un error registrant l'enviament + " + enviament.getId();
				logger.error(errorDescripcio, ex);
				integracioHelper.addAccioError(info, errorDescripcio, ex);
				throw new RegistreNotificaException(ex.getMessage(), ex);
			}
		}

		if (enviamentsRegistrats(notificacioEntity.getEnviaments())) {
			boolean isSir = notificacioEntity.isComunicacioSir();
			notificacioEntity.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIADA : NotificacioEstatEnumDto.REGISTRADA);
			notificacioTableHelper.actualitzarRegistre(notificacioEntity);
			enviarANotifica = !isSir;
		}
		integracioHelper.addAccioOk(info);
		return enviarANotifica;
	}

	private boolean crearAssentamentRegistralPerNotificacio(NotificacioEntity notificacioEntity, String dir3Codi, boolean enviarANotifica,
															boolean isComunicacio, boolean isSirActivat, IntegracioInfo info, long t0,
															NotificacioEnviamentEntity enviament) throws RegistrePluginException {

		//Crea assentament registral + Notific@
		logger.info(" >>> Nou assentament registral...");
		RespostaConsultaRegistre arbResposta;
		try {
			boolean inclouDocuments = isInclouDocuments(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(notificacioEntity));
			boolean generarJustificant = isGenerarJustificant(isComunicacio, isSirActivat, isAnyEnviamentsAAdministracio(notificacioEntity));
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
			logger.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			updateEventWithError(arbResposta, notificacioEntity, null);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			logger.info(" >>> ... OK");
			finalitzaRegistre(arbResposta, notificacioEntity, new HashSet<>(Arrays.asList(enviament)), false);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
			info.getParams().add(new AccioParam("Procés descripció: ", " Procedim a enviar la notificació a Notific@"));
			enviarANotifica = true;
		}
		auditNotificacioHelper.updateRegistreNouEnviament(notificacioEntity, pluginHelper.getRegistreReintentsPeriodeProperty());
		return enviarANotifica;
	}

	private void crearAssentamentRegistralEnviamentComunicacioSIR(NotificacioEntity notificacioEntity, String dir3Codi, boolean totsAdministracio,
															NotificacioEnviamentEntity enviament, IntegracioInfo info, long t0) throws RegistrePluginException {

		logger.info(" >>> Nou assentament registral SIR...");
		RespostaConsultaRegistre arbResposta;
		try {
			boolean generarJustificant =  isGenerarJustificant(true, true, isAnyEnviamentsAAdministracio(notificacioEntity));
			boolean inclouDocuments = isInclouDocuments(true, true, enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO));
			AsientoRegistralBeanDto arb = pluginHelper.notificacioToAsientoRegistralBean( notificacioEntity, enviament, inclouDocuments, true); // Es comunicacio SIR: Este método crearAssentamentRegistralPerEnviament solo se llama para comunicaciones SIR.
			arbResposta = pluginHelper.crearAsientoRegistral(dir3Codi, arb,2L, notificacioEntity.getId(), String.valueOf(enviament.getId()), generarJustificant);
		} catch (Exception e) {
			arbResposta = new RespostaConsultaRegistre();
			arbResposta.setErrorCodi("ERROR");
			arbResposta.setErrorDescripcio(e.getMessage());
		}
		//Registrar event
		if(arbResposta.getErrorCodi() != null) {
			logger.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			updateEventWithError(arbResposta, notificacioEntity, enviament);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre " +
					"(temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			logger.info(" >>> ... OK");
			finalitzaRegistre(arbResposta, notificacioEntity, new HashSet<>(Arrays.asList(enviament)), totsAdministracio);

			//Comunicació + administració (SIR)
			if (totsAdministracio) {
				logger.debug("Comunicació SIR --> actualitzar estat...");
				auditNotificacioHelper.updateNotificacioEnviada(notificacioEntity);
				registreHelper.enviamentUpdateDatat(arbResposta.getEstat(), arbResposta.getRegistreData(), arbResposta.getSirRecepecioData(),
													arbResposta.getSirRegistreDestiData(), arbResposta.getRegistreNumeroFormatat(), enviament);
			}
			long t1 = System.currentTimeMillis();
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

		for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
			if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
				return false;
			}
		}
		return true;
	}

	private boolean isAnyEnviamentsAAdministracio(NotificacioEntity notificacioEntity) {

		for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
			if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
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
		String registreNum = arbResposta.getRegistreNumeroFormatat();
		Date registreData = arbResposta.getRegistreData();
		NotificacioRegistreEstatEnumDto registreEstat = arbResposta.getEstat();
		//Crea un nou event
		notificacioEventHelper.addEnviamentRegistreOKEvent(notificacioEntity, registreNum, registreData, registreEstat, enviaments, totsAdministracio);
	}

	private boolean enviamentsRegistrats(Set<NotificacioEnviamentEntity> enviaments) {
		Iterator<NotificacioEnviamentEntity> it = enviaments.iterator();
		while (it.hasNext()) {
			if (it.next().getRegistreData() == null) {
				return false;
			}
		}
		return true;
	}

	private boolean isSirActivat() {
		return configHelper.getAsBoolean("es.caib.notib.emprar.sir");
	}

	/**
	 * Indica si els documents s'han d'enviar al registre.
	 * Si es true els documents sempre s'han d'enviar.
	 *
	 * @return boolean
	 */
	public boolean isSendDocumentsActive() {
		return configHelper.getAsBoolean("es.caib.notib.plugin.registre.documents.enviar");
	}

	/**
	 * Indica si s'ha de generar el justificant del registre de totes les notificacions.
	 * Si es false només es generen per a comunicacions a administracions (enviaments SIR)
	 *
	 * @return boolean
	 */
	private boolean isGenerarJustificantActive() {
		return configHelper.getAsBoolean("es.caib.notib.plugin.registre.generar.justificant");
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreNotificaHelper.class);

}
