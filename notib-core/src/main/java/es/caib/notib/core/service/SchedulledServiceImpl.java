package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.SchedulledService;
import es.caib.notib.core.api.service.ServeiService;
import es.caib.notib.core.threads.RegistrarThread;
import es.caib.notib.core.config.SchedulingConfig;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EnviamentHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.NotificacioHelper;
import es.caib.notib.core.helper.OrganGestorHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesConstants;
import es.caib.notib.core.helper.SemaforNotificacio;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EnviamentTableRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.monitor.MonitorIntegracioRepository;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SchedulledServiceImpl implements SchedulledService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private OrganGestorRepository organRepository;
	@Resource
	private EnviamentTableRepository envTableRepository;
	@Resource
	private NotificacioTableViewRepository notTableRepository;
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
	private ServeiService serveiService;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private SchedulingConfig schedulingConfig;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MonitorIntegracioRepository monitorRepository;

	@Override
	public void restartSchedulledTasks() {
		schedulingConfig.restartSchedulledTasks();
	}

	// 1. Enviament de notificacions pendents al registre i notific@
	////////////////////////////////////////////////////////////////
	@Override
	public void registrarEnviamentsPendents() throws RegistreNotificaException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("[REG] Cercant notificacions pendents de registrar");
			List<Long> pendents = notificacioService.getNotificacionsPendentsRegistrar();
			if (pendents == null || pendents.isEmpty()) {
				logger.info("[REG] No hi ha notificacions pendents de registrar");
				return;
			}
			logger.info("[REG] Realitzant registre per a " + pendents.size() + " notificacions pendents");
//			ExecutorService executorService = Executors.newFixedThreadPool(pendents.size());
			RegistrarThread thread;
//			Map<Long, Future<Boolean>> futurs = new HashMap<>();
//			Future<Boolean> futur;
			boolean multiThread = Boolean.parseBoolean(configHelper.getConfig(PropertiesConstants.SCHEDULLED_MULTITHREAD));
			for (Long pendent : pendents) {
				if (multiThread) {
					thread = new RegistrarThread(pendent, notificacioHelper);
					thread.run();
//					futur = executorService.submit(thread);
//					futurs.put(pendent, futur);
				} else {
					logger.info("[REG] >>> Realitzant registre de la notificació id: " + pendent);
					notificacioHelper.registrarNotificar(pendent);
				}
			}
//			Set<Long> keys = futurs.keySet();
//			for (Long key : keys) {
//				try {
//					futurs.get(key).get();
//				} catch (Exception ex) {
//					logger.error(String.format("[REG] Error registrant la notificacio ", key), ex);
//				}
//			}

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// 2. Enviament de notificacions registrades a Notific@
	///////////////////////////////////////////////////////
	@Override
	public void notificaEnviamentsRegistrats() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
