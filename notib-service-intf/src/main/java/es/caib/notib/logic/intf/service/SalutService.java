package es.caib.notib.logic.intf.service;


import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import org.springframework.boot.actuate.health.Health;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface SalutService {

	@PreAuthorize("hasRole('NOT_COM')")
	List<IntegracioInfo> getIntegracions();
	@PreAuthorize("hasRole('NOT_COM')")
	List<SubsistemaInfo> getSubsistemes();
	@PreAuthorize("hasRole('NOT_COM')")
	List<ContextInfo> getContexts(String baseUrl);
	SalutInfo checkSalut(String versio, Long latenciaHttpMs);
	Health checkHealthIndicator();

}
