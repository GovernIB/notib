package es.caib.notib.core.service;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.cie.CieFormatSobreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorCieFormatSobreEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.PagadorCieFormatSobreRepository;
import es.caib.notib.core.repository.PagadorCieRepository;

/**
 * Implementació del servei de gestió de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class PagadorCieFormatSobreServiceImpl implements PagadorCieFormatSobreService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private PagadorCieFormatSobreRepository pagadorCieFormatSobreRepository;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	@Override
	@Transactional
	public CieFormatSobreDto create(Long pagadorCieId, CieFormatSobreDto formatSobre) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un format de sobre per el pagador cie (pagador=" + pagadorCieId + ")");
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			PagadorCieFormatSobreEntity p = PagadorCieFormatSobreEntity.getBuilder(formatSobre.getCodi(), pagadorCieEntity).build();
			PagadorCieFormatSobreEntity pagadorCieFormatSobreEntity = pagadorCieFormatSobreRepository.save(p);
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatSobreDto update(CieFormatSobreDto formatSobre) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieFormatSobreEntity pagadorCieFormatSobreEntity = entityComprovarHelper.comprovarPagadorCieFormatSobre(formatSobre.getId());
			pagadorCieFormatSobreEntity.update(formatSobre.getCodi());
			pagadorCieFormatSobreRepository.save(pagadorCieFormatSobreEntity);
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatSobreDto delete(Long id) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieFormatSobreEntity pagadorCieFormatSobreEntity = entityComprovarHelper.comprovarPagadorCieFormatSobre(id);
			pagadorCieFormatSobreRepository.delete(pagadorCieFormatSobreEntity.getId());
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CieFormatSobreDto findById(Long id) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieFormatSobreEntity pagadorCieFormatSobreEntity = entityComprovarHelper.comprovarPagadorCieFormatSobre(id);
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieFormatSobreDto> findAll() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els formats del pagador cie");
			List<PagadorCieFormatSobreEntity> pagadorCieFormatSobreEntity = pagadorCieFormatSobreRepository.findAll();
			return conversioTipusHelper.convertirList(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieFormatSobreDto> findFormatSobreByPagadorCie(Long pagadorCieId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els formats del pagador cie");
			PagadorCieEntity pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			List<PagadorCieFormatSobreEntity> pagadorCieFormatSobreEntity = pagadorCieFormatSobreRepository.findByPagadorCie(pagadorCie);
			return conversioTipusHelper.convertirList(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieFormatSobreDto> findAllPaginat(Long pagadorCieId, PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieEntity pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			Pageable p = paginacioHelper.toSpringDataPageable(paginacioParams);
			Page<PagadorCieFormatSobreEntity> pagadorCieFormatsSobre = pagadorCieFormatSobreRepository.findByPagadorCie(pagadorCie, p);
			return paginacioHelper.toPaginaDto(pagadorCieFormatsSobre, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
}
