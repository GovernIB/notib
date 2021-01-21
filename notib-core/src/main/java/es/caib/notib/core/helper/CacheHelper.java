/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.usuari.DadesUsuari;

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
	private EntitatRepository entitatRepository;
	@Resource
	private ProcedimentRepository procedimentRepository;
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
	private UsuariHelper usuariHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private CacheManager cacheManager;

	@Cacheable(value = "entitatsUsuari", key="#usuariCodi.concat('-').concat(#rolActual)")
	public List<EntitatDto> findEntitatsAccessiblesUsuari(
			String usuariCodi,
			String rolActual) {
		return permisosHelper.findEntitatsAccessiblesUsuari(usuariCodi, rolActual);
	}
	
	@Cacheable(value = "organsGestorsUsuari", key="#auth.name")
	public List<OrganGestorDto> findOrgansGestorsAccessiblesUsuari(Authentication auth) {
		List<OrganGestorEntity> organsGestors = organGestorRepository.findAll();
		Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
		
		permisosHelper.filterGrantedAny(
				organsGestors,
				new ObjectIdentifierExtractor<OrganGestorEntity>() {
					public Long getObjectIdentifier(OrganGestorEntity organGestor) {
						return organGestor.getId();
					}
				},
				OrganGestorEntity.class,
				permisos,
				auth);

		if (entityComprovarHelper.getGenerarLogsPermisosOrgan()) {
			log.info("### PERMISOS - Obtenir Òrgans gestors #####################################");
			log.info("### -----------------------------------------------------------------------");
			log.info("### Usuari: " + auth.getName());
			log.info("### Òrgans: ");
			if (organsGestors != null)
				for (OrganGestorEntity organGestor : organsGestors) {
					log.info("### # " + organGestor.getCodi() + " - " + organGestor.getNom());
				}
			log.info("### -----------------------------------------------------------------------");
		}
		return conversioTipusHelper.convertirList(
				organsGestors, 
				OrganGestorDto.class);
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
	
	@Cacheable(value = "organismes", key="#entitatcodi")
	public List<OrganismeDto> findOrganismesByEntitat(
			String entitatcodi) {
		List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
		Map<String, NodeDir3> organigramaDir3 = pluginHelper.getOrganigramaPerEntitat(entitatcodi);
		for (String codi: organigramaDir3.keySet()) {
			OrganismeDto organisme = new OrganismeDto();
			NodeDir3 node = organigramaDir3.get(codi);
			organisme.setCodi(node.getCodi());
			organisme.setNom(node.getDenominacio());
			organismes.add(organisme);
		}
		Collections.sort(organismes, new Comparator<OrganismeDto>() {
			@Override
			public int compare(OrganismeDto o1, OrganismeDto o2) {
				return o1.getCodi().compareTo(o2.getCodi());
			}
		});
		return organismes;
	}
	
	@Cacheable(value = "organigrama", key="#entitatcodi")
	public Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatcodi) {
		Map<String, OrganismeDto> organigrama = new HashMap<String, OrganismeDto>();
		Map<String, NodeDir3> organigramaDir3 = pluginHelper.getOrganigramaPerEntitat(entitatcodi);
		if (organigramaDir3 != null) {
			for (String organ : organigramaDir3.keySet()) {
				organigrama.put(organ, nodeDir3ToOrganisme(organigramaDir3.get(organ)));
			}
		}
		return organigrama;
	}
	
	private OrganismeDto nodeDir3ToOrganisme(NodeDir3 node) {
		OrganismeDto organisme = new OrganismeDto();
		organisme.setCodi(node.getCodi());
		organisme.setNom(node.getDenominacio());
		String pare = node.getSuperior();
		if (pare != null && !pare.isEmpty()) {
			int size = pare.indexOf(" - ");
			if (size > 0)
			pare = pare.substring(0, pare.indexOf(" - "));
		}
		organisme.setPare(pare);
		List<String> fills = null;
		if (node.getFills() != null && !node.getFills().isEmpty()) {
			fills = new ArrayList<String>();
			for (NodeDir3 fill: node.getFills()) {
				fills.add(fill.getCodi());
			}
		}
		organisme.setFills(fills);
		
		return organisme;
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
	
//	@CacheEvict(value = "findPermisProcedimentsUsuariActualAndEntitat", key="#entitatId")
//	public void evictFindPermisProcedimentsUsuariActualAndEntitat(Long entitatId) {
//	}
//	
//	@CacheEvict(value = "findByGrupAndPermisProcedimentsUsuariActualAndEntitat", key="#entitatId")
//	public void evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(Long entitatId) {
//	}
//	
//	@CacheEvict(value = "findByPermisProcedimentsUsuariActual", key="#entitatId")
//	public void evictFindByPermisProcedimentsUsuariActual(Long entitatId) {
//	}
	
	@CacheEvict(value = "organismes", key="#entitatcodi")
	public void evictFindOrganismesByEntitat(String entitatcodi) {
	}
	
	@CacheEvict(value = "organigrama", key="#entitatcodi")
	public void evictFindOrganigramaByEntitat(String entitatcodi) {
	}
	
	@CacheEvict(value = "organigramaPlugin", key="#entitatcodi")
	public void evictFindOrganigramaPlugin(String entitatCodi) {
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
	
	@CacheEvict(value = "organsGestorsUsuari", allEntries = true)
	public void evictFindOrgansGestorsAccessiblesUsuari() {
	}
	
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void evictFindEntitatsAccessiblesUsuari() {
	}
	
	@CacheEvict(value = "getPermisosEntitatsUsuariActual", key="#auth.name")
	public void evictGetPermisosEntitatsUsuariActual(Authentication auth) {
	}

	@CacheEvict(value = "getPermisosEntitatsUsuariActual", allEntries = true)
	public void evictAllPermisosEntitatsUsuariActual() {
	}
	
	public void clearCache(String value) {
		cacheManager.getCache(value).clear();
	}
	
//	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}
