package es.caib.notib.core.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

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


/**
 * Declaració dels mètodes per a la consulta de l'històric
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricService {


	/**
	 * Consulta l'històric de les notificacions agrupades per organ gestor.
	 * 
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	Map<OrganGestorDto, List<HistoricAggregationOrganDto>> getHistoricsByOrganGestor(Long entitatId, HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric de les notificacions agrupades per organ gestor.
	 * 
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> getHistoricsByProcediment(HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric de les notificacions agrupades per organ gestor.
	 * 
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> getHistoricsByEstat(HistoricFiltreDto filtre);
	
	
	/**
	 * Consulta l'històric de les notificacions agrupades per organ gestor.
	 * 
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	Map<GrupDto, List<HistoricAggregationGrupDto>> getHistoricsByGrup(Long entitatId, HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric de les notificacions agrupades per organ gestor.
	 * 
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	Map<UsuariDto, List<HistoricAggregationUsuariDto>> getHistoricsByUsuariAplicacio(HistoricFiltreDto filtre, List<String> usersCodis);

	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per òrgans gestors.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	List<HistoricAggregationOrganDto> getDadesActualsByOrgansGestor(HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per òrgans gestors.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	List<HistoricAggregationProcedimentDto> getDadesActualsByProcediment(HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per òrgans gestors.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	List<HistoricAggregationEstatDto> getDadesActualsByEstat(HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per grups.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	List<HistoricAggregationGrupDto> getDadesActualsByGrup(HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per òrgans gestors.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	List<HistoricAggregationUsuariDto> getDadesActualsByUsuariAplicacio(HistoricFiltreDto filtre);
}
