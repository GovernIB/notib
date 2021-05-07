/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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

	public boolean realitzarProcesRegistrar(
			NotificacioEntity notificacioEntity) throws RegistreNotificaException {
		logger.info(" [REG-NOT] Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		boolean enviarANotifica = false;
		boolean isComunicacio = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioEntity.getEnviamentTipus());
		long t0 = System.currentTimeMillis();
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_REGISTRE, 
				"Inici procés registrar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Tipus enviament: ", notificacioEntity.getEnviamentTipus().name()),
				new AccioParam("Sir activat", String.valueOf(isSirActivat())));
		
		if (isSirActivat()) {
			boolean totsAdministracio = true;
			for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
				if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
					totsAdministracio = false;
				}
			}
			long startTime;
			double elapsedTime;
//			### COMUNICACIÓ + TOT A ADMINISTRACIÓ
			if(isComunicacio && totsAdministracio) {
				logger.info(" [REG-NOT] Realitzant nou assentament registral per SIR");
				info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral per SIR"));
				startTime = System.nanoTime();
				for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
					info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral de l'enviament: " + enviament.getId()));
					try {
						//Només crea assentament registral sense notificar
						long startTime2 = System.nanoTime();
						crearAssentamentRegistralPerEnviament(
								notificacioEntity, 
								notificacioEntity.getEntitat().getDir3CodiReg() != null ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi(), 
								totsAdministracio, 
								enviament,
								info,
								t0);
						elapsedTime = (System.nanoTime() - startTime2) / 10e6;
						logger.info(" [TIMER-REG-NOT] (Sir activat) Creació assentament registrals d'enviament de comunicació [NotId: " +
								notificacioEntity.getId() + ", encId: " + enviament.getId()+ "]: " + elapsedTime + " ms");
					} catch (Exception ex) {
						String errorDescripcio = "Hi ha hagut un error registrant l'enviament + " + enviament.getId();
						logger.error(errorDescripcio, ex);
						integracioHelper.addAccioError(info, errorDescripcio, ex);
						throw new RegistreNotificaException(
								ex.getMessage(),
								ex);
					}
				}
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-REG-NOT] (Sir activat) Creació de tots els assentaments registrals de comunicació [Id: " + notificacioEntity.getId() + "]: " + elapsedTime + " ms");
			} else {
//				### COMUNICACIÓ/NOTIFICACIÓ + NOTIFIC@
				logger.info(" [REG-NOT] Comunicació: Assentament registral + Notifica");
				try {
					info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] " + notificacioEntity.getEnviamentTipus() + ": nou assentament registral + Notifica de la notificació: " + notificacioEntity.getId()));
					startTime = System.nanoTime();
					enviarANotifica = crearAssentamentRegistralPerNotificacio(
							notificacioEntity, 
							notificacioEntity.getEntitat().getDir3CodiReg() != null ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi(), 
							enviarANotifica,
							isComunicacio,
							true,
							info,
							t0);
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					logger.info(" [TIMER-REG-NOT] (Sir activat) Creació assentament registrals per notificació [Id: " + notificacioEntity.getId() + "]: " + elapsedTime + " ms");
				} catch (Exception ex) {
					String errorDescripcio = "Hi ha hagut un error registrant la notificació " + notificacioEntity.getId();
					logger.error(errorDescripcio, ex);
					integracioHelper.addAccioError(info, errorDescripcio, ex);
					throw new RegistreNotificaException(
							ex.getMessage(),
							ex);
				}
			}
		} else {
//			### ASSENTAMENT REGISTRE NORMAL + NOTIFIC@
			logger.info(" [REG-NOT] Assentament sortida (registre) + Notifica");
			try {
				info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral normal de la notificació: " + notificacioEntity.getId()));
				long startTime = System.nanoTime();
				enviarANotifica = crearAssentamentRegistralPerNotificacio(
						notificacioEntity, 
						notificacioEntity.getEntitat().getDir3CodiReg() != null ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi(), 
						enviarANotifica,
						isComunicacio,
						false,
						info,
						t0);
				double elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-REG-NOT] Creació assentament registrals normal per notificació [Id: " + notificacioEntity.getId() + "]: " + elapsedTime + " ms");
			} catch (Exception ex) {
				String errorDescripcio = "Hi ha hagut un error registrant la notificació " + notificacioEntity.getId();
				logger.error(errorDescripcio, ex);
				integracioHelper.addAccioError(info, errorDescripcio, ex);
				throw new RegistreNotificaException(
						ex.getMessage(),
						ex);
			}
			logger.info(" [REG-NOT] Fi procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		}
		integracioHelper.addAccioOk(info);
		return enviarANotifica;
	}

	private boolean crearAssentamentRegistralPerNotificacio(
			NotificacioEntity notificacioEntity, 
			String dir3Codi,
			boolean enviarANotifica,
			boolean isComunicacio,
			boolean isSirActivat,
			IntegracioInfo info,
			long t0) throws RegistrePluginException {
		//Crea assentament registral + Notific@
		logger.info(" >>> Nou assentament registral...");
		AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(
				notificacioEntity, 
				notificacioEntity.getEnviaments());
		RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(
				dir3Codi, 
				arb, 
				!isSirActivat ? null : (isComunicacio ? 2L : 1L), //### [SIR-DESACTIVAT = registre normal, SIR-ACTIVAT = notificació/comunicació]
				notificacioEntity.getId(),
				getEnviamentIds(notificacioEntity));
		//Registrar event
		if(arbResposta.getErrorCodi() != null) {
			logger.info(" >>> ... ERROR");
			updateEventWithError(
					arbResposta,
					null,
					notificacioEntity,
					null);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			logger.info(" >>> ... OK");
			updateEventWithoutError(
					arbResposta,
					notificacioEntity,
					notificacioEntity.getEnviaments(),
					false);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
			info.getParams().add(new AccioParam("Procés descripció: ", " Procedim a enviar la notificació a Notific@"));
			enviarANotifica = true;
		}
		auditNotificacioHelper.updateRegistreNouEnviament(notificacioEntity,
				pluginHelper.getRegistreReintentsPeriodeProperty());
		return enviarANotifica;
	}

	private void crearAssentamentRegistralPerEnviament(
			NotificacioEntity notificacioEntity, 
			String dir3Codi,
			boolean totsAdministracio, 
			NotificacioEnviamentEntity enviament,
			IntegracioInfo info,
			long t0) throws RegistrePluginException {
		logger.info(" >>> Nou assentament registral...");
		AsientoRegistralBeanDto arb = pluginHelper.notificacioToAsientoRegistralBean(
				notificacioEntity, 
				enviament);
		RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(
				dir3Codi, 
				arb, 
				2L,
				notificacioEntity.getId(),
				String.valueOf(enviament.getId()));
		//Registrar event
		if(arbResposta.getErrorCodi() != null) {
			logger.info(" >>> ... ERROR");
			updateEventWithError(
					arbResposta,
					null,
					notificacioEntity,
					enviament);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			logger.info(" >>> ... OK");
			updateEventWithoutError(
					arbResposta,
					notificacioEntity,
					new HashSet<>(Arrays.asList(enviament)),
					totsAdministracio);

			//Comunicació + administració (SIR)
			if (totsAdministracio) {
				logger.debug("Comunicació SIR --> actualitzar estat...");
				auditNotificacioHelper.updateNotificacioEnviada(notificacioEntity);
				registreHelper.enviamentUpdateDatat(
						arbResposta.getEstat(),
						arbResposta.getRegistreData(),
						arbResposta.getSirRecepecioData(),
						arbResposta.getSirRegistreDestiData(),
						arbResposta.getRegistreNumeroFormatat(),
						enviament);
			}
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
		}
		auditNotificacioHelper.updateRegistreNouEnviament(notificacioEntity,
				pluginHelper.getRegistreReintentsPeriodeProperty());
	}
	
	private String getEnviamentIds(NotificacioEntity notificacio) {
		String enviamentIds = "";
		for(NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
			enviamentIds += enviament.getId() + ", ";
		}
		if (!enviamentIds.isEmpty())
			enviamentIds = enviamentIds.substring(0, enviamentIds.length() - 2);
		return enviamentIds;
	}
	
	private void updateEventWithError(
			RespostaConsultaRegistre arbResposta,
			String errorDescripcio,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament) {
		
		if (arbResposta != null)
			errorDescripcio = arbResposta.getErrorDescripcio();

		NotificacioEventEntity event = notificacioEventHelper.addNotificaRegistreEvent(notificacioEntity,
				enviament, errorDescripcio, NotificacioErrorTipusEnumDto.ERROR_REGISTRE);
	}

	private void updateEventWithoutError(
			RespostaConsultaRegistre arbResposta,
			NotificacioEntity notificacioEntity,
			Set<NotificacioEnviamentEntity> enviaments,
			boolean totsAdministracio) {
		if (arbResposta != null) {
			auditNotificacioHelper.updateNotificacioRegistre(arbResposta, notificacioEntity);

			String registreNum = arbResposta.getRegistreNumeroFormatat();
			Date registreData = arbResposta.getRegistreData();
			NotificacioRegistreEstatEnumDto registreEstat = arbResposta.getEstat();

			//Crea un nou event
			notificacioEventHelper.addEnviamentRegistreOKEvent(notificacioEntity,
					registreNum,
					registreData,
					registreEstat,
					enviaments,
					totsAdministracio);
		}
	}

	private boolean isSirActivat() {
		String sir = getPropertyEmprarSir();
		return Boolean.valueOf(sir);
	}
	
	private String getPropertyEmprarSir() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.emprar.sir");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistreNotificaHelper.class);

}
