/**
 * 
 */
package es.caib.notib.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioDetall;
import es.caib.notib.core.api.dto.IntegracioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.cie.CieTableItemDto;
import es.caib.notib.core.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.core.entity.monitor.MonitorIntegracioParamEntity;
import es.caib.notib.core.repository.monitor.MonitorIntegracioParamRepository;
import es.caib.notib.core.repository.monitor.MonitorIntegracioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;

import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.MonitorIntegracioService;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;

/**
 * Implementació del servei de gestió d'items monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MonitorIntegracioServiceImpl implements MonitorIntegracioService {

	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private MonitorIntegracioRepository monitorRepository;
	@Resource
	private MonitorIntegracioParamRepository paramRepository;

	@Override
	public List<IntegracioDto> integracioFindAll() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant les integracions");
			return integracioHelper.findAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant les darreres accions per a la integració ( codi=" + codi + ")");
//			List<IntegracioAccioDto> accions = integracioHelper.findAccions(codi, filtre);

			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacio);
			Page<MonitorIntegracioEntity> accions = monitorRepository.getByFiltre(
					codi,
					Strings.isNullOrEmpty(filtre.getEntitatCodi()),
					filtre.getEntitatCodi(),
					Strings.isNullOrEmpty(filtre.getAplicacio()),
					filtre.getAplicacio(),
					pageable);
			return paginacioHelper.toPaginaDto(accions, IntegracioAccioDto.class);

//			int index = 0;
//			for (IntegracioAccioDto accio : accions) {
//				accio.setIndex(Long.valueOf(index));
//				index++;
//			}
//			return accions;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Map<String, Integer> countErrors() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return integracioHelper.countErrorsGroupByCodi();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void netejarMonitor() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			monitorRepository.deleteAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public IntegracioDetall detallIntegracio(Long id) {

		try {
			MonitorIntegracioEntity i = monitorRepository.findOne(id);
			List<MonitorIntegracioParamEntity> a = paramRepository.findByMonitorIntegracio(i);
			if (a == null) {
				return IntegracioDetall.builder().descripcio("Error obtinguent el detall de la integració " + id).build();
			}
			List<AccioParam> accions = conversioTipusHelper.convertirList(a, AccioParam.class);
			return IntegracioDetall.builder().data(i.getData()).descripcio(i.getDescripcio()).tipus(i.getTipus()).estat(i.getEstat())
					.errorDescripcio(i.getErrorDescripcio()).excepcioMessage(i.getExcepcioMessage())
					.excepcioStacktrace(i.getExcepcioStacktrace()).parametres(accions).build();
		} catch (Exception ex) {
			return IntegracioDetall.builder().descripcio("Error obtinguent el detall de la integració " + id).build();
		}
	}
	private static final Logger logger = LoggerFactory.getLogger(MonitorIntegracioServiceImpl.class);

}
