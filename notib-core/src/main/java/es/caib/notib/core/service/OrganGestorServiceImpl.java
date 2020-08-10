package es.caib.notib.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.OrganGestorRepository;
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
	private MetricsHelper metricsHelper;
	

	@Override
	@Transactional
	public OrganGestorDto create(OrganGestorDto dto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					Long.parseLong(dto.getEntitatId()), 
					false, 
					true, 
					false);
			
			OrganGestorEntity organGestor = OrganGestorEntity.getBuilder(
					dto.getCodi(),
					dto.getNom(),
					entitat).build();
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
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
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
			List<ProcedimentEntity> procedimentsOrganGestor = procedimentRepository.findByOrganGestorId(organId);
			return (procedimentsOrganGestor != null && !procedimentsOrganGestor.isEmpty());
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
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					true, 
					false);
			
			Page<OrganGestorEntity> organs = null;
			if (filtre == null) {
				organs = organGestorRepository.findByEntitat(
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			} else {
				organs = organGestorRepository.findByEntitatAndFiltre(
						entitat,
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() == null ? "" : filtre.getCodi(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						paginacioHelper.toSpringDataPageable(paginacioParams));
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
	
	@Transactional
	@Override
	public void updateNom(Long entitatId, String organGestorCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			String denominacio = findDenominacioOrganisme(organGestorCodi);
			if (denominacio != null && !denominacio.isEmpty())
				organGestor.update(denominacio);
			else
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS, 
						"No s'ha pogut obtenir la denominació de l'organ gestor");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void updateNoms(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					true, 
					false);
			List<OrganGestorEntity> organsGestors = organGestorRepository.findByEntitat(entitat);
			for(OrganGestorEntity organGestor: organsGestors) {
				String denominacio = findDenominacioOrganisme(organGestor.getCodi());
				if (denominacio != null && !denominacio.isEmpty())
					organGestor.update(denominacio);
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
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						true, 
						false);
	
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
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						true, 
						false);
	
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					codi);
			OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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
			
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			
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
			
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.updatePermis(
					id,
					OrganGestorEntity.class,
					permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
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
			
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.deletePermis(
					id,
					OrganGestorEntity.class,
					permisId);
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
	
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorServiceImpl.class);

}
