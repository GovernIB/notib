/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.SchedulledService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.CreacioSemaforDto;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SchedulledServiceImpl implements SchedulledService {
	
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MetricsHelper metricsHelper;

	// 1. Enviament de notificacions pendents al registre y notific@
	////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.registre.enviaments.periode}", 
			initialDelayString = "${config:es.caib.notib.tasca.registre.enviaments.retard.inicial}")
	public void registrarEnviamentsPendents() throws RegistreNotificaException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("[REG] Cercant notificacions pendents de registrar");
			List pendents = notificacioService.getNotificacionsPendentsRegistrar();
			if (pendents != null && !pendents.isEmpty()) {
				logger.info("[REG] Realitzant registre per a " + pendents.size() + " notificacions pendents");
				for (NotificacioEntity pendent : (List<NotificacioEntity>)pendents) {
					logger.info("[REG] >>> Realitzant registre de la notificació: [Id: " + pendent.getId() + ", Estat: " + pendent.getEstat() + "]");
					notificacioService.notificacioRegistrar(pendent.getId());
				}
			} else {
				logger.info("[REG] No hi ha notificacions pendents de registrar");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	// 2. Enviament de notificacions registrades a Notific@
	///////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviaments.retard.inicial}")
	public void notificaEnviamentsRegistrats() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!isSemaforInUse() && isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[NOT] Cercant notificacions registrades pendents d'enviar a Notifica");
				List pendents = notificacioService.getNotificacionsPendentsEnviar();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[NOT] Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents");
					for (NotificacioEntity pendent: (List<NotificacioEntity>)pendents) {
						logger.info("[NOT] >>> Realitzant enviament a Notifica de la notificació: [Id: " + pendent.getId() + ", Estat: " + pendent.getEstat() + "]");
						notificacioService.notificacioEnviar(pendent.getId());
					}
				} else {
					logger.info("[NOT] No hi ha notificacions pendents d'enviar a Notific@");
				}
			} else {
				logger.info("[NOT] L'enviament de notificacions a Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	// 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
	//////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial}")
	public void enviamentRefrescarEstatPendents() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioEstatActiu() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[EST] Cercant enviaments pendents de refrescar l'estat de Notifica");
				List pendents = notificacioService.getNotificacionsPendentsRefrescarEstat();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[EST] Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments");
					for (NotificacioEnviamentEntity pendent: (List<NotificacioEnviamentEntity>)pendents) {
						logger.info("[EST] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + pendent.getId() + ", Estat: " + pendent.getNotificaEstat() + "]");
						notificacioService.enviamentRefrescarEstat(pendent.getId());
					}
				} else {
					logger.info("[EST] No hi ha enviaments pendents de refrescar l'estat de Notifica");
				}
			} else {
				logger.info("[EST] L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	// 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
	//////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial}")
	public void enviamentRefrescarEstatEnviatSir() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (isTasquesActivesProperty() && isEnviamentActualitzacioEstatRegistreActiu()) {
				logger.info("[SIR] Cercant enviaments pendents de refrescar l'estat enviat SIR");
				List pendents = notificacioService.getNotificacionsPendentsRefrescarEstatRegistre();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[SIR] Realitzant refresc de l'estat de enviat SIR per a " + pendents.size() + " enviaments");
					for (NotificacioEnviamentEntity pendent: (List<NotificacioEnviamentEntity>)pendents) {
						logger.info(">>> Consultat l'estat a registre de l'enviament: [Id: " + pendent.getId() + ", Estat: " + pendent.getNotificaEstat() + "]" + ", i actualitzant les dades a Notib.");
						notificacioService.enviamentRefrescarEstatRegistre(pendent.getId());
					}
				} else {
					logger.info("[SIR] No hi ha enviaments pendents de refrescar l'estat enviats a SIR");
				}
			} else {
				logger.info("[SIR] L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}
	
	
	// 5. Actualització dels procediments a partir de la informació de Rolsac
	/////////////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(cron = "${config:es.caib.notib.actualitzacio.procediments.cron}")
	public void actualitzarProcediments() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (isActualitzacioProcedimentsActiuProperty()) {
				addAdminAuthentication();
				logger.info("[PRO] Cercant entitats per a actualitzar els procediments");
				List<EntitatDto> entitats = entitatService.findAll();
				if (entitats != null && !entitats.isEmpty()) {
					logger.info("[PRO] Realitzant actualització de procediments per a " + entitats.size() + " entitats");
					for (EntitatDto entitat: entitats) {
						logger.info(">>> Actualitzant procedimetns de la entitat: " + entitat.getNom());
						procedimentService.actualitzaProcediments(entitat);
					}
				} else {
					logger.info("[PRO] No hi ha entitats per actualitzar");
				}
			} else {
				logger.info("[PRO] L'actualització de procedimetns està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}

	// Refrescar notificacions expirades
	/////////////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(cron = "${config:es.caib.notib.refrescar.notificacions.expirades.cron}")
	public void refrescarNotificacionsExpirades() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("[NOT] Refrescant notificacions expirades");
			notificacioService.enviamentsRefrescarEstat();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private void addAdminAuthentication() {
		Principal principal = new Principal() {
			public String getName() {
				return "SCHEDULLER";
			}
		};
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("NOT_SUPER"));
		authorities.add(new SimpleGrantedAuthority("NOT_ADMIN"));
		
		Authentication auth = new UsernamePasswordAuthenticationToken(
				principal ,
				"N/A",
				authorities);
		
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
		
	
	private boolean isNotificaEnviamentsActiu() {
		String actives = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasca.notifica.enviaments.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private boolean isEnviamentActualitzacioEstatActiu() {
		String actives = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private boolean isEnviamentActualitzacioEstatRegistreActiu() {
		String actives = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private boolean isTasquesActivesProperty() {
		String actives = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasques.actives");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private boolean isActualitzacioProcedimentsActiuProperty() {
		String actiu = PropertiesHelper.getProperties().getProperty("es.caib.notib.actualitzacio.procediments.actiu");
		if (actiu != null) {
			return new Boolean(actiu).booleanValue();
		} else {
			return false;
		}
	}
	
	private boolean isSemaforInUse() {
		boolean inUse = true;
		synchronized(CreacioSemaforDto.getCreacioSemafor()) {
			inUse = false;
		}
		return inUse;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulledServiceImpl.class);

}
