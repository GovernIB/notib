package es.caib.notib.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels procediments associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentService {

	/**
	 * Crea un nou procediment.
	 * 
	 * @param procediment
	 *            Informació del procediment a crear.
	 * @return El procediment creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public ProcedimentDto create(ProcedimentDto procediment);

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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public ProcedimentDto update(ProcedimentDto procediment) throws NotFoundException;

	/**
	 * Esborra el procediment amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del procediment a esborrar.
	 * @return El procediment esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public ProcedimentDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un procediment donat el seu codi.
	 * 
	 * @param codi
	 *            Codi del procediment a trobar.
	 * @return El procediment amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public ProcedimentDto findById(Long codi);

	/**
	 * Consulta els procediments d'una entitat.
	 * 
	 * @param codi
	 *            Codi del procediment a trobar.
	 * @return El procediment amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public List<ProcedimentDto> findByEntitat(Long entitatId);
	
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
	public PaginaDto<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els procediments.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public List<ProcedimentDto> findAll();

	/**
	 * Llistat amb tots els procediments paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public PaginaDto<ProcedimentDto> findAllPaginat(PaginacioParamsDto paginacioParams);

	/**
	 * Consulta els grups de del procediment.
	 * 
	 * @param codi
	 *            Codi del procediment del qual s'han de mostrar els grups (rols).
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public List<GrupDto> permisFindByProcedimentCodi(
			String codi) throws NotFoundException;

}
