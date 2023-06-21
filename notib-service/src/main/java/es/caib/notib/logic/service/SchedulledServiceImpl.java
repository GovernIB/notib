package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.EnviamentHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.logic.helper.OrganGestorHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import es.caib.notib.logic.helper.SemaforNotificacio;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.SchedulledService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.logic.threads.RegistrarThread;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
@Slf4j
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
	@Lazy
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
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private MonitorIntegracioRepository monitorRepository;

	// 1. Enviament de notificacions pendents al registre i notific@
	////////////////////////////////////////////////////////////////
	@Override
	public void registrarEnviamentsPendents() throws RegistreNotificaException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("[REG] Cercant notificacions pendents de registrar");
			var pendents = notificacioService.getNotificacionsPendentsRegistrar();
			if (pendents == null || pendents.isEmpty()) {
				log.info("[REG] No hi ha notificacions pendents de registrar");
				return;
			}
			log.info("[REG] Realitzant registre per a " + pendents.size() + " notificacions pendents");
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
					log.info("[REG] >>> Realitzant registre de la notificació id: " + pendent);
					notificacioHelper.registrarNotificar(pendent);
				}
			}
//			Set<Long> keys = futurs.keySet();
//			for (Long key : keys) {
//				try {
//					futurs.get(key).get();
//				} catch (Exception ex) {
//					log.error(String.format("[REG] Error registrant la notificacio ", key), ex);
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
		var timer = metricsHelper.iniciMetrica();
		try {
//			if (!isSemaforInUse() && isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
			if (isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
				log.info("[NOT] Cercant notificacions registrades pendents d'enviar a Notifica");
				List<Long> pendents = notificacioService.getNotificacionsPendentsEnviar();
				if (pendents != null && !pendents.isEmpty()) {
					log.info("[NOT] Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents");
					for (Long pendent: pendents) {
						log.info("[NOT] >>> Realitzant enviament a Notifica de la notificació amb id: " + pendent);
						if (SemaforNotificacio.isSemaforInUse(pendent)) {
							continue;
						}
						notificacioService.notificacioEnviar(pendent);
					}
				} else {
					log.info("[NOT] No hi ha notificacions pendents d'enviar a Notific@");
				}
			} else {
				log.info("[NOT] L'enviament de notificacions a Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
	//////////////////////////////////////////////////////////////////
	@Override
	public void enviamentRefrescarEstatPendents() {
		var timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioEstatActiu() && notificaHelper.isConnexioNotificaDisponible()) {
				log.info("[EST] Cercant enviaments pendents de refrescar l'estat de Notifica");
				List<Long> pendents = notificacioService.getNotificacionsPendentsRefrescarEstat();
				if (pendents != null && !pendents.isEmpty()) {
					log.info("[EST] Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments");
					for (Long pendent: pendents) {
						log.info("[EST] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + pendent + "]");
						notificacioService.enviamentRefrescarEstat(pendent);
					}
				} else {
					log.info("[EST] No hi ha enviaments pendents de refrescar l'estat de Notifica");
				}
			} else {
				log.info("[EST] L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
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
		var timer = metricsHelper.iniciMetrica();
		try {
			if (isTasquesActivesProperty() && isEnviamentActualitzacioEstatRegistreActiu()) {
				log.info("[SIR] Cercant enviaments pendents de refrescar l'estat enviat SIR");
				List<Long> pendents = notificacioService.getNotificacionsPendentsRefrescarEstatRegistre();
				if (pendents != null && !pendents.isEmpty()) {
					log.info("[SIR] Realitzant refresc de l'estat de enviat SIR per a " + pendents.size() + " enviaments");
					for (Long pendent: pendents) {
						log.info(">>> Consultat l'estat a registre de l'enviament: [Id: " + pendent + "]" + ", i actualitzant les dades a Notib.");
						notificacioService.enviamentRefrescarEstatRegistre(pendent);
					}
				} else {
					log.info("[SIR] No hi ha enviaments pendents de refrescar l'estat enviats a SIR");
				}
			} else {
				log.info("[SIR] L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}
	
	
	// 5. Actualització dels procediments a partir de la informació de Rolsac
	/////////////////////////////////////////////////////////////////////////
	@Override
	public void actualitzarProcediments() {
		var timer = metricsHelper.iniciMetrica();
		try {
			if (isActualitzacioProcedimentsActiuProperty()) {
				addAdminAuthentication();
				log.info("[PRO] Cercant entitats per a actualitzar els procediments");
				List<EntitatDto> entitats = entitatService.findAll();
				if (entitats != null && !entitats.isEmpty()) {
					log.info("[PRO] Realitzant actualització de procediments per a " + entitats.size() + " entitats");
					for (EntitatDto entitat: entitats) {
						log.info(">>> Actualitzant procedimetns de la entitat: " + entitat.getNom());
						ConfigHelper.setEntitatCodi(entitat.getCodi());
						procedimentService.actualitzaProcediments(entitat);
					}
				} else {
					log.info("[PRO] No hi ha entitats per actualitzar");
				}
			} else {
				log.info("[PRO] L'actualització de procedimetns està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}
	
	//6. Consulta certificació notificacions DEH finalitzades
	//////////////////////////////////////////////////////////////////
	@Override
	public void enviamentRefrescarEstatDEH() {
		var timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioCertificacioActiva() && notificaHelper.isConnexioNotificaDisponible()) {
				log.info("[DEH] Cercant enviaments DEH finalitzats sense certificació...");
				List<Long> pendents = notificacioService.getNotificacionsDEHPendentsRefrescarCert();
				if (pendents != null && !pendents.isEmpty()) {
					log.info("[DEH] Realitzant refresc de certificació de Notifica per a " + pendents.size() + " enviaments");
					for (Long enviament: pendents) {
						log.info("[DEH] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + enviament + "]");
						enviamentHelper.updateDEHCertNovaConsulta(enviament);
						notificacioService.enviamentRefrescarEstat(enviament);
					}
				} else {
					log.info("[DEH] No hi ha enviaments DEH sense certificació");
				}
			} else {
				log.info("[DEH] L'actualització de la certificació dels enviaments amb l'estat de Notific@ està deshabilitada");
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
		var timer = metricsHelper.iniciMetrica();
		try {
			if (!notificaHelper.isAdviserActiu() && isTasquesActivesProperty() && isEnviamentActualitzacioCertificacioActiva() && notificaHelper.isConnexioNotificaDisponible()) {
				log.info("[CIE] Cercant enviaments CIE finalitzats sense certificació...");
				List<Long> pendents = notificacioService.getNotificacionsCIEPendentsRefrescarCert();
				if (pendents != null && !pendents.isEmpty()) {
					log.info("[CIE] Realitzant refresc de certificació de Notifica per a " + pendents.size() + " enviaments");
					for (Long enviament: pendents) {
						log.info("[CIE] >>> Consultat l'estat a Notific@ de l'enviament: [Id: " + enviament + "]");
						enviamentHelper.updateCIECertNovaConsulta(enviament);
						notificacioService.enviamentRefrescarEstat(enviament);
					}
				} else {
					log.info("[CIE] No hi ha enviaments CIE sense certificació");
				}
			} else {
				log.info("[CIE] L'actualització de la certificació dels enviaments amb l'estat de Notific@ està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	//8. Esborra documents temporals
	//////////////////////////////////////////////////////////////////
	@Override
	public void eliminarDocumentsTemporals() {

		var timer = metricsHelper.iniciMetrica();
		String baseDir = getBaseDir(PluginHelper.GESDOC_AGRUPACIO_TEMPORALS);
		if (baseDir == null) {
			log.error("SchedulledService.eliminarDocumentsTemporals -> Error directori base null");
			return;
		}
		try {
			log.info("Eliminant documents temporals del directori " + baseDir);
//			esborrarTemporals(baseDir);
			String command = SystemUtils.IS_OS_LINUX ?
					"find " + baseDir + " -mindepth 1 -type f -mtime +1 -delete" :
					"forfiles /p \"" + baseDir + "\" /s /d -1 /c \"cmd /c del /q @file\"";
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			process.destroy();
		} catch(Exception ex) {
			log.error("SchedulledService.eliminarDocumentsTemporals -> Error eliminant els documents temporals del directori " + baseDir);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	// 9. Actualització dels serveis a partir de la informació de Rolsac
	/////////////////////////////////////////////////////////////////////////
	@Override
	public void actualitzarServeis() {
		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("[SER] Actualitzant serveis...");
			if (!isActualitzacioServeisActiuProperty()) {
				log.info("[SER] L'actualització de serveis està deshabilitada");
				return;
			}
			addAdminAuthentication();
			log.info("[SER] Cercant entitats per a actualitzar els serveis");
			List<EntitatDto> entitats = entitatService.findAll();
			if (entitats == null || entitats.isEmpty()) {
				log.info("[SER] No hi ha entitats per actualitzar");
				return;
			}
			log.info("[SER] Realitzant actualització de serveis per a " + entitats.size() + " entitats");
			for (EntitatDto entitat: entitats) {
				log.info(">>> Actualitzant serveis de la entitat: " + entitat.getNom());
				serveiService.actualitzaServeis(entitat);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}

    @Override
    public void consultaCanvisOrganigrama() {
		log.debug("Execució tasca periòdica: Actualitzar procedimetns");

		if (configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA) == null)	// Tasca en segon pla no configurada
			return;
		List<EntitatEntity> entitats = entitatRepository.findAll();
		for(EntitatEntity entitat: entitats) {
			organGestorHelper.consultaCanvisOrganigrama(entitat);
		}
    }

	@Override
	public void monitorIntegracionsEliminarAntics() {

		log.debug("Execució tasca periòdica: netejar monitor integracions");
		String dies = configHelper.getConfig(PropertiesConstants.MONITOR_INTEGRACIONS_ELIMINAR_ANTERIORS_DIES);
		int d = 3;
		try {
			d = Integer.parseInt(dies);
		} catch (Exception ex) {
			log.error("La propietat no retorna un número -> " + dies);
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
			log.error("Error esborrant les entrades del monitor d'integracions antigues.", ex);
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
		var path = Paths.get(dir);
		File f;
		try (var files = Files.newDirectoryStream(path)) {
			for (var file : files) {
				if (Files.isDirectory(file)) {
					esborrarTemporals(file.toString());
				}
				f = file.toFile();
				long periode = System.currentTimeMillis() - (24 * 60 * 60 * 1000L);
				if (f.lastModified() < periode) {
					log.info("Esborrant fitxer " + file);
					Files.delete(file);
				}
			}
		}
	}

	private String getBaseDir(String agrupacio) {

		// TODO: Això es global o per entitat???!!!
		var baseDir = configHelper.getConfig("es.caib.notib.plugin.gesdoc.filesystem.base.dir");
		if (baseDir == null) {
			return null;
		}
		return baseDir.endsWith("/") ? baseDir + agrupacio : baseDir + "/" + agrupacio;
	}

	// Refrescar notificacions expirades
	/////////////////////////////////////////////////////////////////////////
	@Override
	public void refrescarNotificacionsExpirades() {
		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("[EXPIRATS] Refrescant notificacions expirades");
			addAdminAuthentication();
			enviamentHelper.refrescarEnviamentsExpirats();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private void addAdminAuthentication() {

		Principal principal = () -> "SCHEDULLER";
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("NOT_SUPER"));
		authorities.add(new SimpleGrantedAuthority("NOT_ADMIN"));
		Authentication auth = new UsernamePasswordAuthenticationToken(principal , "N/A", authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	private boolean isNotificaEnviamentsActiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasca.notifica.enviaments.actiu");
	}
	private boolean isEnviamentActualitzacioEstatActiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
	}
	private boolean isEnviamentActualitzacioEstatRegistreActiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.actiu");
	}
	private boolean isTasquesActivesProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasques.actives");
	}
	private boolean isActualitzacioProcedimentsActiuProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.actualitzacio.procediments.actiu");
	}
	private boolean isEnviamentActualitzacioCertificacioActiva() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasca.enviament.actualitzacio.certificacio.finalitzades.actiu");
	}
	private boolean isActualitzacioServeisActiuProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.actualitzacio.serveis.actiu");
	}

}
