package es.caib.notib.logic.intf.service;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la consulta dels serveis associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiService {

	/**
	 * Crea un nou servei.
	 * 
	 * @param servei
	 *            Informació del servei a crear.
	 * @return El servei creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	ProcSerDto create(Long entitatId, ProcSerDataDto servei);

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
	ProcSerDto update(Long entitatId, ProcSerDataDto servei, boolean isAdmin, boolean isAdminEntitat) throws NotFoundException;

	/**
	 * Marca el servei amb l'id especificat com a actiu/inactiu.
	 *
	 * @param id
	 *            Atribut id del servei a activar.
	 * @param actiu
	 *            true si es vol activar o false en cas contrari.
	 * @return El servei modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	ProcSerDto updateActiu(Long id, boolean actiu) throws NotFoundException;

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
	ProcSerDto delete(Long entitatId, Long id, boolean isAdminEntitat) throws NotFoundException;

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
	ProcSerDto findById(Long entitatId, boolean isAdministrador, Long id) throws NotFoundException;
	
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
	ProcSerDto findByCodi(Long entitatId, String codiServei) throws NotFoundException;

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
	List<ProcSerSimpleDto> findByOrganGestorIDescendents(Long entitatId, OrganGestorDto organGestor);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerDto> findByOrganGestorIDescendentsAndComu(Long entitatId, OrganGestorDto organGestor);
	
	
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
	PaginaDto<ProcSerFormDto> findAmbFiltrePaginat(Long entitatId, boolean isUsuari, boolean isUsuariEntitat, boolean isAdministrador, OrganGestorDto organGestorActual,
												   ProcSerFiltreDto filtre, PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els serveis.
	 * 
	 * @return La llista dels serveis.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerDto> findAll();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	boolean serveiEnUs(Long serveiId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	boolean serveiAmbGrups(Long serveiId);
	/**
	 * Llistat amb tots els grups.
	 * 
	 * @return La llista dels serveis.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerGrupDto> findAllGrups();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId);
	
	/**
	 * Llistat amb tots els serveis sense grups.
	 * 
	 * @return La llista dels serveis.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerDto> findServeis(Long entitatId, List<String> grups);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerDto> findServeisSenseGrups(Long entitatId);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<ProcSerDto> findServeisAmbGrups(Long entitatId, List<String> grups);
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis);

	/**
	 * Selecciona tots els serveis als que l'organ indicat té accés
	 *
	 * @param organGestorCodi
	 *
	 * @return
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	List<ProcSerDto> findServeisByOrganGestor(String organGestorCodi);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<CodiValorOrganGestorComuDto> getServeisOrgan(Long entitatId, String organCodi, Long organFiltre, RolEnumDto rol, PermisEnum permis);

	/**
	 * Obté un llistat de tots els serveis notificables d'un organ gestor concret
	 *
	 * @param entitatId Entitat de l'òrgan a consultar
	 * @param organCodi Codi Dir3 de l'òrgan
	 * @param rol Rol de l'usuari per seleccionar els serveis permesos per aquest rol
	 * @param enviamentTipus Indica si es tracta d'una notificació/comunicació normal o comunicació SIR
	 *
	 * @return Llistat amb la informació de tots els serveis seleccionats.
	 *
	 */
	@PreAuthorize("hasRole('tothom')")
	List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, EnviamentTipus enviamentTipus);

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
	ProcSerDto findByNom(Long entitatId, String nomServei) throws NotFoundException;

	Integer getServeisAmbOrganNoSincronitzat(Long entitatId);
}
