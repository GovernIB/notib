package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OficinaEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.OficinaRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.cache.Cache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utilitat per a accedir a les caches. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class CacheHelper {

	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private OficinaRepository oficinaRepository;
	private PluginHelper pluginHelper;
	@Resource
	private CacheManager cacheManager;

	@Getter @Setter
	private static String appVersion;


	@Autowired
	public void setPluginHelper(PluginHelper pluginHelper) {
		this.pluginHelper = pluginHelper;
	}

	@Cacheable(value = "usuariAmbCodi", key="#usuariCodi")
	public DadesUsuari findUsuariAmbCodi(String usuariCodi) {
		return pluginHelper.dadesUsuariConsultarAmbCodi(usuariCodi);
	}

	@CacheEvict(value = "usuariAmbCodi",  key="#usuariCodi")
	public void evictUsuariAmbCodi(final String usuariCodi) {
		// evictUsuariByCodi
	}

	@Cacheable(value = "rolsAmbCodi", key="#usuariCodi")
	public List<String> findRolsUsuariAmbCodi(String usuariCodi) {
		return pluginHelper.consultarRolsAmbCodi(usuariCodi);
	}

	@Cacheable(value = "findOficinesEntitat", key="#codiDir3")
	public List<OficinaDto> llistarOficinesEntitat(String codiDir3) {
		return pluginHelper.llistarOficines(codiDir3, AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
	}

	@Cacheable(value = "findLlibreOrganisme", key="#codiDir3Organ")
	public LlibreDto getLlibreOrganGestor(String codiDir3Entitat, String codiDir3Organ) {
		return pluginHelper.llistarLlibreOrganisme(codiDir3Entitat, codiDir3Organ);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "oficinesSIRUnitat", key="#codiDir3Organ")
	public List<OficinaDto> getOficinesSIRUnitat(Map<String, OrganismeDto> arbreUnitats, String codiDir3Organ) {

		List<OficinaEntity> oficines = new ArrayList<>();
		var organ = codiDir3Organ;
		while (organ != null && !organ.isEmpty()) {
			oficines.addAll(oficinaRepository.findByOrganGestorCodiAndSirIsTrue(organ));
			organ = arbreUnitats.get(organ) != null ? arbreUnitats.get(organ).getPare() : null;
		}
		return oficines.stream().map(o -> OficinaDto.builder().codi(o.getCodi()).nom(o.getNom()).sir(o.isSir())
						.organCodi(o.getOrganGestor() != null ? o.getOrganGestor().getCodi() : null).build()).collect(Collectors.toList());
	}

	@Cacheable(value = "oficinesSIREntitat", key="#codiDir3Entitat")
	public List<OficinaDto> getOficinesSIREntitat(String codiDir3Entitat) {

		var oficines = oficinaRepository.findByEntitat_Dir3CodiAndSirIsTrue(codiDir3Entitat);
		return oficines.stream().map(o -> OficinaDto.builder().codi(o.getCodi()).nom(o.getNom()).sir(o.isSir())
						.organCodi(o.getOrganGestor() != null ? o.getOrganGestor().getCodi() : null).build()).collect(Collectors.toList());
	}

	@Cacheable(value = "unitatPerCodi", key="#codi")
	public OrganGestorDto unitatPerCodi(String codi) {

		var organs = pluginHelper.unitatsPerCodi(codi);
		return organs != null && !organs.isEmpty() ? organs.get(0) : null;
	}

	@Cacheable(value = "organigramaOriginal", key="#entitatDir3Codi")
	public Map<String, OrganismeDto> findOrganigramaNodeByEntitat(final String entitatDir3Codi) {

		var organs = organGestorRepository.findByEntitatDir3Codi(entitatDir3Codi);
		if (organs == null || organs.isEmpty()) {
			return new HashMap<>();
		}
		var entitat = entitatRepository.findByDir3Codi(entitatDir3Codi);
		Map<String, OrganismeDto> organigrama = new HashMap<>();
		var arrel = organGestorRepository.findByEntitatAndCodi(entitat, entitatDir3Codi);
		if (arrel == null) {
			return organigrama;
		}
		var organsMap = organsToMap(organs);
		organToOrganigrama(arrel, organsMap, organigrama);
		return organigrama;
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "findUsuariByCodi", key="#usuariCodi")
	public UsuariEntity findUsuariByCodi(final String usuariCodi) {
		return usuariRepository.findByCodi(usuariCodi);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "findEntitatByCodi", key="#codiDir3Entitat")
	public EntitatEntity findEntitatByCodi(final String codiDir3Entitat) {
		return entitatRepository.findByDir3Codi(codiDir3Entitat);
	}


	private HashMap<String, List<OrganGestorEntity>> organsToMap(final List<OrganGestorEntity> organs) {

		HashMap<String, List<OrganGestorEntity>> organsMap = new HashMap<>();
		for (var organ: organs) {
			if (organsMap.containsKey(organ.getCodiPare())) {
				List<OrganGestorEntity> fills = organsMap.get(organ.getCodiPare());
				fills.add(organ);
				continue;
			}
			List<OrganGestorEntity> fills = new ArrayList<>();
			fills.add(organ);
			organsMap.put(organ.getCodiPare(), fills);
		}
		return organsMap;
	}

	private void organToOrganigrama(final OrganGestorEntity organ, final HashMap<String, List<OrganGestorEntity>> organsMap, Map<String, OrganismeDto> organigrama) {

		List<OrganGestorEntity> fills = organsMap.get(organ.getCodi());
		List<String> codisFills = null;
		if (fills != null && !fills.isEmpty()) {
			codisFills = new ArrayList<>();
			for (OrganGestorEntity fill: fills) {
				codisFills.add(fill.getCodi());
			}
		}
		var organisme = OrganismeDto.builder().codi(organ.getCodi()).nom(organ.getNom()).pare(organ.getCodiPare()).fills(codisFills).estat(organ.getEstat()).build();
		organigrama.put(organ.getCodi(), organisme);
		if (fills == null) {
			return;
		}
		for (OrganGestorEntity fill : fills) {
			organToOrganigrama(fill, organsMap, organigrama);
		}
	}

	@Cacheable(value = "llistarNivellsAdministracions")
	public List<CodiValor> llistarNivellsAdministracions() {
		return pluginHelper.llistarNivellsAdministracions();
	}

	@Cacheable(value = "llistarComunitatsAutonomes")
	public List<CodiValor> llistarComunitatsAutonomes() {
		return pluginHelper.llistarComunitatsAutonomes();
	}

	@Cacheable(value = "llistarPaisos")
	public List<CodiValorPais> llistarPaisos() {
		return pluginHelper.llistarPaisos();
	}


	@Cacheable(value = "llistarProvincies")
	public List<CodiValor> llistarProvincies() {
		return pluginHelper.llistarProvincies();
	}

	@Cacheable(value = "llistarProvincies", key="#codiCA")
	public List<CodiValor> llistarProvincies(String codiCA) {
		return pluginHelper.llistarProvincies(codiCA);
	}

	@Cacheable(value = "llistarLocalitats", key="#codiProvincia")
	public List<CodiValor> llistarLocalitats(String codiProvincia) {
		return pluginHelper.llistarLocalitats(codiProvincia);
	}

	public Collection<String> getAllCaches() {
		return cacheManager.getCacheNames();
	}

	@CacheEvict(value = {"procserAmbPermis", "procedimentsAmbPermis", "serveisAmbPermis", "procsersPermisNotificacioMenu", "procsersPermisComunicacioMenu", "procsersPermisComunicacioSirMenu"}, allEntries = true)
	public void evictFindProcedimentServeisWithPermis() {
		//evictFindProcedimentServeisWithPermis
	}

	@CacheEvict(value = {"organsPermisPerProcedimentComu", "procserOrgansCodisAmbPermis"}, allEntries = true)
	public void evictFindProcedimentsOrganWithPermis() {
		//evictFindProcedimentsOrganWithPermis
	}

	@CacheEvict(value = {"organsAmbPermis", "organsAmbPermisPerConsulta"}, allEntries = true)
	public void evictFindOrgansGestorWithPermis() {
		//evictFindOrgansGestorWithPermis
	}

	@CacheEvict(value = "findUsuariByCodi", allEntries = true)
	public void evictUsuariByCodi() {
		// evictUsuariByCodi
	}

	@CacheEvict(value = "findUsuariByCodi",  key="#usuariCodi")
	public void evictUsuariByCodi(final String usuariCodi) {
		// evictUsuariByCodi
	}

	@CacheEvict(value = "findEntitatByCodi", allEntries = true)
	public void evictEntitatByCodi() {
		// evictUsuariByCodi
	}

	@CacheEvict(value = "unitatPerCodi", allEntries = true)
	public void evictUnitatPerCodi() {
		// evictUnitatPerCodi
	}

	@CacheEvict(value = "findUsuarisAmbPermis", key="#procedimentId.concat('-').concat(#codiOrgan)")
	public void evictFindUsuarisAmbPermis(String procedimentId, String codiOrgan) {
		//evictFindUsuarisAmbPermis
	}

    @CacheEvict(value = "findUsuarisAndRolsAmbPermis", key="#procedimentId.concat('-').concat(#codiOrgan)")
    public void evictFindUsuarisAndRolsAmbPermis(String procedimentId, String codiOrgan) {
        //evictFindUsuarisAndRolsAmbPermis
    }

	@CacheEvict(value = {"oficinesSIREntitat", "oficinesSIRUnitat"}, allEntries = true)
	public void evictCercaOficines() {
		//evictCercaOficines
	}

	@CacheEvict(value = "llistarPaisos", allEntries = true)
	public void evictLlistarPaisos() {
		// llistarPaisos
	}

	@CacheEvict(value = "llistarProvincies", allEntries = true)
	public void evictLlistarProvincies() {
		// evictLlistarProvincies
	}

	@CacheEvict(value = "llistarProvinciesCodiCA", allEntries = true)
	public void evictLlistarProvinciesCodiCA() {
		// evictLlistarProvinciesCodiCA
	}

	public void clearCache(String cacheName) {

		var cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
	}

	public void clearAllCaches() {

		for(var cacheName : cacheManager.getCacheNames()) {
			clearCache(cacheName);
		}
	}

	public long getCacheSize(String cacheName) {

		try {
			var cache = cacheManager.getCache(cacheName);
			if (cache == null) {
				return 0L;
			}
			var c = (Cache)cache.getNativeCache();
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(c.iterator(), Spliterator.ORDERED), false).count();
		} catch (Exception ex) {
			log.error("Error obtenint mida de la cache " + cacheName, ex);
			return 0L;
		}
	}

	public long getTotalEhCacheSize() {

		var totalSize = 0L;
		for (var cacheName : cacheManager.getCacheNames()) {
			totalSize = getCacheSize(cacheName);
		}
		return totalSize;
	}
}
