package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalFiltreDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la consulta dels pagadors postals associats a un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OperadorPostalService {

	/**
	 * Crea un nou pagador postal.
	 * @param entitatId	
	 * 				Informació de l'entitat actual.
	 * @param postal
	 * 				Informació del pagador postal a crear
	 * @return	El pagador postal creat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	OperadorPostalDto create(Long entitatId, OperadorPostalDataDto postal);

	/**
	 * Actualitza la informació d'un pagador postal.
	 * 
	 * @param postal	
	 * 			Pagador postal a modificar amb els nous valors
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	OperadorPostalDto update(OperadorPostalDataDto postal) throws NotFoundException;

	/**
	 * Esborra el pagador postal amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del pagador postal a esborrar.
	 * @return El pagador postal esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	OperadorPostalDto delete(Long id) throws NotFoundException;

	/**
	 * Consulta un pagador postal donat el seu codi.
	 * 
	 * @param id
	 *            Codi del procediment a trobar.
	 * @return El pagador postal amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	OperadorPostalDto findById(Long id);

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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	PaginaDto<OperadorPostalTableItemDto> findAmbFiltrePaginat(Long entitatId, OperadorPostalFiltreDto filtre, PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els pagadors postals.
	 * 
	 * @return La llista dels pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<OperadorPostalDto> findAll();

	@PreAuthorize("hasRole('tothom')")
	List<IdentificadorTextDto> findAllIdentificadorText();

	@PreAuthorize("hasRole('tothom')")
	List<IdentificadorTextDto> findPagadorsByEntitat(EntitatDto entitat);

	@PreAuthorize("hasRole('tothom')")
	List<IdentificadorTextDto> findNoCaducatsByEntitat(EntitatDto entitatId);

	@PreAuthorize("hasRole('tothom')")
	List<IdentificadorTextDto> findNoCaducatsByEntitatAndOrgan(EntitatDto entitatId, String organCodi);

	/**
	 * Llistat amb els pagadors postal d'una entitat.
	 * 
	 * @return La llista dels pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<OperadorPostalDto> findByEntitat(Long entitatId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor);

	/**
	 * Llistat amb tots els pagadros postals paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de pagadors postals.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	PaginaDto<OperadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams);

}
