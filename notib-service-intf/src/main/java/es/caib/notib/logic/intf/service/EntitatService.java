/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.EntitatDataDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;

/**
 * Service per a la gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatService {

	Long getLastPermisosModificatsInstant();

	/**
	 * Crea una nova entitat.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a crear.
	 * @return L'Entitat creada.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public EntitatDto create(EntitatDataDto entitat);

	/**
	 * Actualitza la informació de l'entitat que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a modificar.
	 * @return L'entitat modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public EntitatDto update(EntitatDataDto entitat) throws NotFoundException;

	/**
	 * Marca l'entitat amb l'id especificat com a activa/inactiva.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a esborrar.
	 * @param activa
	 *            true si es vol activar o false en cas contrari.
	 * @return L'entitat modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public EntitatDto updateActiva(
			Long id,
			boolean activa) throws NotFoundException;

	/**
	 * Esborra l'entitat amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a esborrar.
	 * @return L'entitat esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public EntitatDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a trobar.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public EntitatDto findById(Long id);

	/**
	 * Consulta una entitat donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'entitat a trobar.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public EntitatDto findByCodi(String codi);

	/**
	 * Consulta una entitat donat el seu codi.
	 * 
	 * @param dir3Codi
	 *            Codi DIR3 de l'entitat a trobar.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public EntitatDto findByDir3codi(String dir3Codi);

	/**
	 * Llistat amb totes les entitats.
	 * 
	 * @return La llista d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<EntitatDto> findAll();
	
	/**
	 * Consulta els tipus de document d'una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<TipusDocumentDto> findTipusDocumentByEntitat(Long entitatId);
	
	/**
	 * Consulta els tipus de document d'una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public TipusDocumentEnumDto findTipusDocumentDefaultByEntitat(Long entitatId);
	

	/**
	 * Llistat amb totes les entitats paginades.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams);

	/**
	 * Llistat amb les entitats accessibles per a l'usuari actual.
	 * 
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<EntitatDto> findAccessiblesUsuariActual(String rolActual);

	/**
	 * Comprova si l'usuari acutal té permisos d'usuari de l'entitat actual
	 * 
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean hasPermisUsuariEntitat();
	
	/**
	 * Comprova si l'usuari acutal té permisos d'administrador de l'entitat actual
	 * 
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean hasPermisAdminEntitat();


	/**
	 * Comprova si l'usuari acutal té permisos d'administrador de lectura per l'entitat actual
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	boolean hasPermisAdminLectura();

	/**
	 * Comprova si l'usuari acutal té permisos d'aplicació de l'entitat actual
	 * 
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean hasPermisAplicacioEntitat();
	
	/**
	 * Comprova si l'usuari acutal té permisos a l'entitat actual
	 * 
	 * @return El un booleà per a cada rol: Usuari, Administrador d'entitats i Aplicació.
	 */
//	@PreAuthorize("isAuthenticated()")
	public Map<RolEnumDto, Boolean> getPermisosEntitatsUsuariActual();
	/**
	 * Consulta els permisos de l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual es volen consultar els permisos.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	List<PermisDto> permisFindByEntitatId(Long entitatId, PaginacioParamsDto paramsDto) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	void permisUpdate(Long entitatId, PermisDto permis) throws NotFoundException;
	
	/**
	 * Esborra els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public void permisDelete(
			Long entitatId,
			Long permisId) throws NotFoundException;
	
	/**
	 * Recupera les oficines d'una entitat a partir del codi DIR3
	 * 
	 * @param dir3codi
	 *            Codi dir3 de l'entitat de la qual volem recuperar les oficines.
	 * @return La llista de les oficines
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public List<OficinaDto> findOficinesEntitat(
			String dir3codi);
	
	@PreAuthorize("isAuthenticated()")
	byte[] getCapLogo() throws NoSuchFileException, IOException;
	
	@PreAuthorize("isAuthenticated()")
	byte[] getPeuLogo() throws NoSuchFileException, IOException;
	
	@PreAuthorize("isAuthenticated()")
	LlibreDto getLlibreEntitat(String dir3Codi);
	
	@PreAuthorize("isAuthenticated()")
	Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatCodi);

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	boolean existeixPermis(Long entitatId, String principal) throws Exception;

	/**
	 * Afegeix l'entitat que està activada a l'aplicació per poder accedir a les
	 * seves propietats
	 */
	void setConfigEntitat(String entitatCodi);

	@PreAuthorize("hasRole('NOT_SUPER')")
	void resetActualitzacioOrgans(Long id);
}
