package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.AvisDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisService {

	@PreAuthorize("hasRole('NOT_SUPER')")
	AvisDto create(AvisDto avis);

	@PreAuthorize("hasRole('NOT_SUPER')")
	AvisDto update(AvisDto avis);

	@PreAuthorize("hasRole('NOT_SUPER')")
	AvisDto updateActiva(Long id, boolean activa);

	@PreAuthorize("hasRole('NOT_SUPER')")
	AvisDto delete(Long id);

	@PreAuthorize("hasRole('NOT_SUPER')")
	AvisDto findById(Long id);

	@PreAuthorize("hasRole('NOT_SUPER')")
	PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('tothom') or hasRole('NOT_SUPER')")
	List<AvisDto> findActive();

	@PreAuthorize("hasRole('tothom')")
	List<AvisDto> findActiveAdmin(Long entitatId);

}

