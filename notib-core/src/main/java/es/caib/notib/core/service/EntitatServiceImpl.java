/**
 * 
 */
package es.caib.notib.core.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.EntitatTipusDocEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.helper.UsuariHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EntitatTipusDocRepository;
import es.caib.notib.core.security.ExtendedPermission;

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
	private EntitatTipusDocRepository entitatTipusDocRepository;
	
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
				false,
				false );
		
		EntitatEntity entity = EntitatEntity.getBuilder(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getTipus(),
				entitat.getDir3Codi(),
				entitat.getDir3CodiReg(),
				entitat.getApiKey(),
				entitat.isAmbEntregaDeh(),
				entitat.isAmbEntregaCie(),
				entitat.getLogoCapBytes(),
				entitat.getLogoPeuBytes(),
				entitat.getColorFons(),
				entitat.getColorLletra(),
				entitat.getTipusDocDefault().getTipusDocEnum()).
				descripcio(entitat.getDescripcio()).
				build();
		
		EntitatEntity entitatSaved = entitatRepository.save(entity);
		
		if (entitat.getTipusDoc() != null) {
			for (TipusDocumentDto tipusDocument : entitat.getTipusDoc()) {
				EntitatTipusDocEntity tipusDocEntity = EntitatTipusDocEntity.getBuilder(
						entitatSaved, 
						tipusDocument.getTipusDocEnum()).build();
				entitatTipusDocRepository.save(tipusDocEntity);
			}
		}
		
		return conversioTipusHelper.convertir(
				entitatSaved,
				EntitatDto.class);
	}

	@Transactional
	@Override
	public EntitatDto update(EntitatDto entitat) {
		logger.debug("Actualitzant entitat existent (entitat=" + entitat + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false,
				false );
		byte[] logoCapActual = null;
		byte[] logoPeuActual = null;
		EntitatEntity entity = entitatRepository.findOne(entitat.getId());
		List<EntitatTipusDocEntity> tipusDocsEntity = entitatTipusDocRepository.findByEntitat(entity);
		
		if (tipusDocsEntity != null && !tipusDocsEntity.isEmpty()) {
			for (TipusDocumentDto tipusDocDto : entitat.getTipusDoc()) {
				 entitatTipusDocRepository.deleteNotInList(
						entitat.getId(),
						tipusDocDto.getTipusDocEnum());
			}
		}
		if (entitat.getTipusDoc().isEmpty()) {
			entitatTipusDocRepository.delete(tipusDocsEntity);
		}
		
		if ((entitat.getTipusDoc() != null && entitat.getTipusDoc().size() > 1) || tipusDocsEntity.isEmpty()) {
			for (TipusDocumentDto tipusDocument : entitat.getTipusDoc()) {
				EntitatTipusDocEntity tipusDocumentActual = entitatTipusDocRepository.findByEntitatAndTipus(entity.getId(), tipusDocument.getTipusDocEnum());
				if (tipusDocumentActual == null) {
					EntitatTipusDocEntity tipusDocEntity = EntitatTipusDocEntity.getBuilder(
							entity, 
							tipusDocument.getTipusDocEnum()).build();
					entitatTipusDocRepository.save(tipusDocEntity);
				}
			}
		}
		if (!entitat.isEliminarLogoCap()) {
			if (entitat.getLogoCapBytes() != null && entitat.getLogoCapBytes().length != 0) {
				logoCapActual = entitat.getLogoCapBytes();
			} else {
				logoCapActual = entity.getLogoCapBytes();
			}
		}
		
		if (!entitat.isEliminarLogoPeu()) {
			if (entitat.getLogoPeuBytes() != null && entitat.getLogoPeuBytes().length != 0) {
				logoPeuActual = entitat.getLogoPeuBytes();
			} else {
				logoPeuActual = entity.getLogoPeuBytes();
			}
		}
		
		entity.update(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getTipus(),
				entitat.getDir3Codi(),
				entitat.getDir3CodiReg(),
				entitat.getApiKey(),
				entitat.isAmbEntregaDeh(),
				entitat.isAmbEntregaCie(),
				entitat.getDescripcio(),
				logoCapActual,
				logoPeuActual,
				entitat.getColorFons(),
				entitat.getColorLletra(),
				entitat.getTipusDocDefault().getTipusDocEnum());
		return conversioTipusHelper.convertir(
				entity,
				EntitatDto.class);
	}
	
	@Override
	public List<TipusDocumentDto> findTipusDocumentByEntitat(Long entitatId) {
		List<TipusDocumentDto> tipusDocumentsDto = new ArrayList<TipusDocumentDto>();
		
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		
		List<EntitatTipusDocEntity> tipusDocsEntity = entitatTipusDocRepository.findByEntitat(entitat);
		
		for (EntitatTipusDocEntity entitatTipusDocEntity : tipusDocsEntity) {
			TipusDocumentDto tipusDocumentDto = new TipusDocumentDto();
			tipusDocumentDto.setTipusDocEnum(entitatTipusDocEntity.getTipusDocEnum());
			tipusDocumentsDto.add(tipusDocumentDto);
		}
		return tipusDocumentsDto;
	}
	
	@Override
	public TipusDocumentEnumDto findTipusDocumentDefaultByEntitat(Long entitatId) {
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		
		return entitat.getTipusDocDefault();
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
				false,
				false );
		EntitatEntity entitat = entitatRepository.findOne(id);
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
				false,
				false );
		EntitatEntity entitat = entitatRepository.findOne( id );
		List<EntitatTipusDocEntity> tipusDocsEntity = entitatTipusDocRepository.findByEntitat(entitat);
		if (!tipusDocsEntity.isEmpty()) {
			entitatTipusDocRepository.delete(tipusDocsEntity);
		}
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
				null,
				true,
				true,
				true );
		TipusDocumentDto tipusDocumentDto = new TipusDocumentDto();
		EntitatEntity entitat = entitatRepository.findOne(id);
		EntitatDto entitatDto = conversioTipusHelper.convertir(
				entitatRepository.findOne(id),
				EntitatDto.class);
		tipusDocumentDto.setEntitat(entitat.getId());
		tipusDocumentDto.setTipusDocEnum(entitat.getTipusDocDefault());
		entitatDto.setTipusDocDefault(tipusDocumentDto);
		return entitatDto;
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {
		logger.debug("Consulta de l'entitat amb codi (codi=" + codi + ")");
		EntitatEntity entitat = entitatRepository.findByCodi(codi);
		if (entitat == null) return null;
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				true );
		return conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByDir3codi(
			String dir3codi) {
		logger.debug("Consulta de l'entitat amb codi DIR3 (dir3codi=" + dir3codi + ")");
		EntitatDto dto = conversioTipusHelper.convertir(
				entitatRepository.findByDir3Codi(dir3codi),
				EntitatDto.class);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAll() {
		logger.debug("Consulta de totes les entitats");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false,
				false );
		return conversioTipusHelper.convertirList(
					entitatRepository.findAll(),
					EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les entitats paginades (paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false,
				false );
		PaginaDto<EntitatDto> resposta = paginacioHelper.toPaginaDto(
					entitatRepository.findByFiltre(
							paginacioParams.getFiltre(), 
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					EntitatDto.class);
		for (EntitatDto entitat: resposta.getContingut()) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					entitat.getId(),
					EntitatEntity.class);
			entitat.setPermisos(permisos);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAccessiblesUsuariActual(String rolActual) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta les entitats accessibles per l'usuari actual (usuari=" + auth.getName() + ")");
		return permisosHelper.findEntitatsAccessiblesUsuari(auth.getName(), rolActual);
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFindByEntitatId(
			Long entitatId) {
		logger.debug("Consulta dels permisos de l'entitat (entitatId=" + entitatId + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				true );
		return permisosHelper.findPermisos(
				entitatId,
				EntitatEntity.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void permisUpdate(
			Long entitatId,
			PermisDto permis) {
		logger.debug("Modificació com a superusuari del permis de l'entitat (" +
				"entitatId=" + entitatId + ", " +
				"permis=" + permis + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false,
				false );
		permisosHelper.updatePermis(
				entitatId,
				EntitatEntity.class,
				permis);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void permisDelete(
			Long entitatId,
			Long permisId) {
		logger.debug("Eliminació com a superusuari del permis de l'entitat (" +
				"entitatId=" + entitatId + ", " +
				"permisId=" + permisId + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false );
		permisosHelper.deletePermis(
				entitatId,
				EntitatEntity.class,
				permisId);
	}


	@Override
	public boolean hasPermisUsuariEntitat() {
		List<EntitatDto> resposta = entityComprovarHelper.findPermisEntitat(
				new Permission[] {
						ExtendedPermission.USUARI}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}

	@Override
	public boolean hasPermisAdminEntitat() {		
		List<EntitatDto> resposta = entityComprovarHelper.findPermisEntitat(
				new Permission[] {
						ExtendedPermission.ADMINISTRADORENTITAT}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}

	@Override
	public boolean hasPermisAplicacioEntitat() {		
		List<EntitatDto> resposta = entityComprovarHelper.findPermisEntitat(
				new Permission[] {
						ExtendedPermission.APLICACIO}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	/*@Override
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
	}*/
	
	
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


	@Transactional
	@Override
	public byte[] getCapLogo() throws NoSuchFileException, IOException{

		String filePath = PropertiesHelper.getProperties().getProperty("es.caib.notib.capsalera.logo");
		Path path = Paths.get(filePath);
		
		return Files.readAllBytes(path);
	}
	
	@Transactional
	@Override
	public byte[] getPeuLogo() throws NoSuchFileException, IOException{

		String filePath = PropertiesHelper.getProperties().getProperty("es.caib.notib.peu.logo");
		Path path = Paths.get(filePath);
		
		return Files.readAllBytes(path);
	}



}
