/**
 * 
 */
package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioDetall;
import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.service.MonitorIntegracioService;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.persist.filtres.FiltreMonitorIntegracio;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioParamRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Map;


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
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private MonitorIntegracioRepository monitorRepository;
	@Resource
	private MonitorIntegracioParamRepository paramRepository;

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(IntegracioCodiEnum codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant les darreres accions per a la integració ( codi=" + codi + ")");
			var pageable = paginacioHelper.toSpringDataPageable(paginacio);
			var entitatCodiNull = Strings.isNullOrEmpty(filtre.getEntitatCodi());
			var appNull = Strings.isNullOrEmpty(filtre.getAplicacio());
			filtre.setDataFi(DatesUtils.incrementarDataFiSiMateixDia(filtre.getDataInici(), filtre.getDataFi()));

			var f = FiltreMonitorIntegracio.builder()
					.codi(codi)
					.codiEntitatNull(Strings.isNullOrEmpty(filtre.getEntitatCodi()))
					.codiEntitat(!entitatCodiNull ? filtre.getEntitatCodi() : "")
					.aplicacioNull(Strings.isNullOrEmpty(filtre.getAplicacio()))
					.aplicacio(!appNull ? filtre.getAplicacio() : "")
					.descripcioNull(Strings.isNullOrEmpty(filtre.getDescripcio()))
					.descripcio(filtre.getDescripcio())
					.dataIniciNull(filtre.getDataInici() == null)
					.dataInici(filtre.getDataInici())
					.dataFiNull(filtre.getDataFi() == null)
					.dataFi(filtre.getDataFi())
					.tipusNull(filtre.getTipus() == null)
					.tipus(filtre.getTipus())
					.estatNull(filtre.getEstat() == null)
					.estat(filtre.getEstat())
					.build();
			var accions = monitorRepository.getByFiltre(f, pageable);
			return paginacioHelper.toPaginaDto(accions, IntegracioAccioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Map<IntegracioCodiEnum, Integer> countErrors() {

		var timer = metricsHelper.iniciMetrica();
		try {
			return integracioHelper.countErrorsGroupByCodi();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void netejarMonitor() {

		var timer = metricsHelper.iniciMetrica();
		try {
			monitorRepository.deleteAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public IntegracioDetall detallIntegracio(Long id) {

		try {
			var i = monitorRepository.findById(id).orElseThrow();
			var a = paramRepository.findByMonitorIntegracioOrderByIdAsc(i);
			if (a == null) {
				return IntegracioDetall.builder().descripcio("Error obtinguent el detall de la integració " + id).build();
			}
			var accions = conversioTipusHelper.convertirList(a, AccioParam.class);
			return IntegracioDetall.builder().data(i.getData()).descripcio(i.getDescripcio()).tipus(i.getTipus()).estat(i.getEstat())
					.errorDescripcio(i.getErrorDescripcio()).excepcioMessage(i.getExcepcioMessage())
					.excepcioStacktrace(i.getExcepcioStacktrace()).parametres(accions).build();
		} catch (Exception ex) {
			return IntegracioDetall.builder().descripcio("Error obtinguent el detall de la integració " + id).build();
		}
	}

}
