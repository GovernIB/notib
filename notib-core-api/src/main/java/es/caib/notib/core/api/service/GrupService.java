package es.caib.notib.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.GrupFiltreDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
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
public interface GrupService {

	/**
	 * Crea un nou grup.
	 * 
	 * @param procediment
	 *            Informació del procediment a crear.
	 * @return El procediment creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public GrupDto create(
			Long procedimentId,
			Long entitatId,
			List<GrupDto> grup);
	
	/**
	 * Modifica els grups d'un procediment.
	 * 
	 * @param codi
	 *            codi del procediment del qual es vol modificar el grup.
	 * @param grup
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb el codi especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public GrupDto update(GrupDto grup) throws NotFoundException;

	/**
	 * Esborra els grups d'un procediment.
	 * 
	 * @param codi
	 *            Atribut codi del procediment del qual es vol esborrar el grup.
	 * @param codiGrup
	 *            Atribut codiGrup del grup que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public GrupDto delete(
			Long id) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<GrupDto> deleteGrupsProcediment(
			List<GrupDto> grups) throws NotFoundException;

	/**
	 * Consulta un grup donat el seu codi.
	 * 
	 * @param id
	 *            id del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public GrupDto findById(Long id);

	/**
	 * Consulta els  grups d'un procediment
	 * 
	 * @param id
	 *            id del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<GrupDto> findByIdProcediment(Long procedimentId);
	
	/**
	 * Consulta un grup donat el seu codi.
	 * 
	 * @param id
	 *            id del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<GrupDto> findByIdProcedimentAndGrupsId(
			Long procedimentId,
			List<Long> grupsId);
	
	/**
	 * Consulta de les notificacions segons els paràmetres del filtre.
	 * 
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb les notificacions.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<GrupDto> findAmbFiltrePaginat(
			Long entitatId,
			GrupFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	/**
	 * Llistat amb tots els procediments.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<GrupDto> findAll();

	/**
	 * Llistat amb tots els procediments paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<GrupDto> findAllPaginat(PaginacioParamsDto paginacioParams);
}
