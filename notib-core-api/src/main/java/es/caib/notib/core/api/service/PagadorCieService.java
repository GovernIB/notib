package es.caib.notib.core.api.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels pagadors postals associats a un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieService {

	/**
	 * Crea un nou pagador postal.
	 * 
	 * @param procediment
	 *            Informació del procediment a crear.
	 * @return El procediment creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PagadorCieDto create(
			Long entitatId,
			PagadorCieDto cie);

	/**
	 * Actualitza la informació del procediment que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param procediment
	 *            Informació del procediment a modificar.
	 * @return El procediment modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PagadorCieDto update(PagadorCieDto cie) throws NotFoundException;

	/**
	 * Esborra el procediment amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del procediment a esborrar.
	 * @return El procediment esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PagadorCieDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un procediment donat el seu codi.
	 * 
	 * @param codi
	 *            Codi del procediment a trobar.
	 * @return El procediment amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieDto findById(Long id);

	/**
	 * Consulta de les notificacions segons els paràmetres del filtre.
	 * 
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb les notificacions.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PaginaDto<PagadorCieDto> findAmbFiltrePaginat(
			Long entitatId,
			PagadorCieFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els procediments.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') OR hasRole('NOT_SUPER')")
	public List<PagadorCieDto> findAll();

	/**
	 * Llistat amb tots els procediments paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<PagadorCieDto> findAllPaginat(PaginacioParamsDto paginacioParams);

	
}
