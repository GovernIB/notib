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

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.GrupFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.GrupHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.GrupProcedimentRepository;
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
	private GrupRepository grupReposity;
	@Resource
	private GrupProcedimentRepository grupProcedimentRepositoy;
	@Resource
	private ProcedimentRepository procedimentRepositroy;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
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
			
			GrupEntity grupEntity = null;
			
			grupEntity = grupReposity.save(
					GrupEntity.getBuilder(
							grup.getCodi(),
							grup.getNom(),
							entitat).build());
			
			
			return conversioTipusHelper.convertir(
					grupEntity, 
					GrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public GrupDto update(GrupDto grup) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant grup ("
					+ "grup=" + grup + ")");
				
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

	@Override
	@Transactional
	public GrupDto delete(Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
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
			
			GrupEntity grupEntity = grupReposity.findOne(id);
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
	public List<GrupDto> findByProcedimentGrups(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<GrupDto> grups = new ArrayList<GrupDto>();
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProcedimentEntity procediment = procedimentRepositroy.findOne(procedimentId);
			List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepositoy.findByProcediment(procediment); 
			
			for (GrupProcedimentEntity grupProcediment : grupsProcediment) {
				DadesUsuari usuariGrup = pluginHelper.dadesUsuariConsultarAmbCodi(auth.getName());
				if (usuariGrup != null) {
					List<String> rols = pluginHelper.consultarRolsAmbCodi(usuariGrup.getCodi());
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
			List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepositoy.findByProcediment(procediment); 
			
			for (GrupProcedimentEntity grupProcediment : grupsProcediment) {
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
			List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepositoy.findByProcediment(
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
			GrupProcedimentEntity procedimentGrup = grupProcedimentRepositoy.findOne(procedimentGrupId);
			
			return conversioTipusHelper.convertir(
					procedimentGrup, 
					ProcedimentGrupDto.class);
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
	
			List<GrupEntity> grups = grupReposity.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					grups,
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
					false );
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Page<GrupEntity> grup = null;
	
			grup = grupReposity.findByCodiNotNullFiltrePaginat(
					filtre.getCodi() == null || filtre.getCodi().isEmpty(),
					filtre.getCodi(),
					entitat,
					paginacioHelper.toSpringDataPageable(paginacioParams));
			
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
