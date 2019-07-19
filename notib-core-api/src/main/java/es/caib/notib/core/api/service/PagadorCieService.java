package es.caib.notib.core.api.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels pagadors cie associats a un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieService {

	/**
	 * Crea un nou pagador cie.
	 * @param entitatId	
	 * 				Informació de l'entitat actual.
	 * @param cie
	 * 				Informació del pagador cie a crear
	 * @return	El pagador cie creat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieDto create(
			Long entitatId,
			PagadorCieDto cie);

	/**
	 * Actualitza la informació d'un pagador cie.
	 * 
	 * @param cie	
	 * 			Pagador cie a modificar amb els nous valors
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieDto update(PagadorCieDto cie) throws NotFoundException;

	/**
	 * Esborra el pagador cie amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del pagador cie a esborrar.
	 * @return El pagador cie esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un pagador cie donat el seu codi.
	 * 
	 * @param id
	 *            Codi del procediment a trobar.
	 * @return El pagador cie amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public PagadorCieDto findById(Long id);

	/**
	 * Consulta de pagadors cie segons els paràmetres del filtre.
	 * 
	 * @param entitatId
	 * 				Informació de l'entitat actual.
	 * @param filtre
	 *            	Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            	Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb els pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public PaginaDto<PagadorCieDto> findAmbFiltrePaginat(
			Long entitatId,
			PagadorCieFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els pagadors cie.
	 * 
	 * @return La llista dels pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public List<PagadorCieDto> findAll();
	
	/**
	 * Llistat amb els pagadors cie d'una entitat.
	 * 
	 * @return La llista dels pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public List<PagadorCieDto> findByEntitat(Long entitatId);

	/**
	 * Llistat amb tots els pagadros cie paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public PaginaDto<PagadorCieDto> findAllPaginat(PaginacioParamsDto paginacioParams);

	
}
