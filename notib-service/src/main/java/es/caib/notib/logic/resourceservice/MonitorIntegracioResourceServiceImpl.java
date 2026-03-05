package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.model.MonitorIntegracioResource;
import es.caib.notib.logic.intf.resourceservice.MonitorIntegracioResourceService;
import es.caib.notib.persist.resourceentity.MonitorIntegracioResourceEntity;
import es.caib.notib.persist.resourcerepository.MonitorIntegracioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de monitor d'integracions
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorIntegracioResourceServiceImpl extends BaseMutableResourceService<MonitorIntegracioResource, Long, MonitorIntegracioResourceEntity> implements MonitorIntegracioResourceService {

	private final MonitorIntegracioResourceRepository monitorIntegracioResourceRepository;

	@PostConstruct
	public void init() {
		register(MonitorIntegracioResource.REPORT_AGRUPACIONS, new MonitorIntegracioResourceServiceImpl.AgrupacioReportGenerator());
	}

	private class AgrupacioReportGenerator implements ReportGenerator<MonitorIntegracioResourceEntity, Serializable, MonitorIntegracioResource.MonitorIntegracioAgrupacioItem> {
		@Override
		public List<MonitorIntegracioResource.MonitorIntegracioAgrupacioItem> generateData(
			String code,
			MonitorIntegracioResourceEntity entity,
			Serializable params) throws ReportGenerationException {
			List<Object[]> agrupacions = monitorIntegracioResourceRepository.countByCodi();
			return agrupacions.stream().map(a -> new MonitorIntegracioResource.MonitorIntegracioAgrupacioItem(
				(IntegracioCodi)a[0],
				(Long)a[1]
			)).collect(Collectors.toList());
		}
		@Override
		public void onChange(
			Serializable id,
			Serializable previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			Serializable target) {
		}
	}

}
