package es.caib.notib.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels pagadors cie associats a un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieFormatFullaService {

	/**
	 * Crea un nou format de fulla per un pagador cie.
	 * @param pagadorCieId	
	 * 				Id del pagador cie.
	 * @param cie
	 * 				Informació del pagador cie a crear
	 * @return	El pagador cie creat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieFormatFullaDto create(
			Long pagadorCieId,
			PagadorCieFormatFullaDto formatSobre);

	/**
	 * Actualitza la informació d'un format de fulla d'un pagador cie.
	 * 
	 * @param cie	
	 * 			Pagador cie a modificar amb els nous valors
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieFormatFullaDto update(PagadorCieFormatFullaDto formatSobre) throws NotFoundException;

	/**
	 * Esborra un format de fulla d'un pagador cie.
	 * 
	 * @param id
	 *            Atribut id del pagador cie a esborrar.
	 * @return El pagador cie esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PagadorCieFormatFullaDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un format de fulla d'un pagador cie.
	 * 
	 * @param id
	 *            Codi del procediment a trobar.
	 * @return El pagador cie amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public PagadorCieFormatFullaDto findById(Long id);

	
	/**
	 * Llistat amb tots els formats de fulla.
	 * 
	 * @return La llista dels pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public List<PagadorCieFormatFullaDto> findAll();

	/**
	 * Llistat amb tots els formats de fulla d'un pagadro cie paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER')")
	public PaginaDto<PagadorCieFormatFullaDto> findAllPaginat(PaginacioParamsDto paginacioParams);

	
}
