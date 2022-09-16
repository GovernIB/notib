package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la consulta dels procediments associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GrupService {

	/**
	 * Crea un nou grup.
	 * 
	 * @param entitatId Informació de l'entitat actual
	 * @param grup Informació del grup a crear
	 * @return El grup creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public GrupDto create(Long entitatId, GrupDto grup);
	
	/**
	 * Modifica la informació d'un grup.
	 * 
	 * @param grup El grup que es vol modificar.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb el codi especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public GrupDto update(GrupDto grup) throws NotFoundException;

	/**
	 * Esborra un grup donat el seu id.
	 * 
	 * @param id Atribut id del grup que es vol esborrar
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public GrupDto delete(Long id) throws NotFoundException;
	
	/**
	 * Esborra els grups d'un procediment.
	 * 
	 * @param grups Llista dels grups que es volen esborrar
	 * @throws NotFoundException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<GrupDto> deleteGrupsProcediment(List<GrupDto> grups) throws NotFoundException;

	/**
	 * Consulta un grup donat el seu codi.
	 * 
	 * @param id id del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public GrupDto findById(Long entitatId, Long id);
	
	/**
	 * Consulta un grup donat el seu codi.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public GrupDto findByCodi(String grupCodi, Long entitatId);

	
	/**
	 * Consulta un grup donat l'id d'un procediment.
	 * 
	 * @param entitatId Attribut id de l'entitat acutal
	 * @param procedimentId id del procediment a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public PaginaDto<ProcSerGrupDto> findByProcSer(
			Long entitatId,
			Long procedimentId,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Consulta un grup donat el seu codi.
	 * 
	 * @param procedimentId del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<GrupDto> findByProcedimentAndUsuariGrups(Long procedimentId);
	
	/**
	 * Consulta un grup donat el seu codi.
	 * 
	 * @param procedimentId del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<GrupDto> findGrupsByProcSer(Long procedimentId);
	
	/**
	 * Consulta un grup donat el seu codi.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public ProcSerGrupDto findProcedimentGrupById(Long entitatId, Long procedimentGrupId);
	
	/**
	 * Consulta un grup donat el seu codi.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public Boolean existProcedimentGrupByGrupId(Long entitatId, Long grupId);
	
	/**
	 * Consulta els grups que pertanyen a una entitat
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<GrupDto> findByEntitat(Long entitatId);

	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<GrupDto> findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor);
	
	/**
	 * Consulta dels grups segons els paràmetres del filtre.
	 * 
	 * @param entitatId Id de l'entitat actual
	 * @param filtre Paràmetres per a filtrar els resultats.
	 * @param paginacioParams Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb els grups.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public PaginaDto<GrupDto> findAmbFiltrePaginat(Long entitatId, GrupFiltreDto filtre, PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els grups.
	 * 
	 * @return La llista dels grups.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<GrupDto> findAll();

	/**
	 * Llistat amb tots els grups paginats.
	 * 
	 * @param paginacioParams Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de grups.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public PaginaDto<GrupDto> findAllPaginat(PaginacioParamsDto paginacioParams);
}
