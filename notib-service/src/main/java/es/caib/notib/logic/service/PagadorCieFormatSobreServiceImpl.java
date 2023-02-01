package es.caib.notib.logic.service;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.PagadorCieFormatSobreService;
import es.caib.notib.persist.entity.cie.PagadorCieFormatSobreEntity;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.persist.repository.PagadorCieFormatSobreRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;

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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un format de sobre per el pagador cie (pagador=" + pagadorCieId + ")");
			var pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			var p = PagadorCieFormatSobreEntity.getBuilder(formatSobre.getCodi(), pagadorCieEntity).build();
			var pagadorCieFormatSobreEntity = pagadorCieFormatSobreRepository.save(p);
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatSobreDto update(CieFormatSobreDto formatSobre) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieFormatSobreEntity = entityComprovarHelper.comprovarPagadorCieFormatSobre(formatSobre.getId());
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

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieFormatSobreEntity = entityComprovarHelper.comprovarPagadorCieFormatSobre(id);
			pagadorCieFormatSobreRepository.deleteById(pagadorCieFormatSobreEntity.getId());
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CieFormatSobreDto findById(Long id) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieFormatSobreEntity = entityComprovarHelper.comprovarPagadorCieFormatSobre(id);
			return conversioTipusHelper.convertir(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieFormatSobreDto> findAll() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els formats del pagador cie");
			var pagadorCieFormatSobreEntity = pagadorCieFormatSobreRepository.findAll();
			return conversioTipusHelper.convertirList(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieFormatSobreDto> findFormatSobreByPagadorCie(Long pagadorCieId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els formats del pagador cie");
			var pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			var pagadorCieFormatSobreEntity = pagadorCieFormatSobreRepository.findByPagadorCie(pagadorCie);
			return conversioTipusHelper.convertirList(pagadorCieFormatSobreEntity, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieFormatSobreDto> findAllPaginat(Long pagadorCieId, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			var p = paginacioHelper.toSpringDataPageable(paginacioParams);
			var pagadorCieFormatsSobre = pagadorCieFormatSobreRepository.findByPagadorCie(pagadorCie, p);
			return paginacioHelper.toPaginaDto(pagadorCieFormatsSobre, CieFormatSobreDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
}