//			if (!isSemaforInUse() && isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
			if (isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[NOT] Cercant notificacions registrades pendents d'enviar a Notifica");
				List<Long> pendents = notificacioService.getNotificacionsPendentsEnviar();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[NOT] Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents");
					for (Long pendent: pendents) {
						logger.info("[NOT] >>> Realitzant enviament a Notifica de la notificació amb id: " + pendent);
						if (SemaforNotificacio.isSemaforInUse(pendent)) {
							continue;
						}
						notificacioService.notificacioEnviar(pendent);
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
	@Override
	public void enviamentRefrescarEstatPendents() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioEstatActiu() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[EST] Cercant enviaments pendents de refrescar l'estat de Notifica");
				List<Long> pendents = notificacioService.getNotificacionsPendentsRefrescarEstat();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[EST] Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments");
					for (Long pendent: pendents) {
						logger.info("[EST] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + pendent + "]");
						notificacioService.enviamentRefrescarEstat(pendent);
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
				List<Long> pendents = notificacioService.getNotificacionsPendentsRefrescarEstatRegistre();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[SIR] Realitzant refresc de l'estat de enviat SIR per a " + pendents.size() + " enviaments");
					for (Long pendent: pendents) {
						logger.info(">>> Consultat l'estat a registre de l'enviament: [Id: " + pendent + "]" + ", i actualitzant les dades a Notib.");
						notificacioService.enviamentRefrescarEstatRegistre(pendent);
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
						ConfigHelper.setEntitatCodi(entitat.getCodi());
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
	@Override
	public void enviamentRefrescarEstatDEH() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioCertificacioActiva() && notificaHelper.isConnexioNotificaDisponible()) {
				logger.info("[DEH] Cercant enviaments DEH finalitzats sense certificació...");
				List<Long> pendents = notificacioService.getNotificacionsDEHPendentsRefrescarCert();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[DEH] Realitzant refresc de certificació de Notifica per a " + pendents.size() + " enviaments");
					for (Long enviament: pendents) {
						logger.info("[DEH] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + enviament + "]");
						enviamentHelper.updateDEHCertNovaConsulta(enviament);
						notificacioService.enviamentRefrescarEstat(enviament);
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
				List<Long> pendents = notificacioService.getNotificacionsCIEPendentsRefrescarCert();
				if (pendents != null && !pendents.isEmpty()) {
					logger.info("[CIE] Realitzant refresc de certificació de Notifica per a " + pendents.size() + " enviaments");
					for (Long enviament: pendents) {
						logger.info("[CIE] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + enviament + "]");
						enviamentHelper.updateCIECertNovaConsulta(enviament);
						notificacioService.enviamentRefrescarEstat(enviament);
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
	
	//8. Esborra documents temporals
	//////////////////////////////////////////////////////////////////
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
//			esborrarTemporals(baseDir);
			String command = SystemUtils.IS_OS_LINUX ?
					"find " + baseDir + " -mindepth 1 -type f -mtime +1 -delete" :
					"forfiles /p \"" + baseDir + "\" /s /d -1 /c \"cmd /c del /q @file\"";
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			process.destroy();
		} catch(Exception ex) {
			logger.error("SchedulledService.eliminarDocumentsTemporals -> Error eliminant els documents temporals del directori " + baseDir);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	// 9. Actualització dels serveis a partir de la informació de Rolsac
	/////////////////////////////////////////////////////////////////////////
	@Override
	public void actualitzarServeis() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("[SER] Actualitzant serveis...");
			if (!isActualitzacioServeisActiuProperty()) {
				logger.info("[SER] L'actualització de serveis està deshabilitada");
				return;
			}
			addAdminAuthentication();
			logger.info("[SER] Cercant entitats per a actualitzar els serveis");
			List<EntitatDto> entitats = entitatService.findAll();
			if (entitats == null || entitats.isEmpty()) {
				logger.info("[SER] No hi ha entitats per actualitzar");
				return;
			}
			logger.info("[SER] Realitzant actualització de serveis per a " + entitats.size() + " entitats");
			for (EntitatDto entitat: entitats) {
				logger.info(">>> Actualitzant serveis de la entitat: " + entitat.getNom());
				serveiService.actualitzaServeis(entitat);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}

    @Override
    public void consultaCanvisOrganigrama() {
		logger.debug("Execució tasca periòdica: Actualitzar procedimetns");

		if (configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA) == null)	// Tasca en segon pla no configurada
			return;
		List<EntitatEntity> entitats = entitatRepository.findAll();
		for(EntitatEntity entitat: entitats) {
			organGestorHelper.consultaCanvisOrganigrama(entitat);
		}
    }

	@Override
	public void monitorIntegracionsEliminarAntics() {

		logger.debug("Execució tasca periòdica: netejar monitor integracions");
		String dies = configHelper.getConfig(PropertiesConstants.MONITOR_INTEGRACIONS_ELIMINAR_ANTERIORS_DIES);
		int d = 3;
		try {
			d = Integer.parseInt(dies);
		} catch (Exception ex) {
			logger.error("La propietat no retorna un número -> " + dies);
		}
		Calendar c = Calendar.getInstance();
		c.add(DAY_OF_MONTH, -d);
		Date llindar = c.getTime();
		try {
			List<Long> ids;
			while (monitorRepository.existeixenAntics(llindar) == 1) {
				ids = monitorRepository.getNotificacionsAntigues(llindar);
				integracioHelper.eliminarAntics(ids);
			}
		} catch (Exception ex) {
			logger.error("Error esborrant les entrades del monitor d'integracions antigues.", ex);
		}
	}

	@Transactional
	@Override
	public void actualitzarEstatOrgansEnviamentTable() {

		List<EntitatEntity> entitats = entitatRepository.findAll();
		List<OrganGestorEntity> organs;
		Date ara = new Date();
		long diferencia;
		for (EntitatEntity e : entitats) {
			if (e.getDataActualitzacio() == null) {
				continue;
			}
			diferencia = ara.getTime() - e.getDataActualitzacio().getTime();
			if (86400000 < diferencia) {
				continue;
			}
			organs = organRepository.findByNoVigentIsTrue();
			for (OrganGestorEntity o : organs) {
				envTableRepository.updateOrganEstat(o.getCodi(), o.getEstat());
				notTableRepository.updateOrganEstat(o.getCodi(), o.getEstat());
			}
		}
	}

	private void esborrarTemporals(String dir) throws Exception {

		if (Strings.isNullOrEmpty(dir)) {
			return;
		}
		Path path = Paths.get(dir);
		DirectoryStream<Path> files = Files.newDirectoryStream(path);
		for (Path file : files) {
			if (Files.isDirectory(file)) {
				esborrarTemporals(file.toString());
			}
			File f = file.toFile();
			long periode = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L);
			if (f.lastModified() < periode) {
				logger.info("Esborrant fitxer " + file);
				Files.delete(file);
			}
		}
	}

	private String getBaseDir(String agrupacio) {
		// TODO: Això es global o per entitat???!!!
		String baseDir = configHelper.getConfig("es.caib.notib.plugin.gesdoc.filesystem.base.dir");
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
	private boolean isActualitzacioServeisActiuProperty() {
		return configHelper.getAsBoolean("es.caib.notib.actualitzacio.serveis.actiu");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulledServiceImpl.class);

}
