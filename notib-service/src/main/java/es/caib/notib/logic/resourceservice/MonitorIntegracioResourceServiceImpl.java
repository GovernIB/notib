package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
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
import java.util.Arrays;
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
			List<Object[]> agrupacioCounts = monitorIntegracioResourceRepository.countByCodiAndEstat();
			return Arrays.stream(IntegracioCodi.values()).map(c -> {
				List<Object[]> agrupacionsAmbCodi = agrupacioCounts.stream().
					filter(a -> c.equals(a[0])).
					collect(Collectors.toList());
				if (!agrupacionsAmbCodi.isEmpty()) {
					Long estatOkCount = agrupacionsAmbCodi.stream().
						filter(a -> IntegracioAccioEstatEnumDto.OK.equals(a[1])).
						map(a -> (Long) a[2]).
						findFirst().orElse(0L);
					Long estatWarnCount = agrupacionsAmbCodi.stream().
						filter(a -> IntegracioAccioEstatEnumDto.WARN.equals(a[1])).
						map(a -> (Long) a[2]).
						findFirst().orElse(0L);
					Long estatErrorCount = agrupacionsAmbCodi.stream().
						filter(a -> IntegracioAccioEstatEnumDto.ERROR.equals(a[1])).
						map(a -> (Long) a[2]).
						findFirst().orElse(0L);
					return new MonitorIntegracioResource.MonitorIntegracioAgrupacioItem(
						c,
						estatOkCount,
						estatWarnCount,
						estatErrorCount);
				} else {
					return new MonitorIntegracioResource.MonitorIntegracioAgrupacioItem(c, 0, 0, 0);
				}
			}).collect(Collectors.toList());
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
