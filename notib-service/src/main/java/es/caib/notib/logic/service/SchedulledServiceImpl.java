package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.service.*;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
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
    @Autowired
    private CacheHelper cacheHelper;

	// 1. Actualització dels procediments a partir de la informació de Rolsac
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
						log.info(">>> Actualitzant procediments de la entitat: " + entitat.getNom());
						try {
							ConfigHelper.setEntitatCodi(entitat.getCodi());
							procedimentService.actualitzaProcediments(entitat);
							log.info(">>> Procediments actualitzats de la entitat: " + entitat.getNom());
						} catch (Exception ex) {
							log.error(">>> No s'han pogut actualitzar els procediments de l'entitat " + entitat.getNom(), ex);
						}
					}
				} else {
					log.info("[PRO] No hi ha entitats per actualitzar");
				}
			} else {
				log.info("[PRO] L'actualització de procediments està deshabilitada");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}	
	}

	//2. Refrescar notificacions expirades
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

	//4. Consulta certificació notificacions DEH finalitzades
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
	
	//5. Consulta certificació notificacions CIE finalitzades
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
	
	//6. Esborra documents temporals
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
	
	// 7. Actualització dels serveis a partir de la informació de Rolsac
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

	// 8. Consulta de canvis en l'organigrama
	/////////////////////////////////////////////////////////////////////////
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

	// 9. Eliminar entrades al monitor integracions antigues
	/////////////////////////////////////////////////////////////////////////
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

	// 10. Actualitzar estat organs enviament table
	/////////////////////////////////////////////////////////////////////////
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

	@Override
	public void comprimirDocumentsAntics() {

		log.info("Inici tasca comprimir documents antics");
		var llindar = -configHelper.getConfigAsInteger(PropertiesConstants.COMPRIMIR_DOCUMENTS_ANTICS_LLINDAR);
		var ara = Calendar.getInstance().getTime();
		var dataLlindar = DateUtils.addDays(ara, llindar);
		var directori = getBaseDir("notificacions");
		try (var filePathStream = Files.walk(Paths.get(directori), 2)) {
			filePathStream.forEach(filePath -> {
				var file = filePath.toFile();
				if (file.isDirectory()) {
					return;
				}
				try {
					var lastModified = new Date(file.lastModified());
					var proc = Runtime.getRuntime().exec("file " + filePath);
					String s = null;
					String output = "";
					try (var isd = new InputStreamReader(proc.getInputStream()); var stdInput = new BufferedReader(isd)) {
						while ((s = stdInput.readLine()) != null) {
							output = s;
						}
					}
					if (lastModified.after(dataLlindar) || output.contains("Zip")) {
						return;
					}
					var comanda = "zip -j " + filePath + ".zip " + filePath;
					proc = Runtime.getRuntime().exec(comanda);
					proc.waitFor();
					comanda = "rm " + filePath;
					proc = Runtime.getRuntime().exec(comanda);
					proc.waitFor();
					comanda = "mv " + filePath + ".zip " + filePath;
					proc = Runtime.getRuntime().exec(comanda);
					proc.waitFor();
				} catch (Exception e) {
					System.out.println("Error executant la commanda" + e);
				}
			});
		} catch (Exception ex) {
			log.error("Error comprimint els documents antics", ex);
		}
		log.info("Els documents antics s'han comprimit correctament");
    }

	@Override
	public void evictCachePaisosProvincies() {

		cacheHelper.evictLlistarPaisos();
		cacheHelper.evictLlistarProvincies();
		cacheHelper.evictLlistarProvinciesCodiCA();

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
