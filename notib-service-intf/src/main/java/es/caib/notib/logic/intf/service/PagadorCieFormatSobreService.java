package es.caib.notib.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels pagadors cie associats a un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieFormatSobreService {

	/**
	 * Crea un nou format de sobre per un pagador cie.
	 * @param pagadorCieId	
	 * 				Informació de l'entitat actual.
	 * @param cie
	 * 				Informació del pagador cie a crear
	 * @return	El pagador cie creat
	 */
	@PreAuthorize("isAuthenticated()")
	public CieFormatSobreDto create(Long pagadorCieId, CieFormatSobreDto formatSobre);

	/**
	 * Actualitza la informació d'un format de sobre d'un pagador cie.
	 * 
	 * @param cie	
	 * 			Pagador cie a modificar amb els nous valors
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public CieFormatSobreDto update(CieFormatSobreDto formatSobre) throws NotFoundException;

	/**
	 * Esborra un format de sobre d'un pagador cie.
	 * 
	 * @param id
	 *            Atribut id del pagador cie a esborrar.
	 * @return El pagador cie esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public CieFormatSobreDto delete(Long id) throws NotFoundException;

	/**
	 * Consulta un format de sobre d'un pagador cie.
	 * 
	 * @param id
	 *            Codi del procediment a trobar.
	 * @return El pagador cie amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public CieFormatSobreDto findById(Long id);

	
	/**
	 * Llistat amb tots els formats de sobre.
	 * 
	 * @return La llista dels pagadors cie.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<CieFormatSobreDto> findAll();
	
	/**
	 * Llistat amb tots els formats de sobre d'un pagador cie.
	 * 
	 * @return La llista dels pagadors cie.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<CieFormatSobreDto> findFormatSobreByPagadorCie(Long pagadorCieId);

	/**
	 * Llistat amb tots els formats de sobre d'un pagadro cie paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de pagadors cie.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<CieFormatSobreDto> findAllPaginat(Long pagadorCieId, PaginacioParamsDto paginacioParams);

	
}
