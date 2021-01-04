/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RegistreNotificaHelper {
	
	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	ConversioTipusHelper conversioTipusHelper;
	@Autowired
	NotificaHelper notificaHelper;
	@Autowired
	RegistreHelper registreHelper;
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private AuditEnviamentHelper auditEnviamentHelper;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private IntegracioHelper integracioHelper;
	
	public boolean realitzarProcesRegistrar(
			NotificacioEntity notificacioEntity,
			List<NotificacioEnviamentDtoV2> enviaments) throws RegistreNotificaException {
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
			
//			### COMUNICACIÓ + TOT A ADMINISTRACIÓ
			if(isComunicacio && totsAdministracio) {
				logger.info(" [REG-NOT] Realitzant nou assentament registral per SIR");
				info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral per SIR"));
				for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
					info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Realitzant nou assentament registral de l'enviament: " + enviament.getId()));
					try {
						//Només crea assentament registral sense notificar
						crearAssentamentRegistralPerEnviament(
								notificacioEntity, 
								notificacioEntity.getEntitat().getDir3CodiReg() != null ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi(), 
								totsAdministracio, 
								enviament,
								info,
								t0);
					} catch (Exception ex) {
						String errorDescripcio = "Hi ha hagut un error registrant l'enviament + " + enviament.getId();
						logger.error(errorDescripcio, ex);
						integracioHelper.addAccioError(info, errorDescripcio, ex);
						throw new RegistreNotificaException(
								ex.getMessage(),
								ex);
					}
				}
			} else {
//				### COMUNICACIÓ/NOTIFICACIÓ + NOTIFIC@
				logger.info(" [REG-NOT] Comunicació: Assentament registral + Notifica");
				try {
					info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] " + notificacioEntity.getEnviamentTipus() + ": nou assentament registral + Notifica de la notificació: " + notificacioEntity.getId()));
					enviarANotifica = crearAssentamentRegistralPerNotificacio(
							notificacioEntity, 
							notificacioEntity.getEntitat().getDir3CodiReg() != null ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi(), 
							enviarANotifica,
							isComunicacio,
							true,
							info,
							t0);
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
				enviarANotifica = crearAssentamentRegistralPerNotificacio(
						notificacioEntity, 
						notificacioEntity.getEntitat().getDir3CodiReg() != null ? notificacioEntity.getEntitat().getDir3CodiReg() : notificacioEntity.getEntitat().getDir3Codi(), 
						enviarANotifica,
						isComunicacio,
						false,
						info,
						t0);
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
		notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
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
					null,
					notificacioEntity.getEnviaments());
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			logger.info(" >>> ... OK");
			updateEventWithoutError(
					arbResposta,
					notificacioEntity,
					null,
					notificacioEntity.getEnviaments(),
					false);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
			info.getParams().add(new AccioParam("Procés descripció: ", " Procedim a enviar la notificació a Notific@"));
			enviarANotifica = true;
		}
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
		notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
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
					enviament,
					notificacioEntity.getEnviaments());
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] Hi ha hagut un error realitzant el procés de registre (temps=" + (t1 - t0) + "ms): " + arbResposta.getErrorDescripcio()));
		} else {
			logger.info(" >>> ... OK");
			updateEventWithoutError(
					arbResposta,
					notificacioEntity,
					enviament,
					null,
					totsAdministracio);
			long t1 = System.currentTimeMillis();
			info.getParams().add(new AccioParam("Procés descripció: ", " [REG-NOT] El procés de registre ha finalizat correctament (temps=" + (t1 - t0) + "ms)"));
		}
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
	
	public NotificacioEntity updateEventWithError(
			RespostaConsultaRegistre arbResposta,
			String errorDescripcio,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			Set<NotificacioEnviamentEntity> enviaments) {
		
		if (arbResposta != null)
			errorDescripcio = arbResposta.getErrorDescripcio();
			
		//Crea un nou event
		NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
				notificacioEntity).
				error(true).
				errorDescripcio(errorDescripcio);
		
		if (notificacioEntity.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
			eventBulider.callbackInicialitza();
		}
		NotificacioEventEntity event = eventBulider.build();
		//Actualitza l'event per cada enviament
		if (enviament != null) {
			eventBulider.enviament(enviament);

			auditNotificacioHelper.updateNotificacioErrorRegistre(notificacioEntity, event);
			notificacioEventRepository.saveAndFlush(event);
		} else {
			for (NotificacioEnviamentEntity enviamentEntity : enviaments) {
				enviamentEntity.updateNotificaError(true, event);
				eventBulider.enviament(enviamentEntity);
				
				auditNotificacioHelper.updateNotificacioErrorRegistre(notificacioEntity, event);
				notificacioEventRepository.saveAndFlush(event);
			}
		} 
		return notificacioEntity;
	}

	public NotificacioEntity updateEventWithoutError(
			RespostaConsultaRegistre arbResposta,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			Set<NotificacioEnviamentEntity> enviaments,
//			boolean enviarNotificacio,
			boolean totsAdministracio) {
		//Crea un nou event
		NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
				notificacioEntity);
		
		if (notificacioEntity.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
			eventBulider.callbackInicialitza();
		
		NotificacioEventEntity event = eventBulider.build();
		
		if (arbResposta != null) {
			auditNotificacioHelper.updateNotificacioRegistre(arbResposta, notificacioEntity);
			logger.info(" >>> Canvi estat a REGISTRADA ");
//			if (enviarNotificacio) {
//				logger.info(" >>> Notificant...");
//				notificaHelper.notificacioEnviar(notificacioEntity.getId());
//			}
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
			if (enviament != null) {
				auditEnviamentHelper.actualitzaRegistreEnviament(
						arbResposta,
						notificacioEntity,
						enviament,
						totsAdministracio,
						eventBulider,
						event);
			} else {
				for(NotificacioEnviamentEntity enviamentEntity: enviaments) {
					auditEnviamentHelper.actualitzaRegistreEnviament(
							arbResposta,
							notificacioEntity,
							enviamentEntity,
							totsAdministracio,
							eventBulider,
							event);
				}
			}
		}
		return notificacioEntity;
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
