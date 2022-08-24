package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//	@Resource
//	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
//	@Resource
//	private PermisosHelper permisosHelper;

	private PluginHelper pluginHelper;
	@Resource
	private CacheManager cacheManager;

	public static String appVersion;

	@Autowired
	public void setPluginHelper(PluginHelper pluginHelper) {
		this.pluginHelper = pluginHelper;
	}

	@Cacheable(value = "usuariAmbCodi", key="#usuariCodi")
	public DadesUsuari findUsuariAmbCodi(
			String usuariCodi) {
		return pluginHelper.dadesUsuariConsultarAmbCodi(
				usuariCodi);
	}

	@Cacheable(value = "rolsAmbCodi", key="#usuariCodi")
	public List<String> findRolsUsuariAmbCodi(
			String usuariCodi) {
		return pluginHelper.consultarRolsAmbCodi(
				usuariCodi);
	}

	@Cacheable(value = "denominacioOrganisme", key="#codiDir3")
	public String findDenominacioOrganisme(
			String codiDir3) {
		return pluginHelper.getDenominacio(codiDir3);
	}
	
	@Cacheable(value = "findOficinesEntitat", key="#codiDir3")
	public List<OficinaDto> llistarOficinesEntitat(
			String codiDir3) {
		return pluginHelper.llistarOficines(
				codiDir3, 
				AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
	}
	
	@Cacheable(value = "findLlibresOficina", key="#codiDir3Oficina")
	public List<LlibreDto> llistarLlibresOficina(
			String codiDir3Entitat,
			String codiDir3Oficina) {
		return pluginHelper.llistarLlibres(
				codiDir3Entitat, 
				codiDir3Oficina, 
				AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
	}
	
	@Cacheable(value = "findLlibreOrganisme", key="#codiDir3Organ")
	public LlibreDto getLlibreOrganGestor(
			String codiDir3Entitat,
			String codiDir3Organ) {
		return pluginHelper.llistarLlibreOrganisme(
				codiDir3Entitat,
				codiDir3Organ);
	}
	
	@Cacheable(value = "oficinesSIRUnitat", key="#codiDir3Organ")
	public List<OficinaDto> getOficinesSIRUnitat(Map<String, OrganismeDto> arbreUnitats, String codiDir3Organ) {
		return pluginHelper.oficinesSIRUnitat(codiDir3Organ, arbreUnitats);
	}

	@Cacheable(value = "unitatPerCodi", key="#codi")
	public OrganGestorDto unitatPerCodi(String codi) {
		List<OrganGestorDto> organs = pluginHelper.unitatsPerCodi(codi);
		if (organs != null && !organs.isEmpty()) {
			return organs.get(0);
		}
		return null;
	}
	
	@Cacheable(value = "oficinesSIREntitat", key="#codiDir3Entitat")
	public List<OficinaDto> getOficinesSIREntitat(String codiDir3Entitat) {
		return pluginHelper.oficinesSIREntitat(codiDir3Entitat);
	}
	
	@Cacheable(value = "organigramaOriginal", key="#entitatDir3Codi")
	public Map<String, OrganismeDto> findOrganigramaNodeByEntitat(final String entitatDir3Codi) {

		Map<String, OrganismeDto> organigrama = new HashMap<>();
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitatDir3Codi(entitatDir3Codi);
		if (organs == null || organs.isEmpty()) {
			return organigrama;
		}

		OrganGestorEntity arrel = organGestorRepository.findByCodi(entitatDir3Codi);
		HashMap<String, List<OrganGestorEntity>> organsMap = organsToMap(organs);
		organToOrganigrama(arrel, organsMap, organigrama);
		return organigrama;
	}

	private HashMap<String, List<OrganGestorEntity>> organsToMap(final List<OrganGestorEntity> organs) {
		HashMap<String, List<OrganGestorEntity>> organsMap = new HashMap<>();
		for (OrganGestorEntity organ: organs) {
//			if (OrganGestorEstatEnum.V.equals(organ.getEstat()) || OrganGestorEstatEnum.T.equals(organ.getEstat())) {    // Unitats Vigents o Transitòries

				if (organsMap.containsKey(organ.getCodiPare())) {
					List<OrganGestorEntity> fills = organsMap.get(organ.getCodiPare());
					fills.add(organ);
				} else {
					List<OrganGestorEntity> fills = new ArrayList<>();
					fills.add(organ);
					organsMap.put(organ.getCodiPare(), fills);
				}
//			}
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
		organigrama.put(
				organ.getCodi(),
				OrganismeDto.builder()
						.codi(organ.getCodi())
						.nom(organ.getNom())
						.pare(organ.getCodiPare())
						.fills(codisFills)
						.build());

		if (fills != null)
			for (OrganGestorEntity fill : fills)
				organToOrganigrama(fill, organsMap, organigrama);
	}

	@Cacheable(value = "llistarNivellsAdministracions")
	public List<CodiValor> llistarNivellsAdministracions() {
		return pluginHelper.llistarNivellsAdministracions();
	}
	
	
	@Cacheable(value = "llistarComunitatsAutonomes")
	public List<CodiValor> llistarComunitatsAutonomes() {
		return pluginHelper.llistarComunitatsAutonomes();
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

	@CacheEvict(value = {"procsersPermis", "procedimentEntitiesPermis", "procsersPermisMenu", "procedimentEntitiesPermisMenu"}, allEntries = true)
	public void evictFindProcedimentServeisWithPermis() {
	}
	
	@CacheEvict(value = {"procedimentsOrganPermis", "procedimentEntitiessOrganPermis"}, allEntries = true)
	public void evictFindProcedimentsOrganWithPermis() {
	}
	
	@CacheEvict(value = {"organsPermis", "organsEntitiesPermis"}, allEntries = true)
	public void evictFindOrgansGestorWithPermis() {
	}

	@CacheEvict(value = "unitatPerCodi", allEntries = true)
	public void evictUnitatPerCodi() {
	}

	@CacheEvict(value = "findUsuarisAmbPermis", key="#procedimentId.concat('-').concat(#codiOrgan)")
	public void evictFindUsuarisAmbPermis(String procedimentId, String codiOrgan) {
	}

	public void clearCache(String cacheName) {
		cacheManager.getCache(cacheName).clear();
	}

	public void clearAllCaches() {
		for(String cacheName : cacheManager.getCacheNames()) {
			clearCache(cacheName);
		}
	}

	public long getCacheSize(String cacheName)
	{
		Cache cache = cacheManager.getCache(cacheName);
		Object nativeCache = cache.getNativeCache();
		if (nativeCache instanceof net.sf.ehcache.Ehcache) {
			net.sf.ehcache.Ehcache ehCache = (net.sf.ehcache.Ehcache) nativeCache;
			return ehCache.getStatistics().getLocalHeapSizeInBytes();
		}
		return 0L;
	}

	public long getTotalEhCacheSize() {
		long totalSize = 0L;
		for (String cacheName : cacheManager.getCacheNames()) {
			totalSize = getCacheSize(cacheName);
		}
		return totalSize;
	}
}
