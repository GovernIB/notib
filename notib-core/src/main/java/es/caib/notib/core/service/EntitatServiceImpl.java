/**
 * 
 */
package es.caib.notib.core.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.UsuariHelper;
import es.caib.notib.core.repository.EntitatRepository;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EntitatServiceImpl implements EntitatService {

	@Resource
	private EntitatRepository entitatRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	
	@Resource
	private UsuariHelper usuariHelper;



	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto create(EntitatDto entitat) {
		logger.debug("Creant una nova entitat (entitat=" + entitat + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false );
		EntitatEntity entity = EntitatEntity.getBuilder(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getDescripcio(),
				entitat.getTipus(),
				entitat.getCif(),
				entitat.getDir3Codi()).build();
		return conversioTipusHelper.convertir(
				entitatRepository.save(entity),
				EntitatDto.class);
	}

	@Transactional
	@Override
	public EntitatDto update(EntitatDto entitat) {
		logger.debug("Actualitzant entitat existent (entitat=" + entitat + ")");
		entityComprovarHelper.comprovarPermisos(
				entitat.getId(),
				true,
				true );
		EntitatEntity entity = entitatRepository.findOne( entitat.getId() );
		entity.update(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getDescripcio(),
				entitat.getTipus(),
				entitat.getCif(),
				entitat.getDir3Codi());
		return conversioTipusHelper.convertir(
				entity,
				EntitatDto.class);
	}

	@Transactional
	@Override
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		
		logger.debug("Actualitzant propietat activa d'una entitat existent ("
				+ "id=" + id + ", "
				+ "activa=" + activa + ")");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false );
		
		EntitatEntity entitat = entitatRepository.findOne( id );
		
		entitat.updateActiva(activa);
		
		return conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
	}
	
	
	
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto delete(
			Long id) {
		
		logger.debug("Esborrant entitat (id=" + id +  ")");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false );
		
		EntitatEntity entitat = entitatRepository.findOne( id );
		
		entitatRepository.delete(entitat);
		
		permisosHelper.deleteAcl(
				entitat.getId(),
				EntitatEntity.class);
		
		return conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findById(
			Long id) {
		logger.debug("Consulta de l'entitat (id=" + id + ")");
		entityComprovarHelper.comprovarPermisos(
				id,
				true,
				true);
		return conversioTipusHelper.convertir(
				entitatRepository.findOne(id),
				EntitatDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {
		logger.debug("Consulta de l'entitat amb codi (codi=" + codi + ")");
		EntitatEntity entitat = entitatRepository.findByCodi(codi);
		if (entitat == null) return null;
		entityComprovarHelper.comprovarPermisos(
				entitat.getId(),
				true,
				true);
		return conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCif(
			String cif ) {
		
		logger.debug("Consulta de l'entitat (cif=" + cif + ")");
		
//		entityComprovarHelper.comprovarPermisos(
//				id,
//				true,
//				true );
		
		EntitatDto dto = conversioTipusHelper.convertir(
				entitatRepository.findByCif( cif ),
				EntitatDto.class);
		
		return dto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByDir3(
			String dir3 ) {
		
		logger.debug("Consulta de l'entitat (dir3=" + dir3 + ")");
		
//		entityComprovarHelper.comprovarPermisos(
//				id,
//				true,
//				true );
		
		EntitatDto dto = conversioTipusHelper.convertir(
				entitatRepository.findByDir3Codi( dir3 ),
				EntitatDto.class);
		
		return dto;
	}
	
	@Override
	public List<EntitatDto> findAll() {
		
		return conversioTipusHelper.convertirList(
					entitatRepository.findAll(),
					EntitatDto.class
				);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les entitats paginades (paginacioParams=" + paginacioParams + ")");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false );
		
		PaginaDto<EntitatDto> resposta = paginacioHelper.toPaginaDto(
					entitatRepository.findByFiltre(
							paginacioParams.getFiltre(), 
							paginacioHelper.toSpringDataPageable(paginacioParams)
							),
					EntitatDto.class);
		
		for (EntitatDto e: resposta.getContingut()) {
			List<PermisDto> permisos = permisosHelper.findPermisos(e.getId(), EntitatEntity.class);
			e.setPermisos( permisos );
		}
		
		return resposta;
	}
	
	@Override
	public List<EntitatDto> findByEntitatId(Long entitatId) {
		
		entityComprovarHelper.comprovarPermisos(
				entitatId,
				false,
				true );
		
		List<EntitatEntity> entitats = entitatRepository.findListById( entitatId );
		List<EntitatDto> resposta = conversioTipusHelper.convertirList( entitats, EntitatDto.class );
		
		for (EntitatDto e: resposta) {
			List<PermisDto> permisos = permisosHelper.findPermisos(e.getId(), EntitatEntity.class);
			e.setPermisos( permisos );
		}
		
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAccessiblesUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta les entitats accessibles per l'usuari actual (usuari=" + auth.getName() + ")");
		return cacheHelper.findEntitatsAccessiblesUsuari(auth.getName());
	}

	@Transactional
	@Override
	public List<PermisDto> findPermis(
			Long id) {
		
		logger.debug("Consulta com a administrador dels permisos de l'entitat (id=" + id + ")");
		
		entityComprovarHelper.comprovarPermisos(
				id,
				true,
				true );
		
		return permisosHelper.findPermisos(
				id,
				EntitatEntity.class);
	}
	
	
	
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void updatePermis(
			Long id,
			PermisDto permis) {
		
		logger.debug("Modificació com a superusuari del permis de l'entitat ("
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
		
		entityComprovarHelper.comprovarPermisos(
				id,
				true,
				true );
		
		permisosHelper.updatePermis(
				id,
				EntitatEntity.class,
				permis);
	}
	
	
	
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void deletePermis(
			Long id,
			Long permisId) {
		
		logger.debug("Eliminació com a superusuari del permis de l'entitat ("
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		
		entityComprovarHelper.comprovarPermisos(
				id,
				true,
				true);
		
		permisosHelper.deletePermis(
				id,
				EntitatEntity.class,
				permisId);
	}
	
	
	@Override
	@Transactional
	public Map<Long, List<PermisDto>> findPermisos(List<Long> entitatIds) {
		
		logger.debug("Consulta com a administrador dels permisos de les entitats (" + entitatIds.toString() + ")");
		
		for(Long id : entitatIds)
			entityComprovarHelper.comprovarPermisos(
					id,
					true,
					true );
		
		return permisosHelper.findPermisos(
				entitatIds,
				EntitatEntity.class);
	}
	
	
//	@Transactional
//	@Override
//	public List<PermisDto> findPermisAdmin1( Long id ) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		logger.debug("Consulta com a administrador del permis de l'entitat (id=" + id + ")");
//		entityComprovarHelper.comprovarEntitat(
//				id,
//				false,
//				false,
//				false);
//		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
//				id,
//				EntitatEntity.class,
//				new Permission[] {(Permission) ExtendedPermission.ADMINISTRATION},
//				auth);
//		if (!esAdministradorEntitat) {
//			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
//			throw new SecurityException("Sense permisos per administrar aquesta entitat");
//		}
//		return permisosHelper.findPermisos(
//				id,
//				EntitatEntity.class);
//	}

//	@Transactional
//	@Override
//	@CacheEvict(value = "entitatsUsuari", allEntries = true)
//	public void updatePermisAdmin(
//			Long id,
//			PermisDto permis) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		logger.debug("Modificació com a administrador del permis de l'entitat ("
//				+ "id=" + id + ", "
//				+ "permis=" + permis + ")");
//		entityComprovarHelper.comprovarEntitat(
//				id,
//				false,
//				false,
//				false);
//		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
//				id,
//				EntitatEntity.class,
//				new Permission[] {ExtendedPermission.ADMINISTRATION},
//				auth);
//		if (!esAdministradorEntitat) {
//			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
//			throw new SecurityException("Sense permisos per administrar aquesta entitat");
//		}
//		permisosHelper.updatePermis(
//				id,
//				EntitatEntity.class,
//				permis);
//	}
//	@Transactional
//	@Override
//	@CacheEvict(value = "entitatsUsuari", allEntries = true)
//	public void deletePermisAdmin(
//			Long id,
//			Long permisId) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		logger.debug("Eliminació com a administrador del permis de l'entitat ("
//				+ "id=" + id + ", "
//				+ "permisId=" + permisId + ")");
//		entityComprovarHelper.comprovarEntitat(
//				id,
//				false,
//				false,
//				false);
//		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
//				id,
//				EntitatEntity.class,
//				new Permission[] {ExtendedPermission.ADMINISTRATION},
//				auth);
//		if (!esAdministradorEntitat) {
//			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
//			throw new SecurityException("Sense permisos per administrar aquesta entitat");
//		}
//		permisosHelper.deletePermis(
//				id,
//				EntitatEntity.class,
//				permisId);
//	}



	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);
	

}
