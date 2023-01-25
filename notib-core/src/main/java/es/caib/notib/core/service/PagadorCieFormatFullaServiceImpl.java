package es.caib.notib.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.cie.CieFormatFullaDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorCieFormatFullaEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.PagadorCieFormatFullaRepository;
import es.caib.notib.core.repository.PagadorCieRepository;

/**
 * Implementació del servei de gestió de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PagadorCieFormatFullaServiceImpl implements PagadorCieFormatFullaService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private PagadorCieFormatFullaRepository pagadorCieFormatFullaRepository;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	@Override
	@Transactional
	public CieFormatFullaDto create(
			Long pagadorCieId, 
			CieFormatFullaDto formatFulla) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un format de fulla per el pagador cie ("
					+ "pagador=" + pagadorCieId + ")");
			PagadorCieEntity pagadorCieEntity = pagadorCieReposity.findOne(pagadorCieId);
			
			PagadorCieFormatFullaEntity pagadorCieFormatFullaEntity = pagadorCieFormatFullaRepository.save(
					PagadorCieFormatFullaEntity.getBuilder(
							formatFulla.getCodi(),
							pagadorCieEntity).build());
			
			return conversioTipusHelper.convertir(
					pagadorCieFormatFullaEntity, 
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatFullaDto update(CieFormatFullaDto formatFulla) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieFormatFullaEntity pagadorCieFormatFullaEntity = entityComprovarHelper.comprovarPagadorCieFormatFulla(formatFulla.getId());
			pagadorCieFormatFullaEntity.update(formatFulla.getCodi());
			
			pagadorCieFormatFullaRepository.save(pagadorCieFormatFullaEntity);
			
			return conversioTipusHelper.convertir(
					pagadorCieFormatFullaEntity, 
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatFullaDto delete(Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieFormatFullaEntity pagadorCieFormatFullaEntity = entityComprovarHelper.comprovarPagadorCieFormatFulla(id);
			pagadorCieFormatFullaRepository.delete(pagadorCieFormatFullaEntity.getId());
			
			return conversioTipusHelper.convertir(
					pagadorCieFormatFullaEntity, 
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CieFormatFullaDto findById(Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieFormatFullaEntity pagadorCieFormatFullaEntity = entityComprovarHelper.comprovarPagadorCieFormatFulla(id);
			
			return conversioTipusHelper.convertir(
					pagadorCieFormatFullaEntity, 
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieFormatFullaDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els formats del pagador cie");
			List<PagadorCieFormatFullaEntity> pagadorCieFormatFullaEntity = pagadorCieFormatFullaRepository.findAll();
			
			return conversioTipusHelper.convertirList(
					pagadorCieFormatFullaEntity,
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CieFormatFullaDto> findFormatFullaByPagadorCie(Long pagadorCieId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els formats del pagador cie");
			PagadorCieEntity pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			List<PagadorCieFormatFullaEntity> pagadorCieFormatFullaEntity = pagadorCieFormatFullaRepository.findByPagadorCie(pagadorCie);
			
			return conversioTipusHelper.convertirList(
					pagadorCieFormatFullaEntity,
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieFormatFullaDto> findAllPaginat(
			Long pagadorCieId,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieEntity pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			Page<PagadorCieFormatFullaEntity> pagadorCieFormatsFulla = pagadorCieFormatFullaRepository.findByPagadorCie(
					pagadorCie,
					paginacioHelper.toSpringDataPageable(paginacioParams));
		
			return paginacioHelper.toPaginaDto(
					pagadorCieFormatsFulla,
					CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PagadorCieFormatFullaServiceImpl.class);
}