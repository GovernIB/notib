/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.CacheDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.CacheService;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de la gestió de les cachés de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class CacheServiceImpl implements CacheService {

	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;

	public static Map<String, Integer> ordreCaches;

	static {
		ordreCaches = new HashMap<>();
		ordreCaches.put("aclCache", 0);
		ordreCaches.put("usuariAmbCodi", 1);
		ordreCaches.put("rolsAmbCodi", 2);
		ordreCaches.put("entitatsUsuari", 3);
		ordreCaches.put("organsGestorsUsuari", 4);
		ordreCaches.put("findUsuarisAmbPermis", 5);
		ordreCaches.put("organismes", 6);
		ordreCaches.put("organigrama", 7);
		ordreCaches.put("organigramaOriginal", 8);
		ordreCaches.put("codisOrgansFills", 9);
		ordreCaches.put("organCodisAncestors", 10);
		ordreCaches.put("unitatPerCodi", 11);
		ordreCaches.put("findOficinesEntitat", 12);
		ordreCaches.put("findLlibreOrganisme", 13);
		ordreCaches.put("llistarNivellsAdministracions", 14);
		ordreCaches.put("llistarComunitatsAutonomes", 15);
		ordreCaches.put("llistarProvincies", 16);
		ordreCaches.put("llistarLocalitats", 17);
		ordreCaches.put("oficinesSIREntitat", 18);
		ordreCaches.put("oficinesSIRUnitat", 19);
		ordreCaches.put("getPermisosEntitatsUsuariActual", 20);
		ordreCaches.put("procsersPermisNotificacioMenu", 21);
		ordreCaches.put("procsersPermisComunicacioMenu", 22);
		ordreCaches.put("procsersPermisComunicacioSirMenu", 23);
		ordreCaches.put("procserAmbPermis", 24);
		ordreCaches.put("procedimentsAmbPermis", 25);
		ordreCaches.put("serveisAmbPermis", 26);
		ordreCaches.put("organsAmbPermis", 27);
		ordreCaches.put("organsPermisPerProcedimentComu", 28);
		ordreCaches.put("procserOrgansCodisAmbPermis", 29);
	}


	@Override
	public PaginaDto<CacheDto> getAllCaches() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Recuperant el llistat de les caches disponibles");
			List<CacheDto> caches = new ArrayList<>();
			Collection<String> cachesValues = cacheHelper.getAllCaches();
			for (String cacheValue : cachesValues) {
//				if (!cacheValue.equals("aclCache")) {
					CacheDto cache = new CacheDto();
					cache.setCodi(cacheValue);
					cache.setDescripcio(messageHelper.getMessage("es.caib.notib.ehcache." + cacheValue));
					cache.setLocalHeapSize(cacheHelper.getCacheSize(cacheValue));
					caches.add(cache);
//				}
			}
			Collections.sort(caches, new Comparator<CacheDto>() {
				@Override
				public int compare(CacheDto c1, CacheDto c2) {
					Integer c1Pos = ordreCaches.get(c1.getCodi());
					if (c1Pos == null)
						c1Pos = 1000;
					Integer c2Pos = ordreCaches.get(c2.getCodi());
					if (c2Pos == null)
						c2Pos = 1001;
					return c1Pos.compareTo(c2Pos);
				}
			});
			return paginacioHelper.toPaginaDto(caches, CacheDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public void removeCache(String value) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Esborrant la cache (value=" + value + ")");
			cacheHelper.clearCache(value);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
    public void removeAllCaches() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Esborrant totes les caches");
			cacheHelper.clearAllCaches();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

    private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

}
