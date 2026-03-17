package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.model.CacheResource;
import es.caib.notib.logic.intf.resourceservice.CacheResourceService;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de consulta de caches de l'aplciació
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheResourceServiceImpl extends BaseNoDatabaseMutableResourceService<CacheResource, String> implements CacheResourceService {

	private final MetricsHelper metricsHelper;
	private final CacheHelper cacheHelper;
	private final MessageHelper messageHelper;

	@Override
	protected Page<NoDatabaseResourceEntity<CacheResource, String>> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Recuperant el llistat de les caches disponibles");
			List<CacheResource> caches = new ArrayList<>();
			var cachesValues = cacheHelper.getAllCaches();
			CacheResource cache;
			for (var cacheValue : cachesValues) {
				cache = new CacheResource();
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
			Page<CacheResource> page = new PageImpl<>(caches, pageable, caches.size());
			var resultat =  page.map(this::toResourceEntity);
			return resultat;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NoDatabaseResourceEntity<CacheResource, String> toResourceEntity(CacheResource resource) {
		return new NoDatabaseResourceEntity<>(resource.getCodi(), resource);
	}

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
		ordreCaches.put("findUsuariByCodi", 31);
		ordreCaches.put("findEntitatByCodi", 32);
	}

}
