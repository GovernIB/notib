/**
 * 
 */
package es.caib.notib.core.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import es.caib.notib.core.api.dto.IntegracioFiltreDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant les darreres accions per a la integració ( codi=" + codi + ")");
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
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorIntegracioServiceImpl.class);

}
