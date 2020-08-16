package es.caib.notib.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la consulta dels procediments associats a una entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorService {

	@PreAuthorize("hasRole('NOT_ADMIN')")
	public OrganGestorDto create(OrganGestorDto dto);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public OrganGestorDto delete(Long entitatId, Long organId);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateNom(
			Long entitatId, 
			String organGestorCodi);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateNoms(
			Long entitatId);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	public boolean organGestorEnUs(Long organId);
	
	@PreAuthorize("hasRole('NOT_SUPER')")
	public List<OrganGestorDto> findAll();
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public OrganGestorDto findById(
			Long entitatId,
			Long id);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public OrganGestorDto findByCodi(
			Long entitatId,
			String codi);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<OrganGestorDto> findByEntitat(Long entitatId);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<CodiValorDto> findOrgansGestorsCodiByEntitat(Long entitatId);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;
	
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public List<OrganGestorDto> findAccessiblesByUsuariActual();
	
	/**
	 * Recupera els organimes d'una entitat.
	 * 
	 * @return La llista dels tipus d'assumpte.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_USER')")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor);
	
	/**
	 * Recupera la denominació d'un organime.
	 * 
	 * @return La denominació de l'organisme.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('NOT_USER') or hasRole('NOT_APL')")
	public String findDenominacioOrganisme(String codiDir3);

}
