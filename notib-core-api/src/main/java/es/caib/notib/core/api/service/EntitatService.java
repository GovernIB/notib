/**
 * 
 */
package es.caib.notib.core.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatService {

	/**
	 * Crea una nova entitat.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a crear.
	 * @return L'Entitat creada.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public EntitatDto create(EntitatDto entitat);

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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public EntitatDto update(EntitatDto entitat) throws NotFoundException;

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
	@PreAuthorize("hasRole('NOT_ADMIN')")
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public EntitatDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a trobar.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public EntitatDto findById(Long id);

	/**
	 * Consulta una entitat donat el seu cif.
	 * 
	 * @param cif
	 *            Atribut cif de l'entitat a trobar.
	 * @return L'entitat amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public EntitatDto findByCif(String cif);
	
	/**
	 * Consulta una entitat donat el seu codi de dir3.
	 * 
	 * @param dir3
	 *            Atribut codi de dir3 de l'entitat a trobar.
	 * @return L'entitat amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public EntitatDto findByDir3(String dir3);
	
	/**
	 * Consulta una entitat donat el seu codi.
	 * 
	 * @param codi
	 *            Atribut codi de l'entitat a trobar.
	 * @return L'entitat amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public EntitatDto findByCodi(String codi);
	
	/**
	 * Llistat amb totes les entitats.
	 * 
	 * @return Una llista amb totes les entitats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<EntitatDto> findAll();
	
	/**
	 * Llistat amb totes les entitats paginades.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'Entitats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb una entitat paginada.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return Una llista amb una sola entitat.
	 */
	@PreAuthorize("hasRole('NOT_REP')")
	public List<EntitatDto> findByEntitatId( Long entitatId );
	
	/**
	 * Llistat amb les entitats accessibles per a l'usuari actual.
	 * 
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP') or hasRole('NOT_APL')")
	public List<EntitatDto> findAccessiblesUsuariActual();

	/**
	 * Consulta els permisos de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a la qual es volen consultar els permisos.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public List<PermisDto> findPermis(
			Long id) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public void updatePermis(
			Long id,
			PermisDto permis) throws NotFoundException;
	
	/**
	 * Esborra els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public void deletePermis(
			Long id,
			Long permisId) throws NotFoundException;
	
	/**
	 * Comprova els permisos de una llista de entitats
	 * 
	 * @param entitatIds
	 *            Identificadors de les entitats que volem consultar.
	 * 
	 * @return Un mapeig de els identificadors de entitat amb els permisos
	 * 		   corresponents de cada entitat
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public Map<Long, List<PermisDto>> findPermisos(
			List<Long> entitatIds);

}
