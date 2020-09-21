package es.caib.notib.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.TipusAssumpteDto;
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public ProcedimentDto create(
			Long entitatId,
			ProcedimentDto procediment);

	/**
	 * Actualitza la informació del procediment 
	 * 
	 * @param procediment
	 *            Informació del procediment a modificar.
	 * @return El procediment modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public ProcedimentDto update(
			Long entitatId,
			ProcedimentDto procediment,
			boolean isAdmin) throws NotFoundException;

	/**
	 * Esborra el procediment amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del procediment a esborrar.
	 * @return El procediment esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public ProcedimentDto delete(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un procediment donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del procediment a trobar.
	 * @return El meta-expedient amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public ProcedimentDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un procediment donat el seu codi i entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param codiProcediment
	 *            Atribut codi del procediment a trobar.
	 * @return El meta-expedient amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public ProcedimentDto findByCodi(
			Long entitatId,
			String codiProcediment) throws NotFoundException;

	/**
	 * Consulta els procediments d'una entitat.
	 * 
	 * @param codi
	 *            Codi del procediment a trobar.
	 * @return El procediment amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findByEntitat(Long entitatId);
	
	/**
	 * Consulta els procediments d'un organ gestor i els seus organs gestors descendents.
	 * 
	 * @param entitatId
	 *            Identificador de la entitat en la que s'està cercant
	 * @param organGestor
	 * 			  Òrgna gestor del que volem obtenir els procediments
	 * @return Els procediments associats a l'òrgan gestor, o a algund els seus descendents.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor);
	
	
	/**
	 * Consulta de les notificacions segons els paràmetres del filtre i permís actual.
	 * 
	 * @param isUsuari
	 * 			  True si l'usuari actual està a la finestra de l'usuari
	 * @param isUsuariEntitat
	 * 			  True si l'usuari actual està a la finestra de l'usuari d'enitat
	 * @param isAdministrador
	 * 			  True si l'usuari actual està a la finestra de l'administrador
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb les notificacions.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			OrganGestorDto organGestorActual,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els procediments.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findAll();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean procedimentEnUs(Long procedimentId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean procedimentAmbGrups(Long procedimentId);
	/**
	 * Llistat amb tots els grups.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentGrupDto> findAllGrups();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentGrupDto> findGrupsByEntitat(Long entitatId);
	
	/**
	 * Llistat amb tots els procediments sense grups.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findProcediments(Long entitatId, List<String> grups);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public List<ProcedimentDto> findProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findProcedimentsSenseGrups(Long entitatId);
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public List<ProcedimentDto> findProcedimentsSenseGrupsWithPermis(Long entitatId, PermisEnum permis);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcedimentDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups);
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public List<ProcedimentDto> findProcedimentsAmbGrupsWithPermis(Long entitatId, List<String> grups, PermisEnum permis);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis);
	
	/**
	 * Recupera els tipus d'assumpte d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat);
	
	/**
	 * Recupera els codis d'assumpte d'un tipus d'assumpte.
	 * 
	 * @return La llista dels codis d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<CodiAssumpteDto> findCodisAssumpte(
			EntitatDto entitat,
			String codiTipusAssumpte);
	
	/**
	 * Consulta els permisos d'un procediment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param isAdministrador
	 * 			  True si l'usuari acutal està com administrador           
	 * @param id
	 *            Atribut id del permis.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long id) throws NotFoundException;
	
	/**
	 * Modifica els permisos d'un usuari o d'un rol per a un procediment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del procediment.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void permisUpdate(
			Long entitatId,
			Long organGestorId,
			Long id,
			PermisDto permis) throws NotFoundException;
	
	/**
	 * Esborra els permisos d'un usuari o d'un rol per a un procediment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del procediment.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void permisDelete(
			Long entitatId,
			Long organGestorId,
			Long id,
			Long permisId) throws NotFoundException;
	
	/**
	 * Assigna un grup a un procediment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del procediment.
	 * @param procedimentGrup
	 *            El grup a assignar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void grupCreate(
			Long entitatId,
			Long id,
			ProcedimentGrupDto procedimentGrup) throws NotFoundException;
	
	/**
	 * Modifica el grup d'un procediment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del procediment.
	 * @param procedimentGrup
	 *            El grup que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void grupUpdate(
			Long entitatId,
			Long id,
			ProcedimentGrupDto procedimentGrup) throws NotFoundException;
	
	/**
	 * Esborra un grup d'un procediment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param GrupId
	 *            Atribut id del grup.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void grupDelete(
			Long entitatId,
			Long GrupId) throws NotFoundException;

//	/**
//	 * Comprova si l'usuari actual té permisos de consulta sobre algun procediment
//	 * 
//	 * @return true / false
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public boolean hasPermisConsultaProcediment(EntitatDto entitat);
//
//	/**
//	 * Comprova si l'usuari actual té permisos de notificació sobre algun procediment
//	 * 
//	 * @return true / false
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public boolean hasPermisNotificacioProcediment(EntitatDto entitat);
	
	/**
	 * Comprova si l'usuari actual té permisos de notificació sobre algun procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean hasPermisProcediment(
			Long procedimentId,
			PermisEnum permis);
	
	/**
	 * buida els procediments en cache per entitat
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void refrescarCache(
			EntitatDto entitat);

	
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public List<ProcedimentDto> findProcedimentsByOrganGestor(String organGestorCodi);
	
	@PreAuthorize("hasRole('tothom')")
	public List<ProcedimentDto> findProcedimentsByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi, 
			List<String> grups, 
			PermisEnum permis);
	
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void actualitzaProcediments(EntitatDto entitat);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi);

}
