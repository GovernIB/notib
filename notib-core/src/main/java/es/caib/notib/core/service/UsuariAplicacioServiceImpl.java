/**
 * 
 */
package es.caib.notib.core.service;

import javax.annotation.Resource;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.AplicacioRepository;

import java.util.List;

/**
 * Implementació del servei de gestió d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class UsuariAplicacioServiceImpl implements UsuariAplicacioService {
	
	@Resource
	private AplicacioRepository aplicacioRepository;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto create(AplicacioDto aplicacio) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant una nova aplicació (aplicació=" + aplicacio.toString() + ")");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId(), true, true, false, false);
			AplicacioEntity entity = AplicacioEntity.getBuilder(entitat, aplicacio.getUsuariCodi(), aplicacio.getCallbackUrl()).build();
			return conversioTipusHelper.convertir(aplicacioRepository.save(entity), AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto update(AplicacioDto aplicacio) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant l'aplicació existent (aplicacio=" + aplicacio.toString() + ")");
			entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId(), true, true, false, false);
			AplicacioEntity entity = aplicacioRepository.findOne(aplicacio.getId());
			entity.update(aplicacio.getUsuariCodi(), aplicacio.getCallbackUrl());
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto delete(Long id, Long entitatId) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Esborrant aplicacio (id=" + id +  ")");
			entityComprovarHelper.comprovarEntitat(entitatId, true, true, false, false);
			AplicacioEntity entity = aplicacioRepository.findByEntitatIdAndId(entitatId, id );
			aplicacioRepository.delete(entity);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findById(Long aplicacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb id = " + aplicacioId.toString());
			AplicacioEntity entity = aplicacioRepository.findOne(aplicacioId);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndId(Long entitatId, Long aplicacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb entitatId= " + entitatId + " i id = " + aplicacioId.toString());
			AplicacioEntity entity = aplicacioRepository.findByEntitatIdAndId(entitatId, aplicacioId);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByUsuariCodi(String usuariCodi) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb codi = " + usuariCodi);
			AplicacioEntity entity = aplicacioRepository.findByUsuariCodi(usuariCodi);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndUsuariCodi(Long entitatId, String usuariCodi) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb entitatId= " + entitatId + " i codi = " + usuariCodi);
			AplicacioEntity entity = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitatId, usuariCodi);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AplicacioDto> findPaginat(PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de un llistat paginat de totes les aplicacions");
			entityComprovarHelper.comprovarPermisos(null, true, false, false);
			Page<AplicacioEntity> aplicacions = aplicacioRepository.findAllFiltrat(paginacioParams.getFiltre(), paginacioHelper.toSpringDataPageable(paginacioParams));
			return paginacioHelper.toPaginaDto(aplicacions, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AplicacioDto> findPaginatByEntitat(Long entitatId, PaginacioParamsDto paginacioParams) {
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de un llistat paginat de totes les aplicacions de l'entitat amb Id: " + entitatId);
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			List<PaginacioParamsDto.FiltreDto> filtres = paginacioParams.getFiltres();
			String codi = filtres.get(0).getValor();
			String url = filtres.get(1).getValor();
			Pageable params = paginacioHelper.toSpringDataPageable(paginacioParams);
			Boolean activa = !Strings.isNullOrEmpty(filtres.get(2).getValor()) ? Integer.parseInt(filtres.get(2).getValor()) == 1 ? true : false : null;
			Page<AplicacioEntity> aplicacions = activa != null ? aplicacioRepository.findByEntitatIdFiltrat(entitatId, codi, url, activa, params)
												: aplicacioRepository.findByEntitatIdFiltrat(entitatId, codi, url, params);
			return paginacioHelper.toPaginaDto(aplicacions, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndText(Long entitatId, String text) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant usuaris aplicació amb text (text=" + text + ")");
			return conversioTipusHelper.convertir(aplicacioRepository.findByText(entitatId, text), AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	public AplicacioDto updateActiva(Long id, boolean activa) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant propietat activa d'una aplicació existent (id=" + id + ", activa=" + activa + ")");
			AplicacioEntity aplicacio = aplicacioRepository.findOne(id);
			entityComprovarHelper.comprovarEntitat(aplicacio.getEntitat().getId(), true, true, false, false);
			aplicacio.updateActiva(activa);
			return conversioTipusHelper.convertir(aplicacio, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
}
