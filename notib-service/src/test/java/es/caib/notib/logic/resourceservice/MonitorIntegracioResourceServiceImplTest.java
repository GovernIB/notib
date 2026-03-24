package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.model.MonitorIntegracioResource;
import es.caib.notib.persist.resourcerepository.MonitorIntegracioResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaris per MonitorIntegracioResourceServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class MonitorIntegracioResourceServiceImplTest {

	@Mock
	private MonitorIntegracioResourceRepository repository;
	@InjectMocks
	private MonitorIntegracioResourceServiceImpl service;

	@Test
	void agrupacioReportGeneratorShouldReturnCorrectCounts() throws Exception {
		// Given
		// Object[] = {IntegracioCodi, Estat, Count}
		Object[] itemOk = new Object[] { IntegracioCodi.USUARIS, IntegracioAccioEstatEnumDto.OK, 5L };
		Object[] itemWarn = new Object[] { IntegracioCodi.USUARIS, IntegracioAccioEstatEnumDto.WARN, 2L };
		Object[] itemError = new Object[] { IntegracioCodi.USUARIS, IntegracioAccioEstatEnumDto.ERROR, 1L };
		Object[] itemOther = new Object[] { IntegracioCodi.REGISTRE, IntegracioAccioEstatEnumDto.OK, 3L };
		// Mock del mètode countByCodiAndEstat per retornar aquestes dades
		Mockito.when(repository.countByCodiAndEstat())
			.thenReturn(List.of(itemOk, itemWarn, itemError, itemOther));
		// When
		MonitorIntegracioResourceServiceImpl.AgrupacioReportGenerator generator =
			service.new AgrupacioReportGenerator();
		List<MonitorIntegracioResource.MonitorIntegracioAgrupacioItem> result =
			generator.generateData(MonitorIntegracioResource.REPORT_AGRUPACIONS, null, null);
		// Then
		MonitorIntegracioResource.MonitorIntegracioAgrupacioItem code1Item =
			result.stream().filter(i -> i.getGrup() == IntegracioCodi.USUARIS).findFirst().orElseThrow();
		assertEquals(5L, code1Item.getCountOk(), "OK count per CODE1 incorrecte");
		assertEquals(2L, code1Item.getCountWarn(), "WARN count per CODE1 incorrecte");
		assertEquals(1L, code1Item.getCountError(), "ERROR count per CODE1 incorrecte");
		MonitorIntegracioResource.MonitorIntegracioAgrupacioItem code2Item =
			result.stream().filter(i -> i.getGrup() == IntegracioCodi.REGISTRE).findFirst().orElseThrow();
		assertEquals(3L, code2Item.getCountOk(), "OK count per CODE2 incorrecte");
		assertEquals(0L, code2Item.getCountWarn(), "WARN count per CODE2 hauria de ser 0");
		assertEquals(0L, code2Item.getCountError(), "ERROR count per CODE2 hauria de ser 0");
	}

}
