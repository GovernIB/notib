package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.*;
import es.caib.notib.core.api.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la consulta dels serveis associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiService {

	public enum TipusPermis { PROCEDIMENT, PROCEDIMENT_ORGAN }
	/**
	 * Crea un nou servei.
	 * 
	 * @param servei
	 *            Informació del servei a crear.
	 * @return El servei creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	ProcSerDto create(
            Long entitatId,
            ProcSerDataDto servei);

	/**
	 * Actualitza la informació del servei 
	 * 
	 * @param servei
	 *            Informació del servei a modificar.
	 * @return El servei modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public ProcSerDto update(
            Long entitatId,
            ProcSerDataDto servei,
            boolean isAdmin,
            boolean isAdminEntitat) throws NotFoundException;

	/**
	 * Esborra el servei amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id del servei a esborrar.
	 * @return El servei esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public ProcSerDto delete(
            Long entitatId,
            Long id,
            boolean isAdminEntitat) throws NotFoundException;

	/**
	 * Consulta un servei donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del servei a trobar.
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
	 * Consulta un servei donat el seu codi i entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param codiServei
	 *            Atribut codi del servei a trobar.
	 * @return El meta-expedient amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public ProcSerDto findByCodi(
            Long entitatId,
            String codiServei) throws NotFoundException;

	/**
	 * Consulta els serveis d'una entitat.
	 * 
	 * @param entitatId Identificador de l'entitat
	 * @return El servei amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerSimpleDto> findByEntitat(Long entitatId);
	
	/**
	 * Consulta els serveis d'un organ gestor i els seus organs gestors descendents.
	 * 
	 * @param entitatId
	 *            Identificador de la entitat en la que s'està cercant
	 * @param organGestor
	 * 			  Òrgna gestor del que volem obtenir els serveis
	 * @return Els serveis associats a l'òrgan gestor, o a algund els seus descendents.
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
	 * Llistat amb tots els serveis.
	 * 
	 * @return La llista dels serveis.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findAll();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean serveiEnUs(Long serveiId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean serveiAmbGrups(Long serveiId);
	/**
	 * Llistat amb tots els grups.
	 * 
	 * @return La llista dels serveis.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerGrupDto> findAllGrups();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId);
	
	/**
	 * Llistat amb tots els serveis sense grups.
	 * 
	 * @return La llista dels serveis.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findServeis(Long entitatId, List<String> grups);

//	/**
//	 * Get all Serveis with the given permission for the given user
//	 *
//	 * @param entitatId
//	 * @param usuariCodi
//	 * @param permis
//	 * @return Serveis with the given permission for the given user
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<ServeiSimpleDto> findServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findServeisSenseGrups(Long entitatId);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerDto> findServeisAmbGrups(Long entitatId, List<String> grups);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis);

//	/**
//	 * Consulta tots els serveis amb permís per algún organ gestor
//	 *
//	 * @param entitatId
//	 * @param usuariCodi
//	 * @param permis
//	 *
//	 * @return
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<ServeiOrganDto> findServeisOrganWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);

	/**
	 * Selecciona tots els serveis als que l'organ indicat té accés
	 *
	 * @param organId
	 * @param entitatCodi
	 * @param serveisOrgans
	 *
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerOrganDto> findServeisOrganWithPermisByOrgan(String organId, String entitatCodi, List<ProcSerOrganDto> serveisOrgans);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<String> findServeisOrganCodiWithPermisByServei(ProcSerDto servei, String entitatCodi, List<ProcSerOrganDto> serveisOrgans);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<ProcSerSimpleDto> findServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis);

//	/**
//	 * Recupera els tipus d'assumpte d'una entitat.
//	 *
//	 * @return La llista dels tipus d'assumpte.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat);
//
//	/**
//	 * Recupera els codis d'assumpte d'un tipus d'assumpte.
//	 *
//	 * @return La llista dels codis d'assumpte.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public List<CodiAssumpteDto> findCodisAssumpte(
//            EntitatDto entitat,
//            String codiTipusAssumpte);
//
//	/**
//	 * Consulta els permisos d'un servei.
//	 *
//	 * @param entitatId
//	 *            Id de l'entitat.
//	 * @param isAdministrador
//	 * 			  True si l'usuari acutal està com administrador
//	 * @param serveiId
//	 *            Atribut id del permis.
//	 * @return El llistat de permisos.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public List<PermisDto> permisFind(
//            Long entitatId,
//            boolean isAdministrador,
//            Long serveiId,
//            String organ,
//            String organActual,
//            TipusPermis tipus) throws NotFoundException;
//
//	/**
//	 * Modifica els permisos d'un usuari o d'un rol per a un servei.
//	 *
//	 * @param entitatId
//	 *            Id de l'entitat.
//	 * @param id
//	 *            Atribut id del servei.
//	 * @param permis
//	 *            El permís que es vol modificar.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public void permisUpdate(
//            Long entitatId,
//            Long organGestorId,
//            Long id,
//            PermisDto permis) throws NotFoundException;
//
//	/**
//	 * Esborra els permisos d'un usuari o d'un rol per a un servei.
//	 *
//	 * @param entitatId
//	 *            Id de l'entitat.
//	 * @param id
//	 *            Atribut id del servei.
//	 * @param permisId
//	 *            Atribut id del permís que es vol esborrar.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public void permisDelete(
//            Long entitatId,
//            Long organGestorId,
//            Long id,
//            String organ,
//            Long permisId,
//            TipusPermis tipus) throws NotFoundException;
//
//	/**
//	 * Assigna un grup a un servei.
//	 *
//	 * @param entitatId
//	 *            Id de l'entitat.
//	 * @param id
//	 *            Atribut id del servei.
//	 * @param serveiGrup
//	 *            El grup a assignar.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public ServeiGrupDto grupCreate(
//            Long entitatId,
//            Long id,
//            ServeiGrupDto serveiGrup) throws NotFoundException;
//
//	/**
//	 * Modifica el grup d'un servei.
//	 *
//	 * @param entitatId
//	 *            Id de l'entitat.
//	 * @param id
//	 *            Atribut id del servei.
//	 * @param serveiGrup
//	 *            El grup que es vol modificar.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public ServeiGrupDto grupUpdate(
//            Long entitatId,
//            Long id,
//            ServeiGrupDto serveiGrup) throws NotFoundException;
//
//	/**
//	 * Esborra un grup d'un servei.
//	 *
//	 * @param entitatId
//	 *            Id de l'entitat.
//	 * @param GrupId
//	 *            Atribut id del grup.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public ServeiGrupDto grupDelete(
//            Long entitatId,
//            Long GrupId) throws NotFoundException;
//
//	/**
//	 * Comprova si l'usuari actual té permisos de notificació sobre algun servei
//	 *
//	 * @return true / false
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//	public boolean hasPermisServei(
//            Long serveiId,
//            PermisEnum permis);
//
//	/**
//	 * buida els serveis en cache per entitat
//	 *
//	 * @param entitat
//	 *            Id de l'entitat.
//	 */
//	@PreAuthorize("hasRole('NOT_ADMIN')")
//	public void refrescarCache(
//            EntitatDto entitat);
//
//
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public List<ProcSerDto> findServeisByOrganGestor(String organGestorCodi);

	@PreAuthorize("hasRole('tothom')")
	public List<ProcSerDto> findServeisByOrganGestorWithPermis(
            Long entitatId,
            String organGestorCodi,
            List<String> grups,
            PermisEnum permis);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<CodiValorComuDto> getServeisOrgan(
            Long entitatId,
            String organCodi,
            Long organFiltre,
            RolEnumDto rol,
            PermisEnum permis);

	/**
	 * Obté un llistat de tots els serveis notificables d'un organ gestor concret
	 *
	 * @param entitatId Entitat de l'òrgan a consultar
	 * @param organCodi Codi Dir3 de l'òrgan
	 * @param rol Rol de l'usuari per seleccionar els serveis permesos per aquest rol
	 *
	 * @return Llistat amb la informació de tots els serveis seleccionats.
	 *
	 */
	@PreAuthorize("hasRole('tothom')")
	List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(
            Long entitatId,
            String organCodi,
            RolEnumDto rol);

	/**
	 * Consulta si l'usuari té permís de notificació a tots els serveis comuns per a algún òrgan gestor.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @return boleà indicant si es te permis de serveis comuns a algun òrgan
	 */
	boolean hasServeisComunsAndNotificacioPermission(Long entitatId);

	/**
	 * Actualitza el servei indicat amb la informació del servei actual
	 * retornada pel plugin Gestor Documental Administratiu (GDA)
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	boolean actualitzarServei(String codiSia, EntitatDto entitat);

	/**
	 * Actualitza els serveis de la entitat indicada amb la informació dels serveis actual
	 * retornada pel plugin Gestor Documental Administratiu (GDA)
	 *
	 * @param entitat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	void actualitzaServeis(EntitatDto entitat);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi);

	/**
	 * Consulta si existeix un procés en curs actualitzant els serveis de l'entitat indicada.
	 *
	 * @param entitatDto Entitat que es vol consultar
	 * @return boolean indicant si existeix un procés en segon pla actualitzant els procediements de l'entitat indicada.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	boolean isUpdatingServeis(EntitatDto entitatDto);

	/**
	 * Consulta un servei donat el seu nom i entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param nomServei
	 *            Atribut nom del servei a trobar.
	 * @return El servei amb el nom especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'servei amb el nom especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	ProcSerDto findByNom(
            Long entitatId,
            String nomServei) throws NotFoundException;
}
