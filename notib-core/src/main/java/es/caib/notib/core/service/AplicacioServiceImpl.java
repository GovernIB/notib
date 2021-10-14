/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.core.api.dto.ExcepcioLogDto;
import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.ProcedimentsCacheable;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.core.repository.acl.AclSidRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementació dels mètodes per a gestionar la versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private AclSidRepository aclSidRepository;

	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private PermisosCacheable permisosCacheable;
	@Autowired
	private ProcedimentsCacheable procedimentsCacheable;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ExcepcioLogHelper excepcioLogHelper;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;

	@Transactional
	@Override
	public void processarAutenticacioUsuari() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			logger.debug("Processant autenticació (usuariCodi=" + auth.getName() + ")");
			UsuariEntity usuari = usuariRepository.findOne(auth.getName());
			if (usuari == null) {
				logger.debug("Consultant plugin de dades d'usuari (" +
						"usuariCodi=" + auth.getName() + ")");
				DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
				String idioma = configHelper.getConfig("es.caib.notib.default.user.language");
				if (dadesUsuari != null) {
					usuari = usuariRepository.save(
							UsuariEntity.getBuilder(
									dadesUsuari.getCodi(),
									dadesUsuari.getEmail(),
									idioma).
							nom(dadesUsuari.getNom()).
							llinatges(dadesUsuari.getLlinatges()).
							nomSencer(dadesUsuari.getNomSencer()).
							build());
				} else {
					throw new NotFoundException(
							auth.getName(),
							DadesUsuari.class);
				}
			} else {
				logger.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");
				DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
				if (dadesUsuari != null) {
					if (dadesUsuari.getNomSencer() != null) {
						usuari.update(
								dadesUsuari.getNomSencer(),
								dadesUsuari.getEmail());
					} else {
						usuari.update(
								dadesUsuari.getNom(),
								dadesUsuari.getLlinatges(),
								dadesUsuari.getEmail());
					}
				} else {
					throw new NotFoundException(
							auth.getName(),
							DadesUsuari.class);
				}
			}
			permisosCacheable.clearAuthenticationPermissionsCaches(auth);
			procedimentsCacheable.clearAuthenticationProcedimentsCaches(auth);

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant configuració de usuari actual");
			UsuariEntity usuari = usuariRepository.findOne(dto.getCodi());
			usuari.update(
					dto.getRebreEmailsNotificacio(),
					dto.getRebreEmailsNotificacioCreats(),
					dto.getIdioma());
			
			return toUsuariDtoAmbRols(usuari);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void updateRolUsuariActual(String rol) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant úlrim rol de usuari actual");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UsuariEntity usuari = usuariRepository.findOne(auth.getName());
			usuari.updateUltimRol(rol);
//			return toUsuariDtoAmbRols(usuari);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void updateEntitatUsuariActual(Long entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant úlrim rol de usuari actual");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UsuariEntity usuari = usuariRepository.findOne(auth.getName());
			usuari.updateUltimaEntitat(entitat);
//			return toUsuariDtoAmbRols(usuari);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariActual() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			logger.debug("Obtenint usuari actual");
			if (auth == null){
				return null;
			} else {
				return toUsuariDtoAmbRols(usuariRepository.findOne(auth.getName()));
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> findRolsUsuariAmbCodi(String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Obtenint els rols de l'usuari amb codi (codi=" + codi + ")");
			return cacheHelper.findRolsUsuariAmbCodi(codi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<String> findRolsUsuariActual() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Obtenint els rols de l'usuari actual");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			return cacheHelper.findRolsUsuariAmbCodi(auth.getName());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodi(String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Obtenint usuari amb codi (codi=" + codi + ")");
			return conversioTipusHelper.convertir(
					usuariRepository.findOne(codi),
					UsuariDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbText(String text) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant usuaris amb text (text=" + text + ")");
			return conversioTipusHelper.convertirList(
					usuariRepository.findByText(text),
					UsuariDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<IntegracioDto> integracioFindAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant les integracions");
			return integracioHelper.findAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant les darreres accions per a la integració (" +
					"codi=" + codi + ")");
			return integracioHelper.findAccionsByIntegracioCodi(codi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public void excepcioSave(Throwable exception) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Emmagatzemant excepció (" +
					"exception=" + exception + ")");
			excepcioLogHelper.addExcepcio(exception);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ExcepcioLogDto excepcioFindOne(Long index) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta d'una excepció (index=" + index + ")");
			return excepcioLogHelper.findAll().get(index.intValue());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<ExcepcioLogDto> excepcioFindAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de les excepcions disponibles");
			return excepcioLogHelper.findAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("Consulta dels rols definits a les ACLs");
			return aclSidRepository.findSidByPrincipalFalse();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public String propertyGet(String property) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del valor de la property (" +
					"property=" + property + ")");
			return configHelper.getConfig(property);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public String propertyGet(String property, String defaultValue) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del valor de la property (property=" + property + ", default=" + defaultValue + ")");
			String propertyValue = configHelper.getConfig(property);
			if (propertyValue == null || propertyValue.trim().isEmpty())
				return defaultValue;
			return propertyValue;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public String getMetrics() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant les mètriques de l'aplicació");
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(
						new MetricsModule(
								TimeUnit.SECONDS,
								TimeUnit.MILLISECONDS,
								false));
				return mapper.writeValueAsString(metricsHelper.getMetricRegistry());
			} catch (Exception ex) {
				logger.error("Error al generar les mètriques de l'aplicació", ex);
				return "ERR";
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public String getAppVersion() {
		return CacheHelper.appVersion;
	}
	@Override
	public void setAppVersion(String appVersion) {
		CacheHelper.appVersion = appVersion;
	}

	private UsuariDto toUsuariDtoAmbRols(
			UsuariEntity usuari) {
		if (usuari == null)
			return null;
		UsuariDto dto = conversioTipusHelper.convertir(
				usuari,
				UsuariDto.class);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getAuthorities() != null) {
			String[] rols = new String[auth.getAuthorities().size()];
			int index = 0;
			for (GrantedAuthority grantedAuthority: auth.getAuthorities()) {
				rols[index++] = grantedAuthority.getAuthority();
			}
			dto.setRols(rols);
		}
		return dto;
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
