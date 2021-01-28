package es.caib.notib.core.api.service;

import java.util.List;

import javax.xml.bind.ValidationException;

import es.caib.notib.core.api.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.exception.NotFoundException;

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
	public OrganGestorDto updateOficina(OrganGestorDto dto);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void updateNom(
			Long entitatId, 
			String organGestorCodi);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public void updateNoms(
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
	public List<CodiValorDto> findOrgansGestorsCodiByEntitat(Long entitatId);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds);
	
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
			Long id) throws NotFoundException;
	
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
	 * Recupera la denominació d'un organime.
	 * 
	 * @return La denominació de l'organisme.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public String findDenominacioOrganisme(String codiDir3);

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
	 * Recupera les oficines SIR d'un òrgan gestor (anomenat organisme dins Regweb)
	 * 
	 * @return La llista dels codis d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
	public List<OficinaDto> getOficinesOrganisme(
			Long entitatId,
			String organGestorDir3Codi);
	
	/**
	 * Recupera els òrgans sobre els quals té permís l'usuari actual
	 * 
	 * @param entitatId
	 * @param permis
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
	public List<OrganGestorDto> findOrgansGestorsWithPermis(
			Long entitatId, 
			String usuariCodi,
			PermisEnum permis);

	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
    public List<CodiValorDto> getOrgansGestorsDisponiblesConsulta(
    		Long entitatId,
			String usuari,
			RolEnumDto rol,
			String organ);
}
