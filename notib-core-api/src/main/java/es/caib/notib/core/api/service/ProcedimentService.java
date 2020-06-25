package es.caib.notib.core.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<ProcedimentDto> findByEntitat(Long entitatId);
	
	
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb tots els procediments.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<ProcedimentDto> findAll();

	/**
	 * Llistat amb tots els grups.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<ProcedimentGrupDto> findAllGrups();
	
	/**
	 * Llistat amb tots els procediments sense grups.
	 * 
	 * @return La llista dels procediments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<ProcedimentDto> findProcedimentsSenseGrups(EntitatDto entitatActual);
	
	/**
	 * Recupera els tipus d'assumpte d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat);
	
	/**
	 * Recupera els codis d'assumpte d'un tipus d'assumpte.
	 * 
	 * @return La llista dels codis d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<CodiAssumpteDto> findCodisAssumpte(
			EntitatDto entitat,
			String codiTipusAssumpte);
	
	/**
	 * Recupera els organimes d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat);
	
	/**
	 * Recupera la denominació d'un organime.
	 * 
	 * @return La denominació de l'organisme.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public String findDenominacioOrganisme(String codiDir3);
	
	/**
	 * Recupera les oficines d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<OficinaDto> findOficines(EntitatDto entitat);
	
	/**
	 * Recupera els llibres d'una entitat i d'una oficina.
	 * 
	 * @return La llista dels codis d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<LlibreDto> findLlibres(
			EntitatDto entitat,
			String oficina);
	
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis,
			boolean isAdministrador) throws NotFoundException;
	
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId,
			boolean isAdministrador) throws NotFoundException;
	
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void grupDelete(
			Long entitatId,
			Long GrupId) throws NotFoundException;

	/**
	 * Comprova si l'usuari actual té permisos de consulta sobre algun procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public boolean hasPermisConsultaProcediment(EntitatDto entitat);

	/**
	 * Comprova si l'usuari actual té permisos de notificació sobre algun procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public boolean hasPermisNotificacioProcediment(EntitatDto entitat);
	
	/**
	 * Comprova si l'usuari actual té permisos de notificació sobre algun procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public boolean hasPermisGestioProcediment(
			Long procedimentId);
	
	/**
	 * Comprova si l'usuari actual té permisos de notificació sobre algun procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public boolean hasPermisProcessarProcediment(
			String procedimentCodi,
			Long procedimentId,
			boolean isAdministrador);
	
	/**
	 * Comprova si l'usuari actual té permisos de consulta i pertany al grup d'un procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public boolean hasGrupPermisConsultaProcediment(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat);

	/**
	 * Comprova si l'usuari actual té permisos de notificació i pertany al grup d'un procediment
	 * 
	 * @return true / false
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public boolean hasGrupPermisNotificacioProcediment(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat);

	/**
	 * buida els procediments en cache per entitat
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void refrescarCache(
			EntitatDto entitat);

	
	// ORGANS GESTORS
	/////////////////////////////////////////////////////////////
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<CodiValorDto> findOrgansGestorsByEntitat(Long entitatId);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(
			Long entitatId, 
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateOrganGestorNom(
			Long entitatId, 
			String organGestorCodi);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateOrgansGestorsNom(
			Long entitatId);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	public OrganGestorDto findOrganGestorById(
			Long entitatId,
			Long id);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<PermisDto> permisOrganGestorFind(
			Long entitatId,
			Long id) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void permisOrganGestorUpdate(
			Long entitatId,
			Long id,
			PermisDto permis) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void permisOrganGestorDelete(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;
	
}
