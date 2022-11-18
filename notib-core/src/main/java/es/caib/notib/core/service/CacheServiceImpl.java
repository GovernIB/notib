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
import java.util.List;

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
