/**
 * 
 */
package es.caib.notib.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.CacheDto;
import es.caib.notib.logic.intf.dto.PaginaDto;

/**
 * Service per a la gestió de les cachés de l'aplicació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CacheService {

	/**
	 * Retorna una llista de les cachés reconegudes per el manager actual
	 * 
	 * @return una llista amb els codis de les caches
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	PaginaDto<CacheDto> getAllCaches();

	/**
	 * Esborra una cache donat el seu codi (value)
	 * 
	 * @param value el codi de la caché a esborrar
	 * 
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	void removeCache(String value);

	/**
	 * Esborra totes les caches
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	void removeAllCaches();
	
}
