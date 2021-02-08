package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.exception.NoPermisosException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementació del servei de gestió de òrgans gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class OrganGestorServiceImpl implements OrganGestorService{

	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private GrupRepository grupReposity;
	@Resource
	private PagadorPostalRepository pagadorPostalReposity;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private ProcedimentHelper procedimentHelper;
	@Resource
	private OrganGestorHelper organGestorHelper;
	

	@Override
	@Transactional
	public OrganGestorDto create(OrganGestorDto dto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i
			//		que l'Organ que crea es fill d'almenys un dels Organs que administra 
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					dto.getEntitatId()); 
//					true, 
//					false, 
//					false);
			
			OrganGestorEntity organGestor = OrganGestorEntity.getBuilder(
					dto.getCodi(),
					dto.getNom(),
					entitat,
					dto.getLlibre(),
					dto.getLlibreNom(),
					dto.getOficina() != null ? dto.getOficina().getCodi() : null,
					dto.getOficina() != null ? dto.getOficina().getNom() : null).build();
			return conversioTipusHelper.convertir(
					organGestorRepository.save(organGestor),
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public OrganGestorDto delete(
			Long entitatId,
			Long organId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i
			//		verificar que almenys un dels organs que administra es pare del que vol eliminar.
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId);
//					true,
//					false,
//					false);
			
			OrganGestorEntity organGestorEntity = entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					organId);
		
			//Eliminar organ
			organGestorRepository.delete(organGestorEntity);

			return conversioTipusHelper.convertir(
					organGestorEntity, 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public OrganGestorDto updateOficina(OrganGestorDto dto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(
					dto.getEntitatId(), 
					false, 
					true, 
					false);
			
			OrganGestorEntity organGestor = organGestorRepository.findOne(dto.getId());
			organGestor.updateOficina(
					dto.getOficina().getCodi(),
					dto.getOficina().getNom());
			return conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean organGestorEnUs(Long organId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			boolean organEnUs=false;
			//1) Si té permisos
			organEnUs=permisosHelper.hasAnyPermis(organId,OrganGestorEntity.class);
			if (!organEnUs) {
				//2) Revisam si té procediments assignats
				List<ProcedimentEntity> procedimentsOrganGestor = procedimentRepository.findByOrganGestorId(organId);
				organEnUs=procedimentsOrganGestor != null && !procedimentsOrganGestor.isEmpty();
				if (!organEnUs) {
					//3) revisar si té grups
					List<GrupEntity> grupsByOrganGestor = grupReposity.findByOrganGestorId(organId);
					organEnUs=grupsByOrganGestor != null && !grupsByOrganGestor.isEmpty();
					
					if (!organEnUs) {
						//4) revisar si té pagadors cie
						List<PagadorCieEntity> pagCiesByOrganGestor = pagadorCieReposity.findByOrganGestorId(organId);
						organEnUs=pagCiesByOrganGestor != null && !pagCiesByOrganGestor.isEmpty();
						if (!organEnUs) {
							//5) revisar si té pagadors postals
							List<PagadorPostalEntity> pagPostalByOrganGestor = pagadorPostalReposity.findByOrganGestorId(organId);
							organEnUs=pagPostalByOrganGestor != null && !pagPostalByOrganGestor.isEmpty();
						}
					}
				
				}
			}
			return organEnUs;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganGestorEntity> organs = organGestorRepository.findAll();
			return conversioTipusHelper.convertirList(
					organs, 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(
					organs, 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CodiValorDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorDto> organsGestors = new ArrayList<CodiValorDto>();
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			for (OrganGestorEntity organ: organs) {
				organsGestors.add(new CodiValorDto(organ.getCodi(), organ.getCodi() + " - " + organ.getNom()));
			}
			return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return conversioTipusHelper.convertirList(
					organGestorRepository.findByProcedimentIds(procedimentIds), 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findDescencentsByCodi(
			Long entitatId, 
			String organCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organCodi);
			return conversioTipusHelper.convertirList(
					organGestorRepository.findByCodiIn(organs),
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			String organActualCodiDir3,
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("llibreCodiNom", new String[] {"llibre"});
			mapeigPropietatsOrdenacio.put("oficinaNom", new String[] {"entitat.oficina"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			
			Page<OrganGestorEntity> organs = null;
			
			
			//Cas d'Administrador d'Entitat
			//	Tots els organs fills de l'Entitat
			if (organActualCodiDir3 == null) {
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
						entitatId); 
//						false, 
//						true, 
//						false);
			
				if (filtre == null) {
					organs = organGestorRepository.findByEntitat(
							entitat,
							pageable);
				} else {
					organs = organGestorRepository.findByEntitatAndFiltre(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOficina() == null || filtre.getOficina().isEmpty(),
							filtre.getOficina() == null ? "" : filtre.getOficina(),
							pageable);
				}
			//Cas d'Administrador d'Organ
			//	Només el l'Organ de l'administrador, i els seus fills (tant de primer nivell com següents)
			}else{
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
						entitatId); 
//						true, 
//						false, 
//						false);
				
				//Comprovació permisos organ
				entityComprovarHelper.comprovarPermisosOrganGestor(organActualCodiDir3);
				//OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat,organActualId);
				List<String> organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
				if (filtre == null) {
					organs = organGestorRepository.findByEntitatAndOrganGestor(
							entitat,
							organGestorsListCodisDir3,
							pageable);
				} else {
					organs = organGestorRepository.findByEntitatAndOrganGestorAndFiltre(
							entitat,
							organGestorsListCodisDir3,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOficina() == null || filtre.getOficina().isEmpty(),
							filtre.getOficina() == null ? "" : filtre.getOficina(),
							pageable);
				}
			}
			
			PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(
					organs,
					OrganGestorDto.class);
			
			for (OrganGestorDto organ: paginaOrgans.getContingut()) {
				List<PermisDto> permisos = permisosHelper.findPermisos(
						organ.getId(),
						OrganGestorEntity.class);
				organ.setPermisos(permisos);
			}
			return paginaOrgans;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	//List<String> obtenirCodisDir3Fills(organGestor)
	
	@Transactional
	@Override
	public void updateNom(Long entitatId, String organGestorCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId); 
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			String denominacio = findDenominacioOrganisme(organGestorCodi);
			if (denominacio != null && !denominacio.isEmpty())
				organGestor.update(denominacio);
			else
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS, 
						"No s'ha pogut obtenir la denominació de l'organ gestor");
			LlibreDto llibreOrgan = cacheHelper.getLlibreOrganGestor(
					entitat.getDir3Codi(),
					organGestor.getCodi());
			if (llibreOrgan != null) {
				organGestor.updateLlibre(llibreOrgan.getCodi(), llibreOrgan.getNomLlarg());
			} else {  
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE, 
						"No s'ha pogut obtenir el llibre de l'organ gestor");
			}
			Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			
			List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
					arbreUnitats,
					organGestor.getCodi());
			if (oficinesSIR != null && !oficinesSIR.isEmpty()) {
				organGestor.updateOficina(oficinesSIR.get(0).getCodi(), oficinesSIR.get(0).getNom());
			} else { 
				logger.error(
						IntegracioHelper.INTCODI_REGISTRE, 
						"No s'ha pogut obtenir el llibre de l'organ gestor");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(timeout = 1200)
	@Override
	public void updateNoms(Long entitatId, String organActualCodiDir3) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ 
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId); 
			List<OrganGestorEntity> organsGestors;
			if (organActualCodiDir3 == null)
				organsGestors = organGestorRepository.findByEntitat(entitat);
			else {
				List<String> organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
				organsGestors = organGestorRepository.findByEntitatAndOrgansGestors(
						entitat,
						organGestorsListCodisDir3);
			}
			
			for(OrganGestorEntity organGestor: organsGestors) {
				String denominacio = findDenominacioOrganisme(organGestor.getCodi());
				if (denominacio != null && !denominacio.isEmpty())
					organGestor.update(denominacio);
				// Llibre òrgan gestor
				LlibreDto llibreOrgan = cacheHelper.getLlibreOrganGestor(
						entitat.getDir3Codi(),
						organGestor.getCodi());
				if (llibreOrgan != null)
					organGestor.updateLlibre(llibreOrgan.getCodi(), llibreOrgan.getNomLlarg());
				Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
				// Oficina SIR òrgan gestor
				List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
						arbreUnitats,
						organGestor.getCodi());
				if (oficinesSIR != null && !oficinesSIR.isEmpty())
					organGestor.updateOficina(oficinesSIR.get(0).getCodi(), oficinesSIR.get(0).getNom());
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findById(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "id=" + id + ")");
			EntitatEntity entitat = null;
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true, 
//						false, 
//						false);
	
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					id);
			OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findByCodi(
			Long entitatId,
			String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "codi=" + codi + ")");
//			EntitatEntity entitat = null;
				
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
//			if (entitatId != null)
//				entitat = entityComprovarHelper.comprovarEntitat(
//						entitatId); 
//						true, 
//						false, 
//						false);
	
//			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
//					entitat, 
//					codi);
			
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(codi);
			
			if (organGestor!=null) {
					OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
					return resposta;
			}else
				return null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAccessiblesByUsuariActual() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.findOrgansGestorsAccessiblesUsuari(auth);

//		List<OrganGestorEntity> organsGestors = organGestorRepository.findAll();
//		Permission[] permisos = new Permission[] {ExtendedPermission.ADMINISTRADOR};
//		
//		permisosHelper.filterGrantedAny(
//				organsGestors,
//				new ObjectIdentifierExtractor<OrganGestorEntity>() {
//					public Long getObjectIdentifier(OrganGestorEntity organGestor) {
//						return organGestor.getId();
//					}
//				},
//				OrganGestorEntity.class,
//				permisos,
//				auth);
//		
//		return conversioTipusHelper.convertirList(
//				organsGestors, 
//				OrganGestorDto.class);
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id +  ")"); 
			EntitatEntity entitat = null;
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			
			entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					id);
			
			return permisosHelper.findPermisos(
					id,
					OrganGestorEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void permisUpdate(
			Long entitatId,
			Long id,
			boolean isAdminOrgan,
			PermisDto permisDto) throws ValidationException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permisDto + ")");
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			
			PermisDto permis = permisosHelper.findPermis(
					id,
					OrganGestorEntity.class,
					permisDto.getId());	
			if (permis != null && isAdminOrgan && ((permis.getId() == null && permis.isAdministrador()) ||
					(permis.getId() != null && (permis.isAdministrador() != permisDto.isAdministrador())))) {
				throw new ValidationException("Un administrador d'òrgan no pot gestionar el permís d'admministrador d'òrgans gestors");
			}
			
			permisosHelper.updatePermis(
					id,
					OrganGestorEntity.class,
					permisDto);
			cacheHelper.evictFindOrgansGestorsAccessiblesUsuari();
			cacheHelper.evictFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentsWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			cacheHelper.evictAllPermisosEntitatsUsuariActual();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Eliminació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permisId=" + permisId + ")");
			EntitatEntity entitat = null;
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.deletePermis(
					id,
					OrganGestorEntity.class,
					permisId);
			cacheHelper.evictFindOrgansGestorsAccessiblesUsuari();
			cacheHelper.evictFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentsWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			cacheHelper.evictAllPermisosEntitatsUsuariActual();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
			try {
				organismes = cacheHelper.findOrganismesByEntitat(entitat.getDir3Codi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return organismes;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
			try {
				organismes = organigramaHelper.getOrganismesFillsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi() + " i òrgan gestor: " + organGestor.getCodi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return organismes;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public String findDenominacioOrganisme(String codiDir3) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String denominacio = null;
			try {
				denominacio = cacheHelper.findDenominacioOrganisme(codiDir3);
			} catch (Exception e) {
				String errorMessage = "No s'ha pogut recuperar la denominació de l'organismes: " + codiDir3;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return denominacio;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public LlibreDto getLlibreOrganisme(
			Long entitatId,
			String organGestorDir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			LlibreDto llibre = new LlibreDto();
			try {
				//Recupera el llibre de l'òrgan gestor especificat (organisme)
				llibre = cacheHelper.getLlibreOrganGestor(
						entitat.getDir3Codi(),
						organGestorDir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'ha pogut recuperar el llibre de l'òrgan gestor: " + organGestorDir3Codi;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return llibre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "organsPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
	public List<OrganGestorDto> findOrgansGestorsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true,
					false, 
					false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);

			// 1. Obtenim els òrgans gestors amb permisos
			List<OrganGestorEntity> organsDisponibles = organGestorRepository.findByEntitat(entitat);
			
			permisosHelper.filterGrantedAny(
					organsDisponibles,
					new ObjectIdentifierExtractor<OrganGestorEntity>() {
						public Long getObjectIdentifier(OrganGestorEntity organGestor) {
							return organGestor.getId();
						}
					},
					OrganGestorEntity.class,
					permisos,
					auth);
			
			List<OrganGestorDto> organsGestorsDto = conversioTipusHelper.convertirList(
					organsDisponibles, 
					OrganGestorDto.class); 

			if (organsGestorsDto != null && !organsGestorsDto.isEmpty()) {
				Set<OrganGestorDto> organsGestorsAmbPermis = new HashSet<OrganGestorDto>(organsGestorsDto);
				
				// 2. Obtenim els òrgans gestors fills dels organs gestors amb permisos
				if (!organsDisponibles.isEmpty()) {
					for (OrganGestorEntity organGestorEntity : organsDisponibles) {
						List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
										entitat.getDir3Codi(), 
										organGestorEntity.getCodi());
						if (organsFills != null)
							for(String organCodi: organsFills) {
								organsGestorsAmbPermis.add(findByCodi(entitatId, organCodi));
							}
										
					}
				}
				
				organsGestorsDto = new ArrayList<OrganGestorDto>(organsGestorsAmbPermis);
				Collections.sort(organsGestorsDto, new Comparator<OrganGestorDto>() {
					@Override
					public int compare(OrganGestorDto o1, OrganGestorDto o2) {
						return o1.getCodi().compareTo(o1.getCodi());
					}
				});
			}
			
			return organsGestorsDto;
	
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
	@Transactional(readOnly = true)
    public List<CodiValorDto> getOrgansGestorsDisponiblesConsulta(
    		Long entitatId,
			String usuari,
			RolEnumDto rol,
			String organ) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValorDto> organsGestors = new ArrayList<>();
			List<OrganGestorEntity> organsGestorsDisponibles = new ArrayList<>();

			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			if (RolEnumDto.NOT_SUPER.equals(rol)) {
				organsGestorsDisponibles = organGestorRepository.findAll();
			} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
				organsGestorsDisponibles = organGestorRepository.findByEntitat(entitat);
			} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
				List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organ);
				organsGestorsDisponibles = organGestorRepository.findByCodiIn(organs);
			} else if (RolEnumDto.tothom.equals(rol)) {
				organsGestorsDisponibles = recuperarOrgansPerProcedimentAmbPermis(
						usuari,
						entitat,
						PermisEnum.CONSULTA);
			}
			Collections.sort(organsGestorsDisponibles, new Comparator<OrganGestorEntity>() {
				@Override
				public int compare(OrganGestorEntity p1, OrganGestorEntity p2) {
					return p1.getNom().compareTo(p2.getNom());
				}
			});

			for (OrganGestorEntity organGestor : organsGestorsDisponibles) {
				String nom = organGestor.getCodi();
				if (organGestor.getNom() != null && !organGestor.getNom().isEmpty()) {
					nom += " - " + organGestor.getNom();
				}
				organsGestors.add(new CodiValorDto(organGestor.getId().toString(), nom));
			}
//		// Eliminam l'òrgan gestor entitat  --> Per ara el mantenim, ja que hi ha notificacions realitzades a l'entitat
//		OrganGestorDto organEntitat = organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi());
//		organsGestorsDisponibles.remove(organEntitat);
        	return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

    @Override
	@Transactional(readOnly = true)
	public List<OficinaDto> getOficinesOrganisme(
			Long entitatId,
			String organGestorDir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			List<OficinaDto> oficines = new ArrayList<OficinaDto>();
			try {
				Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
				oficines = cacheHelper.getOficinesSIRUnitat(
						arbreUnitats,
						organGestorDir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'han pogut recuperar les oficines SIR de l'òrgan gestor: " + organGestorDir3Codi;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return oficines;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
    
	private List<ProcedimentEntity> addProcedimentsOrgan(
			List<ProcedimentEntity> procedimentsDisponibles,
			List<ProcedimentOrganEntity> procedimentsOrgansDisponibles) {
		if (procedimentsOrgansDisponibles != null && !procedimentsOrgansDisponibles.isEmpty()) {
			Set<ProcedimentEntity> setProcediments = new HashSet<>(procedimentsDisponibles);
			for (ProcedimentOrganEntity procedimentOrgan : procedimentsOrgansDisponibles) {
				setProcediments.add(procedimentOrgan.getProcediment());
			}
			procedimentsDisponibles = new ArrayList<ProcedimentEntity>(setProcediments);
		}
		return procedimentsDisponibles;
	}

	private List<OrganGestorEntity> recuperarOrgansPerProcedimentAmbPermis(
			String usuari,
			EntitatEntity entitat,
			PermisEnum permis) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
		List<ProcedimentEntity> procedimentsDisponibles = procedimentHelper.getProcedimentsWithPermis(
				usuari,
				auth,
				entitat,
				permisos);
		List<ProcedimentOrganEntity> procedimentsOrgansDisponibles = procedimentHelper.getProcedimentOrganWithPermis(
				usuari,
				auth,
				entitat,
				permisos);

		procedimentsDisponibles = addProcedimentsOrgan(procedimentsDisponibles, procedimentsOrgansDisponibles);

		List<OrganGestorEntity> organsGestorsProcediments = new ArrayList<>();
		List<Long> procedimentsDisponiblesIds = new ArrayList<>();
		for (ProcedimentEntity pro : procedimentsDisponibles)
			procedimentsDisponiblesIds.add(pro.getId());

		// 1-recuperam els òrgans dels procediments disponibles (amb permís)
		if (!procedimentsDisponiblesIds.isEmpty())
			organsGestorsProcediments = organGestorRepository.findByProcedimentIds(procedimentsDisponiblesIds);
		// 2-recuperam els òrgans amb permís
		List<OrganGestorEntity> organsGestorsAmbPermis = organGestorHelper.getProcedimentsWithPermis(
				usuari,
				auth,
				entitat,
				permisos);
		// 3-juntam tots els òrgans i ordenam per nom
		List<OrganGestorEntity> organsGestors;
		Set<OrganGestorEntity> setOrgansGestors = new HashSet<>(organsGestorsProcediments);
		setOrgansGestors.addAll(organsGestorsAmbPermis);
		if (procedimentsOrgansDisponibles != null) {
			for (ProcedimentOrganEntity procedimentOrgan : procedimentsOrgansDisponibles) {
				setOrgansGestors.add(procedimentOrgan.getOrganGestor());
			}
		}
		organsGestors = new ArrayList<>(setOrgansGestors);
		if (!PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.notifica.dir3.entitat.permes", false)) {
			organsGestors.remove(organGestorRepository.findByCodi(entitat.getDir3Codi()));
		}
		if (procedimentsDisponibles.isEmpty() && organsGestors.isEmpty())
			throw new NoPermisosException("Usuari sense permios assignats");
		return organsGestors;
	}

    private static final Logger logger = LoggerFactory.getLogger(OrganGestorServiceImpl.class);

}
