package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.SchedulledService;
import es.caib.notib.core.config.SchedulingConfig;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.plugin.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
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
	private NotificacioHelper notificacioHelper;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private EnviamentHelper enviamentHelper;

	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private SchedulingConfig schedulingConfig;

	@Override
	public void restartSchedulledTasks() {
		schedulingConfig.restartSchedulledTasks();
	}

	// 1. Enviament de notificacions pendents al registre i notific@
	////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registrarEnviamentsPendents() throws RegistreNotificaException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("[REG] Cercant notificacions pendents de registrar");
			List pendents = notificacioService.getNotificacionsPendentsRegistrar();
			if (pendents != null && !pendents.isEmpty()) {
				logger.info("[REG] Realitzant registre per a " + pendents.size() + " notificacions pendents");
				for (NotificacioEntity pendent : (List<NotificacioEntity>)pendents) {
					logger.info("[REG] >>> Realitzant registre de la notificació: [Id: " + pendent.getId() + ", Estat: " + pendent.getEstat() + "]");
					notificacioHelper.registrarNotificar(pendent.getId());
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
	
	//6. Consulta certificació notificacions DEH finalitzades
	//////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void enviamentRefrescarEstatDEH() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioCertificacioActiva() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[DEH] Cercant enviaments DEH finalitzats sense certificació...");
				List pendents = notificacioService.getNotificacionsDEHPendentsRefrescarCert();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[DEH] Realitzant refresc de certificació de Notifica per a " + pendents.size() + " enviaments");
					for (NotificacioEnviamentEntity enviament: (List<NotificacioEnviamentEntity>)pendents) {
						logger.info("[DEH] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
						enviamentHelper.updateDEHCertNovaConsulta(enviament.getId());
						notificacioService.enviamentRefrescarEstat(enviament.getId());
					}
				} else {
					logger.info("[DEH] No hi ha enviaments DEH sense certificació");
				}
			} else {
				logger.info("[DEH] L'actualització de la certificació dels enviaments amb l'estat de Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	//7. Consulta certificació notificacions CIE finalitzades
	//////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void enviamentRefrescarEstatCIE() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioCertificacioActiva() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[CIE] Cercant enviaments CIE finalitzats sense certificació...");
				List pendents = notificacioService.getNotificacionsCIEPendentsRefrescarCert();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[CIE] Realitzant refresc de certificació de Notifica per a " + pendents.size() + " enviaments");
					for (NotificacioEnviamentEntity enviament: (List<NotificacioEnviamentEntity>)pendents) {
						logger.info("[CIE] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
						enviamentHelper.updateCIECertNovaConsulta(enviament.getId());
						notificacioService.enviamentRefrescarEstat(enviament.getId());
					}
				} else {
					logger.info("[CIE] No hi ha enviaments CIE sense certificació");
				}
			} else {
				logger.info("[CIE] L'actualització de la certificació dels enviaments amb l'estat de Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public void eliminarDocumentsTemporals() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		String baseDir = getBaseDir(PluginHelper.GESDOC_AGRUPACIO_TEMPORALS);
		if (baseDir == null) {
			logger.error("SchedulledService.eliminarDocumentsTemporals -> Error directori base null");
			return;
		}
		try {
			logger.info("Eliminant documents temporals del directori " + baseDir);
			esborrarTemporals(baseDir);
		} catch(Exception ex) {
			logger.error("SchedulledService.eliminarDocumentsTemporals -> Error eliminant els documents temporals del directori " + baseDir);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private void esborrarTemporals(String dir) throws Exception {

		if (Strings.isNullOrEmpty(dir)) {
			return;
		}
		Path path = Paths.get(dir);
		DirectoryStream<Path> files = Files.newDirectoryStream(path);
		long now = new Date().getTime();
		for (Path file : files) {
			if (Files.isDirectory(file)) {
				esborrarTemporals(file.toString());
			}
			File f = file.toFile();
			long periode = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L);;
			if (f.lastModified() < periode) {
				logger.info("Esborrant fitxer " + file);
				Files.delete(file);
			}
		}
	}

	private String getBaseDir(String agrupacio) {
		String baseDir = PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesdoc.filesystem.base.dir");
		if (baseDir == null) {
			return null;
		}
		return baseDir.endsWith("/") ? baseDir + agrupacio : baseDir + "/" + agrupacio;
	}

	// Refrescar notificacions expirades
	/////////////////////////////////////////////////////////////////////////
	@Override
	public void refrescarNotificacionsExpirades() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("[EXPIRATS] Refrescant notificacions expirades");
			addAdminAuthentication();
			enviamentHelper.refrescarEnviamentsExpirats();
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
		return configHelper.getAsBoolean("es.caib.notib.tasca.notifica.enviaments.actiu");
	}
	private boolean isEnviamentActualitzacioEstatActiu() {
		return configHelper.getAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
	}
	private boolean isEnviamentActualitzacioEstatRegistreActiu() {
		return configHelper.getAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.actiu");
	}
	private boolean isTasquesActivesProperty() {
		return configHelper.getAsBoolean("es.caib.notib.tasques.actives");
	}
	private boolean isActualitzacioProcedimentsActiuProperty() {
		return configHelper.getAsBoolean("es.caib.notib.actualitzacio.procediments.actiu");
	}
	private boolean isEnviamentActualitzacioCertificacioActiva() {
		return configHelper.getAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.certificacio.finalitzades.actiu");
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
