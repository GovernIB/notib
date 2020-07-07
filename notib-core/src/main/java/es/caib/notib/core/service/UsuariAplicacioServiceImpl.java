/**
 * 
 */
package es.caib.notib.core.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.AplicacioRepository;

/**
 * Implementació del servei de gestió d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	@Override
	@Transactional
	public AplicacioDto create(AplicacioDto aplicacio) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant una nova aplicació (aplicació=" + aplicacio.toString() + ")");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId());
			AplicacioEntity entity = AplicacioEntity.getBuilder(
					entitat,
					aplicacio.getUsuariCodi(),
					aplicacio.getCallbackUrl()).build();
			
			return conversioTipusHelper.convertir(
					aplicacioRepository.save(entity),
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public AplicacioDto update(AplicacioDto aplicacio) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant l'aplicació existent (aplicacio=" + aplicacio.toString() + ")");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			AplicacioEntity entity = aplicacioRepository.findOne(aplicacio.getId());
			
			entity.update(
					aplicacio.getUsuariCodi(),
					aplicacio.getCallbackUrl());
			
			return conversioTipusHelper.convertir(
					entity,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
		
	}
	
	@Override
	@Transactional
	public AplicacioDto delete(Long id, Long entitatId) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Esborrant aplicacio (id=" + id +  ")");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			AplicacioEntity entity = aplicacioRepository.findByEntitatIdAndId(entitatId, id );
			
			aplicacioRepository.delete(entity);
			
			return conversioTipusHelper.convertir(
					entity,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findById(Long aplicacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta una aplicació amb id = " + aplicacioId.toString());
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			AplicacioEntity entity = aplicacioRepository.findOne(aplicacioId);
			
			return conversioTipusHelper.convertir(
					entity,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndId(Long entitatId, Long aplicacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta una aplicació amb entitatId= " + entitatId + " i id = " + aplicacioId.toString());
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			AplicacioEntity entity = aplicacioRepository.findByEntitatIdAndId(entitatId, aplicacioId);
			
			return conversioTipusHelper.convertir(
					entity,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByUsuariCodi(String usuariCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta una aplicació amb codi = " + usuariCodi);
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			AplicacioEntity entity = aplicacioRepository.findByUsuariCodi(usuariCodi);
			
			return conversioTipusHelper.convertir(
					entity,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndUsuariCodi(Long entitatId, String usuariCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta una aplicació amb entitatId= " + entitatId + " i codi = " + usuariCodi);
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			AplicacioEntity entity = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitatId, usuariCodi);
			
			return conversioTipusHelper.convertir(
					entity,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AplicacioDto> findPaginat(PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de un llistat paginat de totes les aplicacions");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			Page<AplicacioEntity> aplicacions = aplicacioRepository.findAllFiltrat(
					paginacioParams.getFiltre(),
					paginacioHelper.toSpringDataPageable(paginacioParams)
					);
			
			return paginacioHelper.toPaginaDto(
					aplicacions,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AplicacioDto> findPaginatByEntitat(Long entitatId, PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de un llistat paginat de totes les aplicacions de l'entitat amb Id: " + entitatId);
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					false,
					false);
			
			Page<AplicacioEntity> aplicacions = aplicacioRepository.findByEntitatIdFiltrat(
					entitatId,
					paginacioParams.getFiltre(),
					paginacioHelper.toSpringDataPageable(paginacioParams)
					);
			
			return paginacioHelper.toPaginaDto(
					aplicacions,
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(UsuariAplicacioServiceImpl.class);

	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndText(Long entitatId, String text) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant usuaris aplicació amb text (text=" + text + ")");
			return conversioTipusHelper.convertir(
					aplicacioRepository.findByText(text),
					AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	
}
