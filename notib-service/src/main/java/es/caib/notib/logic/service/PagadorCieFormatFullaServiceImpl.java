package es.caib.notib.logic.service;

import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.PagadorCieFormatFullaService;
import es.caib.notib.persist.entity.cie.PagadorCieFormatFullaEntity;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.persist.repository.PagadorCieFormatFullaRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;

/**
 * Implementació del servei de gestió de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
	public CieFormatFullaDto create(Long pagadorCieId, CieFormatFullaDto formatFulla) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un format de fulla per el pagador cie (pagador=" + pagadorCieId + ")");
			var pagadorCieEntity = pagadorCieReposity.findById(pagadorCieId).orElseThrow();
			var p = PagadorCieFormatFullaEntity.getBuilder(formatFulla.getCodi(), pagadorCieEntity).build();
			var pagadorCieFormatFullaEntity = pagadorCieFormatFullaRepository.save(p);
			return conversioTipusHelper.convertir(pagadorCieFormatFullaEntity, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatFullaDto update(CieFormatFullaDto formatFulla) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieFormatFullaEntity = entityComprovarHelper.comprovarPagadorCieFormatFulla(formatFulla.getId());
			pagadorCieFormatFullaEntity.update(formatFulla.getCodi());
			pagadorCieFormatFullaRepository.save(pagadorCieFormatFullaEntity);
			return conversioTipusHelper.convertir(pagadorCieFormatFullaEntity, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieFormatFullaDto delete(Long id) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieFormatFullaEntity = entityComprovarHelper.comprovarPagadorCieFormatFulla(id);
			pagadorCieFormatFullaRepository.deleteById(pagadorCieFormatFullaEntity.getId());
			return conversioTipusHelper.convertir(pagadorCieFormatFullaEntity, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CieFormatFullaDto findById(Long id) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieFormatFullaEntity = entityComprovarHelper.comprovarPagadorCieFormatFulla(id);
			return conversioTipusHelper.convertir(pagadorCieFormatFullaEntity, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieFormatFullaDto> findAll() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els formats del pagador cie");
			var pagadorCieFormatFullaEntity = pagadorCieFormatFullaRepository.findAll();
			return conversioTipusHelper.convertirList(pagadorCieFormatFullaEntity, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CieFormatFullaDto> findFormatFullaByPagadorCie(Long pagadorCieId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els formats del pagador cie");
			var pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			var pagadorCieFormatFullaEntity = pagadorCieFormatFullaRepository.findByPagadorCie(pagadorCie);
			return conversioTipusHelper.convertirList(pagadorCieFormatFullaEntity, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieFormatFullaDto> findAllPaginat(Long pagadorCieId, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCie = entityComprovarHelper.comprovarPagadorCie(pagadorCieId);
			var p = paginacioHelper.toSpringDataPageable(paginacioParams);
			var pagadorCieFormatsFulla = pagadorCieFormatFullaRepository.findByPagadorCie(pagadorCie, p);
			return paginacioHelper.toPaginaDto(pagadorCieFormatsFulla, CieFormatFullaDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
}
