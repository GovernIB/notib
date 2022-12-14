/**
 * 
 */
package es.caib.notib.core.api.service;

import java.util.List;
import java.util.Map;

import es.caib.notib.core.api.dto.IntegracioFiltreDto;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió del item monitorIntegracio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioService {

	/**
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public List<IntegracioDto> integracioFindAll();
	
	/**
	 * Obté la llista de les darreres accions realitzades a una integració.
	 * 
	 * @param codi
	 *             Codi de la integració.
	 * @return La llista amb les darreres accions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la integració amb el codi especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) throws NotFoundException;

	/** Consulta el número d'errors per integració. */
	@PreAuthorize("hasRole('NOT_SUPER')")
	Map<String, Integer> countErrors();

	@PreAuthorize("hasRole('NOT_SUPER')")
	void netejarMonitor();


}
