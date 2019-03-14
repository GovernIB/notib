package es.caib.notib.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels pagadors postals associats a un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorPostalService {

	/**
	 * Crea un nou pagador postal.
	 * @param entitatId	
	 * 				Informació de l'entitat actual.
	 * @param postal
	 * 				Informació del pagador postal a crear
	 * @return	El pagador postal creat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorPostalDto create(
			Long entitatId,
			PagadorPostalDto postal);

	/**
	 * Actualitza la informació d'un pagador postal.
	 * 
	 * @param postal	
	 * 			Pagador postal a modificar amb els nous valors
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorPostalDto update(PagadorPostalDto postal) throws NotFoundException;

	/**
	 * Esborra el pagador postal amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del pagador postal a esborrar.
	 * @return El pagador postal esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorPostalDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un pagador postal donat el seu codi.
	 * 
	 * @param id
	 *            Codi del procediment a trobar.
	 * @return El pagador postal amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public PagadorPostalDto findById(Long id);

	/**
	 * Consulta de pagadors postal segons els paràmetres del filtre.
	 * 
	 * @param entitatId
	 * 				Informació de l'entitat actual.
	 * @param filtre
	 *            	Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            	Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb els pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public PaginaDto<PagadorPostalDto> findAmbFiltrePaginat(
			Long entitatId,
			PagadorPostalFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els pagadors postals.
	 * 
	 * @return La llista dels pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public List<PagadorPostalDto> findAll();

	/**
	 * Llistat amb tots els pagadros postals paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public PaginaDto<PagadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams);

	
}
