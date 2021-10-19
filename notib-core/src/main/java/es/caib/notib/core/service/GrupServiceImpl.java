package es.caib.notib.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.GrupFiltreDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.procediment.ProcedimentGrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcSerEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.GrupHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.GrupProcSerRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;

/**
 * Implementació del servei de gestió de grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class GrupServiceImpl implements GrupService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private GrupHelper conversioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private GrupRepository grupReposity;
	@Resource
	private GrupProcSerRepository grupProcedimentRepositoy;
	@Resource
	private ProcedimentRepository procedimentRepositroy;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	@Audita(entityType = TipusEntitat.GRUP, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public GrupDto create(
			Long entitatId,
			GrupDto grup) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou gurp ("
					+ "grup=" + grup + ")");
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al grup i que es administrador de l'organ indicat 
			
			OrganGestorEntity organGestor = null;
			if (grup.getOrganGestorId() != null) {
				organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						grup.getOrganGestorId());
			}
			
			GrupEntity grupEntity = grupReposity.save(
					GrupEntity.getBuilder(
							grup.getCodi(),
							grup.getNom(),
							entitat,
							organGestor).build());
			
			return conversioTipusHelper.convertir(
					grupEntity, 
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.GRUP, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public GrupDto update(GrupDto grup) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant grup ("
					+ "grup=" + grup + ")");
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al grup i que es administrador de l'organ indicat
			
			GrupEntity grupEntity = entityComprovarHelper.comprovarGrup(grup.getId());
			grupEntity.update(
							grup.getCodi(),
							grup.getNom());
			
			grupReposity.save(grupEntity);
			return conversioTipusHelper.convertir(
					grupEntity, 
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.GRUP, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public GrupDto delete(Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que es administrador de l'organ del grup que vol eliminar 
			GrupEntity grupEntity = entityComprovarHelper.comprovarGrup(id);
			grupReposity.delete(grupEntity);
			return conversioTipusHelper.convertir(
					grupEntity, 
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public GrupDto findById(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId);
			
			GrupEntity grupEntity = entityComprovarHelper.comprovarGrup(id);
			return conversioTipusHelper.convertir(
					grupEntity, 
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public GrupDto findByCodi(
			String grupCodi,
			Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = null;
			
			if(entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			
			GrupEntity grupEntity = grupReposity.findByCodiAndEntitat(grupCodi, entitat);
			return conversioTipusHelper.convertir(
					grupEntity, 
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<GrupDto> findByProcedimentAndUsuariGrups(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<GrupDto> grups = new ArrayList<GrupDto>();
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProcedimentEntity procediment = procedimentRepositroy.findOne(procedimentId);
			List<GrupProcSerEntity> grupsProcediment = grupProcedimentRepositoy.findByProcSer(procediment);
			
			for (GrupProcSerEntity grupProcediment : grupsProcediment) {
				DadesUsuari usuariGrup = cacheHelper.findUsuariAmbCodi(auth.getName());
				if (usuariGrup != null) {
					List<String> rols = cacheHelper.findRolsUsuariAmbCodi(usuariGrup.getCodi());
					if (rols.contains(grupProcediment.getGrup().getCodi())) {
						grups.add(conversioTipusHelper.convertir(
								grupReposity.findOne(grupProcediment.getGrup().getId()), 
								GrupDto.class));
					}
				}			
			}
			
			return grups;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<GrupDto> findGrupsByProcediment(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<GrupDto> grups = new ArrayList<GrupDto>();
			ProcedimentEntity procediment = procedimentRepositroy.findOne(procedimentId);
			List<GrupProcSerEntity> grupsProcediment = grupProcedimentRepositoy.findByProcSer(procediment);
			
			for (GrupProcSerEntity grupProcediment : grupsProcediment) {
				grups.add(conversioTipusHelper.convertir(
					grupReposity.findOne(grupProcediment.getGrup().getId()), 
					GrupDto.class));	
			}
			
			return grups;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ProcedimentGrupDto> findByProcediment(
			Long entitatId, 
			Long procedimentId,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (entitatId != null)
				entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false,
						false, 
						false);
			
			ProcedimentEntity procediment = procedimentRepositroy.findOne(procedimentId);
			List<GrupProcSerEntity> grupsProcediment = grupProcedimentRepositoy.findByProcSer(
					procediment,
					paginacioHelper.toSpringDataPageable(paginacioParams)); 
			
			return paginacioHelper.toPaginaDto(
					grupsProcediment, 
					ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public ProcedimentGrupDto findProcedimentGrupById(
			Long entitatId, 
			Long procedimentGrupId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId);
			GrupProcSerEntity procedimentGrup = grupProcedimentRepositoy.findOne(procedimentGrupId);
			
			return conversioTipusHelper.convertir(
					procedimentGrup, 
					ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Boolean existProcedimentGrupByGrupId(
			Long entitatId, 
			Long grupId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			GrupEntity grup = entityComprovarHelper.comprovarGrup(grupId);
			if (!grup.getEntitat().getId().equals(entitatId)) {
				throw new ValidationException("El grup que s'intenta eliminar no pertany a la entitat actual");
			}
			List<GrupProcSerEntity> procedimentGrups = grupProcedimentRepositoy.findByGrup(grup);
			return (procedimentGrups != null && !procedimentGrups.isEmpty());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	
	@Override
	@Transactional
	public List<GrupDto> deleteGrupsProcediment(
			List<GrupDto> grups) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//TODO: Si es tothom comprovar que és administrador d'Organ i que es administrador de l'organ del grup que vol eliminar
			List<GrupEntity> grupsEntity = entityComprovarHelper.comprovarGrups(grups);
			
			grupReposity.delete(grupsEntity);
			
			return grups;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<GrupDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			
			return conversioTipusHelper.convertirList(
					grupReposity.findByEntitat(entitat),
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<GrupDto> findByEntitatAndOrganGestor(
			EntitatDto entitat, 
			OrganGestorDto organGestor) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
					entitat.getDir3Codi(), 
					organGestor.getCodi());
			
			return conversioTipusHelper.convertirList(
					grupReposity.findByEntitatIdAndOrganGestorCodiIn(entitat.getId(), organsFills),
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<GrupDto> findAmbFiltrePaginat(
			Long entitatId, 
			GrupFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true );
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Page<GrupEntity> grup = null;
	
			List<String> organsFills = null;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitat.getDir3Codi(), 
						organGestor.getCodi());	
				
				grup = grupReposity.findByCodiNotNullFiltrePaginatWithOrgan(
						filtre.getCodi() == null || filtre.getCodi().isEmpty(),
						filtre.getCodi(),
						organsFills,
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams));
				
			}else{
				grup = grupReposity.findByCodiNotNullFiltrePaginat(
						filtre.getCodi() == null || filtre.getCodi().isEmpty(),
						filtre.getCodi(),
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			}
			
			
			
			
			
			return paginacioHelper.toPaginaDto(
					grup,
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<GrupDto> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginaDto<GrupDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
