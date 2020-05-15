/**
 * 
 */
package es.caib.notib.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.SchedulledService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SchedulledServiceImpl implements SchedulledService {
	
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PropertiesHelper propertiesHelper;
	

	// 1. Enviament de notificacions pendents al registre y notific@
	////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.registre.enviaments.periode}", 
			initialDelayString = "${config:es.caib.notib.tasca.registre.enviaments.retard.inicial}")
	public void registrarEnviamentsPendents() {
		logger.info("[REG] Cercant notificacions pendents de registrar");
		int maxPendents = getRegistreEnviamentsProcessarMaxProperty();
		List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatPendent(
				pluginHelper.getRegistreReintentsMaxProperty(),
				new PageRequest(0, maxPendents));
		if (!pendents.isEmpty()) {
			logger.info("[REG] Realitzant registre per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
			for (NotificacioEntity pendent : pendents) {
				logger.info("[REG] >>> Realitzant registre de la notificació: [Id: " + pendent.getId() + ", Estat: " + pendent.getEstat() + "]");
				notificacioService.notificacioRegistrar(pendent.getId());
			}
		} else {
			logger.info("[REG] No hi ha notificacions pendents de registrar");
		}
	}
	
	// 2. Enviament de notificacions registrades a Notific@
	///////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviaments.retard.inicial}")
	public void notificaEnviamentsRegistrats() {
		if (isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
			logger.info("[NOT] Cercant notificacions registrades pendents d'enviar a Notifica");
			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatRegistrada(
					pluginHelper.getNotificaReintentsMaxProperty(), 
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.info("[NOT] Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEntity pendent: pendents) {
					logger.info("[NOT] >>> Realitzant enviament a Notifica de la notificació: [Id: " + pendent.getId() + ", Estat: " + pendent.getEstat() + "]");
					notificacioService.notificacioEnviar(pendent.getId());
				}
			} else {
				logger.info("[NOT] No hi ha notificacions pendents d'enviar a Notific@");
			}
		} else {
			logger.info("[NOT] L'enviament de notificacions a Notific@ està deshabilitada");
		}
	}
	// 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
	// PENDENT ELIMINAR DESPRÉS DE PROVAR ADVISER
	//////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial}")
	public void enviamentRefrescarEstatPendents() {
		if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioEstatActiu() && notificaHelper.isConnexioNotificaDisponible()) {
			logger.info("[EST] Cercant enviaments pendents de refrescar l'estat de Notifica");
			int maxPendents = getEnviamentActualitzacioEstatProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByNotificaRefresc(
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.info("[EST] Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					logger.info("[EST] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + pendent.getId() + ", Estat: " + pendent.getNotificaEstat() + "]");
					notificacioService.enviamentRefrescarEstat(pendent.getId());
				}
			} else {
				logger.info("[EST] No hi ha enviaments pendents de refrescar l'estat de Notifica");
			}
		} else {
			logger.info("[EST] L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
		}
	}
	// 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
	// PENDENT ELIMINAR DESPRÉS DE PROVAR ADVISER
	//////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial}")
	public void enviamentRefrescarEstatEnviatSir() {
		if (isTasquesActivesProperty() && isEnviamentActualitzacioEstatRegistreActiu()) {
			logger.info("[SIR] Cercant enviaments pendents de refrescar l'estat enviat SIR");
			int maxPendents = getEnviamentActualitzacioEstatRegistreProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByRegistreRefresc(
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.info("[SIR] Realitzant refresc de l'estat de enviat SIR per a " + pendents.size() + " enviaments (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					logger.info(">>> Consultat l'estat a registre de l'enviament: [Id: " + pendent.getId() + ", Estat: " + pendent.getNotificaEstat() + "]" + ", i actualitzant les dades a Notib.");
					notificacioService.enviamentRefrescarEstatRegistre(pendent.getId());
				}
			} else {
				logger.info("[SIR] No hi ha enviaments pendents de refrescar l'estat enviats a SIR");
			}
		} else {
			logger.info("[SIR] L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
		}
	}
	
	private boolean isNotificaEnviamentsActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.notifica.enviaments.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getNotificaEnviamentsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.notifica.enviaments.processar.max",
				10);
	}
	
	private int getRegistreEnviamentsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.registre.enviaments.processar.max",
				10);
	}

	private boolean isEnviamentActualitzacioEstatActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private boolean isEnviamentActualitzacioEstatRegistreActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max",
				10);
	}
	private int getEnviamentActualitzacioEstatRegistreProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.registre.processar.max",
				10);
	}
	private boolean isTasquesActivesProperty() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasques.actives");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulledServiceImpl.class);

}
