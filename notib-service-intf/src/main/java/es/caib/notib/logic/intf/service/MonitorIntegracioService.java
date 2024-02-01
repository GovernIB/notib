/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioDetall;
import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;


/**
 * Declaració dels mètodes per a la gestió del item monitorIntegracio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioService {

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
	PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(IntegracioCodiEnum codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) throws NotFoundException;

	/** Consulta el número d'errors per integració. */
	@PreAuthorize("hasRole('NOT_SUPER')")
	Map<IntegracioCodiEnum, Integer> countErrors();

	@PreAuthorize("hasRole('NOT_SUPER')")
	void netejarMonitor();

	@PreAuthorize("hasRole('NOT_SUPER')")
	IntegracioDetall detallIntegracio(Long id);

}
