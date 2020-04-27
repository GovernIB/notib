/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.List;

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
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
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
		
//		usuarisEntitatHelper.omplirUsuarisPerEntitats(
//				resposta,
//				false);
		
		//////////////////////////////////////////////////////////////////
		
//		List<EntitatDto> entitats = conversioTipusHelper.convertirList(
//				entitatRepository.findAll(),
//				EntitatDto.class );
//		
//		List<EntitatDto> result = new ArrayList<>();
//		for(EntitatDto e : entitats) {
//			List<PermisDto> permisos = permisosHelper.findPermisos(
//					e.getId(),
//					EntitatEntity.class);
//			for(PermisDto p : permisos) {
//				if(p.getNom().equals(usuariCodi)) {
//					e.setUsuariActualRepresentant(p.isRepresentant());
//					result.add(e);
//				}
//			}
//		}
//		
//		return result;
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
	
	@CacheEvict(value = "findPermisProcedimentsUsuariActualAndEntitat", key="#entitatId")
	public void evictFindPermisProcedimentsUsuariActualAndEntitat(Long entitatId) {
	}
	
	@CacheEvict(value = "findByGrupAndPermisProcedimentsUsuariActualAndEntitat", key="#entitatId")
	public void evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(Long entitatId) {
	}
	
	@CacheEvict(value = "findByPermisProcedimentsUsuariActual", key="#entitatId")
	public void evictFindByPermisProcedimentsUsuariActual(Long entitatId) {
	}
	

	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);

}
