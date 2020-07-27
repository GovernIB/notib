/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
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
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private UsuariHelper usuariHelper;

	@Cacheable(value = "entitatsUsuari", key="#usuariCodi")
	public List<EntitatDto> findEntitatsAccessiblesUsuari(
			String usuariCodi) {
		logger.debug("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<EntitatEntity> entitats = entitatRepository.findByActiva(true);
		permisosHelper.filterGrantedAny(
				entitats,
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitat) {
						return entitat.getId();
					}
				},
				//revisar
				EntitatEntity.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRADORENTITAT},
				auth);
		
		List<EntitatDto> resposta = conversioTipusHelper.convertirList(
				entitats,
				EntitatDto.class);
		
		for(EntitatDto dto : resposta) dto.setUsuariActualAdministradorEntitat(true);
		
		return resposta;
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
		List<ObjetoDirectorio> organismesDir3 = pluginHelper.llistarOrganismesPerEntitat(entitatcodi);
		if (organismesDir3 != null) {
			for (ObjetoDirectorio organismeRegistre : organismesDir3) {
				OrganismeDto organisme = new OrganismeDto();
				organisme.setCodi(organismeRegistre.getCodi());
				organisme.setNom(organismeRegistre.getDenominacio());
				organismes.add(organisme);
			}
		}
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
	
	@CacheEvict(value = "getPermisosEntitatsUsuariActual", key="#auth.name")
	public void evictGetPermisosEntitatsUsuariActual(Authentication auth) {
//		System.out.println("Esborram cache permisos de " + auth.getName());
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}
