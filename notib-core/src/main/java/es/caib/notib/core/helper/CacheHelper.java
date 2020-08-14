/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
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

	@Cacheable(value = "RolsAmbCodi", key="#rolsCodi")
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
				return o1.getCodi().compareTo(o1.getCodi());
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
		organisme.setPare(node.getSuperior());
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
	
	@CacheEvict(value = "findPermisProcedimentsUsuariActualAndEntitat", key="#entitatId")
	public void evictFindPermisProcedimentsUsuariActualAndEntitat(Long entitatId) {
	}
	
	@CacheEvict(value = "findByGrupAndPermisProcedimentsUsuariActualAndEntitat", key="#entitatId")
	public void evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(Long entitatId) {
	}
	
	@CacheEvict(value = "findByPermisProcedimentsUsuariActual", key="#entitatId")
	public void evictFindByPermisProcedimentsUsuariActual(Long entitatId) {
	}
	
	@CacheEvict(value = "organismes", key="#entitatcodi")
	public void evictFindOrganismesByEntitat(String entitatcodi) {
	}
	
	@CacheEvict(value = "organigrama", key="#entitatcodi")
	public void evictFindOrganigramaByEntitat(String entitatcodi) {
	}
	
	@CacheEvict(value = "organsGestorsUsuari", allEntries = true)
	public void evictFindOrgansGestorsAccessiblesUsuari() {
	}
	
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void evictFindEntitatsAccessiblesUsuari() {
	}
	
	@CacheEvict(value = "getPermisosEntitatsUsuariActual", key="#auth.name")
	public void evictGetPermisosEntitatsUsuariActual(Authentication auth) {
//		System.out.println("Esborram cache permisos de " + auth.getName());
	}
	
	
//	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}
