/**
 * 
 */
package es.caib.notib.logic.service;

import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import es.caib.notib.logic.cacheable.PermisosCacheable;
import es.caib.notib.logic.cacheable.ProcSerCacheable;
import es.caib.notib.logic.config.SchedulingConfig;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.ExcepcioLogHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.dto.ExcepcioLogDto;
import es.caib.notib.logic.intf.dto.ProcessosInicialsEnum;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.ProcessosInicialsRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.persist.repository.acl.AclSidRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Implementació dels mètodes per a gestionar la versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class AplicacioServiceImpl implements AplicacioService {

	
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private AclSidRepository aclSidRepository;
	@Autowired
	private ProcessosInicialsRepository processosInicialsRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private PermisosCacheable permisosCacheable;
	@Autowired
	private ProcSerCacheable procedimentsCacheable;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ExcepcioLogHelper excepcioLogHelper;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private SchedulingConfig schedulingConfig;



	@Override
	public void actualitzarEntitatThreadLocal(String entitatCodi) {
		ConfigHelper.setEntitatCodi(entitatCodi);
	}

	@Transactional
	@Override
	public void processarAutenticacioUsuari() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			log.debug("Processant autenticació (usuariCodi=" + auth.getName() + ")");
			var usuari = usuariRepository.findById(auth.getName()).orElse(null);
			if (usuari == null) {
				crearUsuari(auth.getName());
				permisosCacheable.clearAuthenticationPermissionsCaches(auth);
				procedimentsCacheable.clearAuthenticationProcedimentsCaches(auth);
				return;
			}
			log.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");
			var dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari == null) {
				throw new NotFoundException(auth.getName(), DadesUsuari.class);
			}
			if (dadesUsuari.getNomSencer() != null) {
				usuari.update(dadesUsuari.getNomSencer(), dadesUsuari.getEmail());
			} else {
				usuari.update(dadesUsuari.getNom(), dadesUsuari.getLlinatges(), dadesUsuari.getEmail());
			}
			cacheHelper.evictUsuariByCodi(usuari.getCodi());
			permisosCacheable.clearAuthenticationPermissionsCaches(auth);
			procedimentsCacheable.clearAuthenticationProcedimentsCaches(auth);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean existeixUsuariNotib(String codi) {
		return usuariRepository.findByCodi(codi) != null;
	}

	@Override
	public boolean existeixUsuariSeycon(String codi) {
		return cacheHelper.findUsuariAmbCodi(codi) != null;
	}

	@Transactional
	@Override
	public void crearUsuari(String codi) {

		log.debug("Consultant plugin de dades d'usuari (usuariCodi=" + codi + ")");
		var dadesUsuari = cacheHelper.findUsuariAmbCodi(codi);
		var idioma = configHelper.getConfig("es.caib.notib.default.user.language");
		if (dadesUsuari == null) {
			throw new NotFoundException(codi, DadesUsuari.class);
		}
		var u = UsuariEntity.builder().codi(dadesUsuari.getCodi()).email(dadesUsuari.getEmail()).idioma(idioma).nom(dadesUsuari.getNom())
						.llinatges(dadesUsuari.getLlinatges()).nomSencer(dadesUsuari.getNomSencer()).build();
		usuariRepository.save(u);
	}

	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant configuració de usuari actual");
			var usuari = usuariRepository.findById(dto.getCodi()).orElseThrow();
			usuari.update(UsuariEntity.builder().rebreEmailsNotificacio(dto.getRebreEmailsNotificacio()).emailAlt(dto.getEmailAlt())
					.rebreEmailsNotificacioCreats(dto.getRebreEmailsNotificacioCreats()).idioma(dto.getIdioma()).build());
			return toUsuariDtoAmbRols(usuari);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void updateRolUsuariActual(String rol) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant úlrim rol de usuari actual");
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var usuari = usuariRepository.findById(auth.getName()).orElseThrow();
			usuari.updateUltimRol(rol);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void updateEntitatUsuariActual(Long entitat) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant úlrim rol de usuari actual");
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var usuari = usuariRepository.findById(auth.getName()).orElseThrow();
			usuari.updateUltimaEntitat(entitat);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariActual() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			log.debug("Obtenint usuari actual");
			return auth != null ? toUsuariDtoAmbRols(cacheHelper.findUsuariByCodi(auth.getName())) : null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public String getIdiomaUsuariActual() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			log.debug("Obtenint usuari actual");
			return auth != null ? usuariRepository.getIdiomaUsuari(auth.getName()) : null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Transactional(readOnly = true)
	@Override
	public List<String> findRolsUsuariAmbCodi(String codi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Obtenint els rols de l'usuari amb codi (codi=" + codi + ")");
			return cacheHelper.findRolsUsuariAmbCodi(codi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<String> findRolsUsuariActual() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Obtenint els rols de l'usuari actual");
			var auth = SecurityContextHolder.getContext().getAuthentication();
			return cacheHelper.findRolsUsuariAmbCodi(auth.getName());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodi(String codi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Obtenint usuari amb codi (codi=" + codi + ")");
			return conversioTipusHelper.convertir(usuariRepository.findById(codi).orElse(null), UsuariDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbText(String text) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant usuaris amb text (text=" + text + ")");
			return conversioTipusHelper.convertirList(usuariRepository.findByText(text), UsuariDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public void excepcioSave(Throwable exception) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Emmagatzemant excepció (exception=" + exception + ")");
			excepcioLogHelper.addExcepcio(exception);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ExcepcioLogDto excepcioFindOne(Long index) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta d'una excepció (index=" + index + ")");
			return excepcioLogHelper.findAll().get(index.intValue());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<ExcepcioLogDto> excepcioFindAll() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de les excepcions disponibles");
			return excepcioLogHelper.findAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("Consulta dels rols definits a les ACLs");
			return aclSidRepository.findSidByPrincipalFalse();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public String propertyGetByEntitat(String property, String defaultValue) {

		var value = configHelper.getConfig(property);
		return Strings.isNullOrEmpty(value) ? defaultValue : value;
	}

	@Override
	public String propertyGetByEntitat(String property) {
		return configHelper.getConfig(property);
	}

	@Override
	@Cacheable(value = "propietat", key = "#property")
	public String propertyGet(String property) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del valor de la property (property=" + property + ")");
			return configHelper.getConfig(property);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public String propertyGet(String property, String defaultValue) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del valor de la property (property=" + property + ", default=" + defaultValue + ")");
			var propertyValue = configHelper.getConfig(property);
			return propertyValue == null || propertyValue.trim().isEmpty() ? defaultValue : propertyValue;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public String getMetrics() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant les mètriques de l'aplicació");
			var mapper = new ObjectMapper();
			mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS,false));
			return mapper.writeValueAsString(metricsHelper.getMetricRegistry());
		} catch (Exception ex) {
			log.error("Error al generar les mètriques de l'aplicació", ex);
			return "ERR";
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public String getAppVersion() {
		return CacheHelper.getAppVersion();
	}

	@Override
	public void setAppVersion(String appVersion) {
		CacheHelper.setAppVersion(appVersion);
	}

	private UsuariDto toUsuariDtoAmbRols(UsuariEntity usuari) {

		if (usuari == null) {
			return null;
		}
		var dto = conversioTipusHelper.convertir(usuari, UsuariDto.class);
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
			return dto;
		}
		var rols = usuariRepository.getNotibRolsDisponibles(auth.getName());
		var rolsAplicacio = List.of("NOT_SUPER", "NOT_ADMIN", "NOT_CARPETA", "NOT_APL");
		rols.addAll(rolsAplicacio);
		rols.retainAll(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		dto.setRols(rols.toArray(String[]::new));
		return dto;
	}

	public String getMissatgeErrorAccesAdmin() {
		return messageHelper.getMessage("error.acces.administrador.excepcio");
	}

	@Override
	public void restartSchedulledTasks() {
		schedulingConfig.restartSchedulledTasks();
	}

	@Override
	public void propagateDbProperties() {
		configHelper.reloadDbProperties();
	}


	// PROCESSOS INICIALS
    @Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<ProcessosInicialsEnum> getProcessosInicialsPendents() {

		List<ProcessosInicialsEnum> processosInicials = new ArrayList<>();
		var processos = processosInicialsRepository.findProcesosInicialsEntityByInitTrue();
		if (processos != null) {
			processosInicials = processos.stream().map(p -> p.getCodi()).collect(Collectors.toList());
		}
        return processosInicials;
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateProcesInicialExecutat(ProcessosInicialsEnum proces) {
		processosInicialsRepository.updateInit(proces, false);
	}

}
