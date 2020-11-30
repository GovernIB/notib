package es.caib.notib.core.ejb;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricFiltreDto;
import es.caib.notib.core.api.service.HistoricService;


public class HistoricServiceBean implements HistoricService {

	@Autowired
	HistoricService delegate;
	
	@Override
	public Map<OrganGestorDto, List<HistoricAggregationOrganDto>> getHistoricsByOrganGestor(Long entitatId, HistoricFiltreDto filtre) {
		return delegate.getHistoricsByOrganGestor(entitatId, filtre);
	}

	@Override
	public Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> getHistoricsByProcediment(HistoricFiltreDto filtre) {
		return delegate.getHistoricsByProcediment(filtre);
	}

	@Override
	public Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> getHistoricsByEstat(HistoricFiltreDto filtre) {
		return delegate.getHistoricsByEstat(filtre);
	}

	@Override
	public Map<GrupDto, List<HistoricAggregationGrupDto>> getHistoricsByGrup(Long entitatId, HistoricFiltreDto filtre) {
		return delegate.getHistoricsByGrup(entitatId, filtre);
	}

	@Override
	public Map<UsuariDto, List<HistoricAggregationUsuariDto>> getHistoricsByUsuariAplicacio(HistoricFiltreDto filtre, List<String> usersCodis) {
		return delegate.getHistoricsByUsuariAplicacio(filtre, usersCodis);
	}

	@Override
	public List<HistoricAggregationOrganDto> getDadesActualsByOrgansGestor(HistoricFiltreDto filtre) {
		return delegate.getDadesActualsByOrgansGestor(filtre);
	}

	@Override
	public List<HistoricAggregationProcedimentDto> getDadesActualsByProcediment(HistoricFiltreDto filtre) {
		return delegate.getDadesActualsByProcediment(filtre);
	}

	@Override
	public List<HistoricAggregationEstatDto> getDadesActualsByEstat(HistoricFiltreDto filtre) {
		return delegate.getDadesActualsByEstat(filtre);
	}
	
	@Override
	public List<HistoricAggregationGrupDto> getDadesActualsByGrup(HistoricFiltreDto filtre) {
		return delegate.getDadesActualsByGrup(filtre);
	}

	@Override
	public List<HistoricAggregationUsuariDto> getDadesActualsByUsuariAplicacio(HistoricFiltreDto filtre) {
		return delegate.getDadesActualsByUsuariAplicacio(filtre);
	}

}
