package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.*;
import es.caib.notib.core.api.exception.NotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la consulta dels procediments associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentService {

	public enum TipusPermis { PROCEDIMENT, PROCEDIMENT_ORGAN }
	/**
	 * Crea un nou procediment.
	 *
	 * @param procediment
	 *            Informació del procediment a crear.
	 * @return El procediment creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	ProcSerDto create(
			Long entitatId,
			ProcSerDataDto procediment);

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
	public ProcSerDto update(
			Long entitatId,
			ProcSerDataDto procediment,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException;

	/**
	 * Marca el procediment amb l'id especificat com a actiu/inactiu.
	 *
	 * @param id
	 *            Atribut id del procediment a activar.
	 * @param actiu
	 *            true si es vol activar o false en cas contrari.
	 * @return El procediment modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public ProcSerDto updateActiu(
			Long id,
			boolean actiu) throws NotFoundException;

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
	public ProcSerDto delete(
			Long entitatId,
			Long id,
			boolean isAdminEntitat) throws NotFoundException;

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
	public ProcSerDto findById(
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
	public ProcSerDto findByCodi(
			Long entitatId,
			String codiProcediment) throws NotFoundException;

	/**
	 * Consulta els procediments d'una entitat.
	 * 
	 * @param entitatId Identificador de l'entitat
	 * @return El procediment amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerSimpleDto> findByEntitat(Long entitatId);
	
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
	public List<ProcSerSimpleDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(
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
	public PaginaDto<ProcSerFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			OrganGestorDto organGestorActual,
			ProcSerFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els procediments.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findAll();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean procedimentEnUs(Long procedimentId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean procedimentAmbGrups(Long procedimentId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean procedimentActiu(Long procedimentId);
	/**
	 * Llistat amb tots els grups.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerGrupDto> findAllGrups();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId);
	
	/**
	 * Llistat amb tots els procediments sense grups.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findProcediments(Long entitatId, List<String> grups);

	/**
	 * Get all Procediments with the given permission for the given user
	 *
	 * @param entitatId
	 * @param usuariCodi
	 * @param permis
	 * @return Procediments with the given permission for the given user
	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<ProcSerSimpleDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<ProcSerSimpleDto> findProcedimentServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<ProcSerSimpleDto> findProcedimentServeisWithPermisMenu(Long entitatId, String usuariCodi, PermisEnum permis);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findProcedimentsSenseGrups(Long entitatId);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis);

//	/**
//	 * Consulta tots els procediments amb permís per algún organ gestor
//	 *
//	 * @param entitatId
//	 * @param usuariCodi
//	 * @param permis
//	 *
//	 * @return
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<ProcSerOrganDto> findProcedimentsOrganWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);

	/**
	 * Selecciona tots els procediments als que l'organ indicat té accés
	 *
	 * @param organId
	 * @param entitatCodi
	 * @param procedimentsOrgans
	 *
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerOrganDto> findProcedimentsOrganWithPermisByOrgan(String organId, String entitatCodi, List<ProcSerOrganDto> procedimentsOrgans);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(ProcSerDto procediment, String entitatCodi, List<ProcSerOrganDto> procedimentsOrgans);

	/**
	 * Recupera els tipus d'assumpte d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat);
	
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
	 * @param procedimentId
	 *            Atribut id del permis.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long procedimentId,
			String organ,
			String organActual,
			TipusPermis tipus,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
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
			String organ,
			Long permisId,
			TipusPermis tipus) throws NotFoundException;
	
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
	public ProcSerGrupDto grupCreate(
			Long entitatId,
			Long id,
			ProcSerGrupDto procedimentGrup) throws NotFoundException;
	
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
	public ProcSerGrupDto grupUpdate(
			Long entitatId,
			Long id,
			ProcSerGrupDto procedimentGrup) throws NotFoundException;
	
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
	public ProcSerGrupDto grupDelete(
			Long entitatId,
			Long GrupId) throws NotFoundException;

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
	 * @param entitat
	 *            Id de l'entitat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void refrescarCache(
			EntitatDto entitat);

	
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public List<ProcSerDto> findProcedimentsByOrganGestor(String organGestorCodi);

	@PreAuthorize("hasRole('tothom')")
	public List<ProcSerDto> findProcedimentsByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi,
			List<String> grups,
			PermisEnum permis);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<CodiValorOrganGestorComuDto> getProcedimentsOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis);

	/**
	 * Obté un llistat de tots els procediments notificables d'un organ gestor concret
	 *
	 * @param entitatId Entitat de l'òrgan a consultar
	 * @param organCodi Codi Dir3 de l'òrgan
	 * @param rol Rol de l'usuari per seleccionar els procediments permesos per aquest rol
	 * @param enviamentTipus Indica si es tracta d'una notificació/comunicació normal o comunicació SIR
	 *
	 * @return Llistat amb la informació de tots els procediments seleccionats.
	 *
	 */
	@PreAuthorize("hasRole('tothom')")
	List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(
			Long entitatId,
			String organCodi,
			RolEnumDto rol,
			TipusEnviamentEnumDto enviamentTipus);

	/**
	 * Consulta si l'usuari té permís de notificació a tots els procediments comuns per a algún òrgan gestor.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param enviamentTipus Indica si es tracta d'una notificació/comunicació normal o comunicació SIR
	 * @return boleà indicant si es te permis de procediments comuns a algun òrgan
	 */
	boolean hasProcedimentsComunsAndNotificacioPermission(Long entitatId, TipusEnviamentEnumDto enviamentTipus);

	/**
	 * Actualitza el procediment indicat amb la informació del procediment actual
	 * retornada pel plugin Gestor Documental Administratiu (GDA)
	 */
	boolean actualitzarProcediment(String codiSia, EntitatDto entitat);

	/**
	 * Actualitza els procediments de la entitat indicada amb la informació dels procediments actual
	 * retornada pel plugin Gestor Documental Administratiu (GDA)
	 *
	 * @param entitat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	void actualitzaProcediments(EntitatDto entitat);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi);

	/**
	 * Consulta si existeix un procés en curs actualitzant els procediments de l'entitat indicada.
	 *
	 * @param entitatDto Entitat que es vol consultar
	 * @return boolean indicant si existeix un procés en segon pla actualitzant els procediements de l'entitat indicada.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	boolean isUpdatingProcediments(EntitatDto entitatDto);

	/**
	 * Consulta un procediment donat el seu nom i entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param nomProcediment
	 *            Atribut nom del procediment a trobar.
	 * @return El procediment amb el nom especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'procediment amb el nom especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	ProcSerDto findByNom(
			Long entitatId,
			String nomProcediment) throws NotFoundException;


	Integer getProcedimentsAmbOrganNoSincronitzat(Long entitatId);
}
