package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.xml.bind.ValidationException;
import java.util.List;

/**
 * Declaració dels mètodes per a la consulta dels procediments associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorService {

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public OrganGestorDto create(OrganGestorDto dto);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public OrganGestorDto delete(Long entitatId, Long organId);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public OrganGestorDto update(OrganGestorDto dto);

	/**
	 * Actualitza les dades de l'organ gestor indicat de la base de dades
	 * amb la informació de dir3
	 *
	 * @param entitatId Identificador de l'entitat en curs
	 * @param organGestorCodi Codi Dir3 de l'òrgan gestor a actualitzar
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	void updateOne(
			Long entitatId, 
			String organGestorCodi);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi);

	/**
	 * Consulta si existeix un procés en curs actualitzant els organs de l'entitat indicada.
	 *
	 * @param entitatDto Entitat que es vol consultar
	 * @return boolean indicant si existeix un procés en segon pla actualitzant els organs de l'entitat indicada.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	boolean isUpdatingOrgans(EntitatDto entitatDto);

	/**
	 * Actualitza les dades dels organs gestors de la base de dades
	 * amb la informació de dir3
	 *
	 * @param entitatId Identificador de l'entitat en curs
	 * @param organActualCodiDir3 Codi Dir3 del pare dels òrgans gestors a actualitzar
	 *                            null per actualitzar-los a tots
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	void updateAll(
			Long entitatId, String organActualCodiDir3);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public boolean organGestorEnUs(Long organId);
	
	@PreAuthorize("hasRole('NOT_SUPER')")
	public List<OrganGestorDto> findAll();
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public OrganGestorDto findById(
			Long entitatId,
			Long id);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public OrganGestorDto findByCodi(
			Long entitatId,
			String codi);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<OrganGestorDto> findByEntitat(Long entitatId);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<CodiValorEstatDto> findOrgansGestorsCodiByEntitat(Long entitatId);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	List<OrganGestorDto> findByCodisAndEstat(List<String> codisOrgans, OrganGestorEstatEnum estat);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OrganGestorDto> findDescencentsByCodi(
			Long entitatId,
			String organCodi);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			String organCodiDir3,
			OrganGestorFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void permisUpdate(
			Long entitatId,
			Long id,
			boolean isAdminOrgan,
			PermisDto permis) throws NotFoundException, ValidationException;
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<OrganGestorDto> findAccessiblesByUsuariActual();
	
	/**
	 * Recupera els organimes d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor);

	/**
	 * Recupera el llibre d'un òrgan gestor (anomenat organisme dins Regweb)
	 * 
	 * @return La llista dels codis d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public LlibreDto getLlibreOrganisme(
			Long entitatId,
			String organGestorDir3Codi);
	
	/**
	 * Recupera les oficines SIR d'un òrgan gestor / entitat (anomenat organisme dins Regweb)
	 * 
	 * @param entitatId 
	 * 					Entitat actual
	 * @param dir3codi
	 * 					Codi DIR3 de l'òrgan gestor / entitat del qual es volen recuperar les oficines
	 * @param isFiltre
	 * 					Indicar si la cerca és per emplenar un filtre
	 * @return La llista de les oficines
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OficinaDto> getOficinesSIR(
			Long entitatId,
			String dir3codi,
			boolean isFiltre);

	/**
	 * Recupera els òrgans sobre els que l'usuari actual té el permís
	 * indicat per paràmetre
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param usuariCodi Codi de l'usuari actual
	 * @param permis Permís que volem que tinguin els òrgans consultats
	 *
	 * @return Llistat dels òrgans gestors sobre els que l'usuari té el permís
	 */
	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
	List<OrganGestorDto> findOrgansGestorsWithPermis(
			Long entitatId, 
			String usuariCodi,
			PermisEnum permis);

	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
    public List<CodiValorEstatDto> getOrgansGestorsDisponiblesConsulta(
    		Long entitatId,
			String usuari,
			RolEnumDto rol,
			String organ);

	/**
	 *  Obte el llistat d'organs en format d'arbre
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	Arbre<OrganGestorDto> generarArbreOrgans(String codiEntitat);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OrganGestorDto> getOrgansAsList();
}
