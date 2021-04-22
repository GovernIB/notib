/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.EntitatTipusDocEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EntitatTipusDocRepository;
import es.caib.notib.core.security.ExtendedPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private EntitatTipusDocRepository entitatTipusDocRepository;
	@Resource
	private AplicacioRepository aplicacioRepository;
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
	private PermisosCacheable permisosCacheable;
	@Resource
	private MetricsHelper metricsHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;

	@Audita(entityType = TipusEntitat.ENTITAT, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto create(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
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
					entitat.getTipusDocDefault().getTipusDocEnum(),
					entitat.getOficina(),
					entitat.getNomOficinaVirtual(),
					entitat.isLlibreEntitat(),
					entitat.getLlibre(),
					entitat.getLlibreNom(),
					entitat.isOficinaEntitat()).
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
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.ENTITAT, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	public EntitatDto update(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant entitat existent (entitat=" + entitat + ")");
//			entityComprovarHelper.comprovarPermisos(
//					null,
//					true,
//					false,
//					false );
			entityComprovarHelper.comprovarEntitat(
					entitat.getId(),
					true,
					true,
					false,
					false);
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
			if (entitat.getTipusDoc() == null || entitat.getTipusDoc().isEmpty()) {
				entitatTipusDocRepository.delete(tipusDocsEntity);
			}
			
			if ((entitat.getTipusDoc() != null && entitat.getTipusDoc().size() > 1) || tipusDocsEntity.isEmpty()) {
				if (entitat.getTipusDoc() != null)
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
					entitat.getTipusDocDefault().getTipusDocEnum(),
					entitat.getOficina(),
					entitat.getNomOficinaVirtual(),
					entitat.isLlibreEntitat(),
					entitat.getLlibre(),
					entitat.getLlibreNom(),
					entitat.isOficinaEntitat());
			return conversioTipusHelper.convertir(
					entity,
					EntitatDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.ENTITAT, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
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
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.ENTITAT, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto delete(
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
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
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public List<TipusDocumentDto> findTipusDocumentByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<TipusDocumentDto> tipusDocumentsDto = new ArrayList<TipusDocumentDto>();
			
			EntitatEntity entitat = entitatRepository.findOne(entitatId);
			
			List<EntitatTipusDocEntity> tipusDocsEntity = entitatTipusDocRepository.findByEntitat(entitat);
			
			for (EntitatTipusDocEntity entitatTipusDocEntity : tipusDocsEntity) {
				TipusDocumentDto tipusDocumentDto = new TipusDocumentDto();
				tipusDocumentDto.setTipusDocEnum(entitatTipusDocEntity.getTipusDocEnum());
				tipusDocumentsDto.add(tipusDocumentDto);
			}
			return tipusDocumentsDto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public TipusDocumentEnumDto findTipusDocumentDefaultByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entitatRepository.findOne(entitatId);
			return entitat.getTipusDocDefault();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findById(
			Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'entitat (id=" + entitatId + ")");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			return conversioTipusHelper.convertir(
					entitat,
					EntitatDto.class);
//			entityComprovarHelper.comprovarPermisos(
//					null,
//					false,
//					false,
//					true );
//			TipusDocumentDto tipusDocumentDto = new TipusDocumentDto();
//			tipusDocumentDto.setEntitat(entitat.getId());
//			tipusDocumentDto.setTipusDocEnum(entitat.getTipusDocDefault());
//			entitatDto.setTipusDocDefault(tipusDocumentDto);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
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
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByDir3codi(
			String dir3codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'entitat amb codi DIR3 (dir3codi=" + dir3codi + ")");
			EntitatDto dto = conversioTipusHelper.convertir(
					entitatRepository.findByDir3Codi(dir3codi),
					EntitatDto.class);
			return dto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de totes les entitats");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false );
			return conversioTipusHelper.convertirList(
						entitatRepository.findAll(),
						EntitatDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
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
				// Permisos
				List<PermisDto> permisos = permisosHelper.findPermisos(
						entitat.getId(),
						EntitatEntity.class);
				entitat.setPermisos(permisos);
				
				// Aplicacions
				entitat.setNumAplicacions(aplicacioRepository.countByEntitatId(entitat.getId()));
			}
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAccessiblesUsuariActual(String rolActual) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			logger.debug("Consulta les entitats accessibles per l'usuari actual (usuari=" + auth.getName() + ")");
			return permisosCacheable.findEntitatsAccessiblesUsuari(auth.getName(), rolActual);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFindByEntitatId(
			Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos de l'entitat (entitatId=" + entitatId + ")");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true );
			return permisosHelper.findPermisos(
					entitatId,
					EntitatEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void permisUpdate(
			Long entitatId,
			PermisDto permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació com a superusuari del permis de l'entitat (" +
					"entitatId=" + entitatId + ", " +
					"permis=" + permis + ")");
			
			if (TipusEnumDto.ROL.equals(permis.getTipus())) {
				if (permis.getPrincipal().equalsIgnoreCase("tothom")) {
					permis.setPrincipal(permis.getPrincipal().toLowerCase());					
				} else {
					permis.setPrincipal(permis.getPrincipal().toUpperCase());
				}
			} else {
				if (TipusEnumDto.USUARI.equals(permis.getTipus())) {
					permis.setPrincipal(permis.getPrincipal().toLowerCase());
				}
			}
			
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					true,
					false,
					false);
//			entityComprovarHelper.comprovarPermisos(
//					null,
//					true,
//					false,
//					false );
			permisosHelper.updatePermis(
					entitatId,
					EntitatEntity.class,
					permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void permisDelete(
			Long entitatId,
			Long permisId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Eliminació com a superusuari del permis de l'entitat (" +
					"entitatId=" + entitatId + ", " +
					"permisId=" + permisId + ")");
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					true,
					false,
					false);
//			entityComprovarHelper.comprovarPermisos(
//					null,
//					true,
//					true,
//					false );
			permisosHelper.deletePermis(
					entitatId,
					EntitatEntity.class,
					permisId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Override
	public boolean hasPermisUsuariEntitat() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<EntitatDto> resposta = entityComprovarHelper.findPermisEntitat(
					new Permission[] {
							ExtendedPermission.USUARI}
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean hasPermisAdminEntitat() {		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<EntitatDto> resposta = entityComprovarHelper.findPermisEntitat(
					new Permission[] {
							ExtendedPermission.ADMINISTRADORENTITAT}
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean hasPermisAplicacioEntitat() {		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<EntitatDto> resposta = entityComprovarHelper.findPermisEntitat(
					new Permission[] {
							ExtendedPermission.APLICACIO}
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public Map<RolEnumDto, Boolean> getPermisosEntitatsUsuariActual() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				return permisosCacheable.getPermisosEntitatsUsuariActual(auth);
			} else {
				Map<RolEnumDto, Boolean> hasPermisos = new HashMap<RolEnumDto, Boolean>();
				hasPermisos.put(RolEnumDto.tothom, false);
				hasPermisos.put(RolEnumDto.NOT_ADMIN, false);
				hasPermisos.put(RolEnumDto.NOT_APL, false);
				hasPermisos.put(RolEnumDto.NOT_ADMIN_ORGAN, false);
				return hasPermisos;
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OficinaDto> findOficinesEntitat(String dir3codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OficinaDto> oficines = new ArrayList<OficinaDto>();
			try {
				//Recupera les oficines d'una entitat
				oficines = cacheHelper.llistarOficinesEntitat(dir3codi);
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar les oficines de l'entitat amb codi: " + dir3codi;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return oficines;	
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public byte[] getCapLogo() throws NoSuchFileException, IOException{
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String filePath = PropertiesHelper.getProperties().getProperty("es.caib.notib.capsalera.logo");
			Path path = Paths.get(filePath);
			
			return Files.readAllBytes(path);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public byte[] getPeuLogo() throws NoSuchFileException, IOException{
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String filePath = PropertiesHelper.getProperties().getProperty("es.caib.notib.peu.logo");
			Path path = Paths.get(filePath);
			
			return Files.readAllBytes(path);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public LlibreDto getLlibreEntitat(
			String dir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			LlibreDto llibre = new LlibreDto();
			try {
				llibre = cacheHelper.getLlibreOrganGestor(
						dir3Codi,
						dir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'ha pogut recuperar el llibre de l'entitat amb codi Dir3: " + dir3Codi;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return llibre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return organGestorCachable.findOrganigramaByEntitat(entitatCodi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
