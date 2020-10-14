package es.caib.notib.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcedimentRepository;

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
					dto.getLlibreNom()).build();
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
			if (llibreOrgan != null)
				organGestor.update(llibreOrgan.getCodi(), llibreOrgan.getNomLlarg());
			else 
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE, 
						"No s'ha pogut obtenir el llibre de l'organ gestor");
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
//					true, 
//					false, 
//					false);
			List<OrganGestorEntity> organsGestors;
			if (organActualCodiDir3==null)
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
				LlibreDto llibreOrgan = cacheHelper.getLlibreOrganGestor(
						entitat.getDir3Codi(),
						organGestor.getCodi());
				if (llibreOrgan != null)
					organGestor.update(llibreOrgan.getCodi(), llibreOrgan.getNomLlarg());
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
			PermisDto permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permis + ")");
			EntitatEntity entitat = null;
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.updatePermis(
					id,
					OrganGestorEntity.class,
					permis);
			cacheHelper.evictFindOrgansGestorsAccessiblesUsuari();
			cacheHelper.evictFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentsWithPermis();
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
	public List<OrganGestorDto> findOrgansGestorsWithPermis(Long entitatId, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true,
					false, 
					false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
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
			return organsGestorsDto;
	
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorServiceImpl.class);

}
