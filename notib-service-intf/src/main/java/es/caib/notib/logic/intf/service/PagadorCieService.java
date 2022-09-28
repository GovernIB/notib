package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFiltreDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	CieDto create(Long entitatId, CieDataDto cie);

	/**
	 * Actualitza la informació d'un pagador cie.
	 * 
	 * @param cie	
	 * 			Pagador cie a modificar amb els nous valors
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	CieDto update(CieDataDto cie) throws NotFoundException;

	/**
	 * Esborra el pagador cie amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del pagador cie a esborrar.
	 * @return El pagador cie esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws NotFoundException 
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	CieDto delete(Long id) throws NotFoundException;

	/**
	 * Consulta un pagador cie donat el seu codi.
	 * 
	 * @param id
	 *            Codi del procediment a trobar.
	 * @return El pagador cie amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	CieDto findById(Long id);

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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	PaginaDto<CieTableItemDto> findAmbFiltrePaginat(Long entitatId, CieFiltreDto filtre, PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els pagadors cie.
	 * 
	 * @return La llista dels pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<CieDto> findAll();

	List<IdentificadorTextDto> findAllIdentificadorText();

	/**
	 * Llistat amb els pagadors cie d'una entitat.
	 *
	 * @return La llista dels pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<CieDto> findByEntitat(Long entitatId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor);

	/**
	 * Llistat amb tots els pagadros cie paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de pagadors cie.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	PaginaDto<CieDto> findAllPaginat(PaginacioParamsDto paginacioParams);

}
