/**
 * 
 */
package es.caib.notib.logic.intf.service;

import java.util.List;
import java.util.Map;

import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

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


}