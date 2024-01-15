/**
 * 
 */
package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.CacheDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de la gestió de les cachés de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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

	private static final Map<String, Integer> ordreCaches;

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
		ordreCaches.put("organsAmbPermisPerConsulta", 28);
		ordreCaches.put("organsPermisPerProcedimentComu", 29);
		ordreCaches.put("procserOrgansCodisAmbPermis", 30);
		ordreCaches.put("findUsuariByCodi", 1);
	}


	@Override
	public PaginaDto<CacheDto> getAllCaches() {
		
		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Recuperant el llistat de les caches disponibles");
			List<CacheDto> caches = new ArrayList<>();
			var cachesValues = cacheHelper.getAllCaches();
			CacheDto cache;
			for (var cacheValue : cachesValues) {
				cache = new CacheDto();
				cache.setCodi(cacheValue);
				cache.setDescripcio(messageHelper.getMessage("es.caib.notib.ehcache." + cacheValue));
				cache.setLocalHeapSize(cacheHelper.getCacheSize(cacheValue));
				caches.add(cache);
			}
			caches.sort((c1, c2) -> {
				var c1Pos = ordreCaches.get(c1.getCodi());
				if (c1Pos == null) {
					c1Pos = 1000;
				}
				var c2Pos = ordreCaches.get(c2.getCodi());
				if (c2Pos == null) {
					c2Pos = 1001;
				}
				return c1Pos.compareTo(c2Pos);
			});
			return paginacioHelper.toPaginaDto(caches, CacheDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public void removeCache(String value) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Esborrant la cache (value=" + value + ")");
			cacheHelper.clearCache(value);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
    public void removeAllCaches() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Esborrant totes les caches");
			cacheHelper.clearAllCaches();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

}
