package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
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
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private CacheManager cacheManager;

	public static String appVersion;

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
	public List<OficinaDto> getOficinesSIRUnitat(
			Map<String, NodeDir3> arbreUnitats,
			String codiDir3Organ) {
		return pluginHelper.oficinesSIRUnitat(
				codiDir3Organ,
				arbreUnitats);
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
	public List<OficinaDto> getOficinesSIREntitat(
			String codiDir3Entitat) {
		return pluginHelper.oficinesSIREntitat(codiDir3Entitat);
	}
	
	@Cacheable(value = "organigramaOriginal", key="#entitatcodi")
	public Map<String, NodeDir3> findOrganigramaNodeByEntitat(String entitatcodi) {
		return  pluginHelper.getOrganigramaPerEntitat(entitatcodi);
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

	@CacheEvict(value = {"procedimentsPermis", "procedimentEntitiesPermis"}, allEntries = true)
	public void evictFindProcedimentsWithPermis() {
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
