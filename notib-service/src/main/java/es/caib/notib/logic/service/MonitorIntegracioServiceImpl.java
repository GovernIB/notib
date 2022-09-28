/**
 * 
 */
package es.caib.notib.logic.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.service.MonitorIntegracioService;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MetricsHelper;

/**
 * Implementació del servei de gestió d'items monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class MonitorIntegracioServiceImpl implements MonitorIntegracioService {

	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	@Override
	public List<IntegracioDto> integracioFindAll() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant les integracions");
			return integracioHelper.findAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant les darreres accions per a la integració ( codi=" + codi + ")");
			List<IntegracioAccioDto> accions = integracioHelper.findAccions(codi, filtre);
			int index = 0;
			for (IntegracioAccioDto accio : accions) {
				accio.setIndex(Long.valueOf(index));
				index++;
			}
			return accions;
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
}
