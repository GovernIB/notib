/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.OrganGestorService;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class OrganGestorServiceBean implements OrganGestorService {

	@Autowired
	OrganGestorService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public OrganGestorDto create(OrganGestorDto dto) {
		return delegate.create(dto);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public OrganGestorDto delete(Long entitatId, Long organId) {
		return delegate.delete(entitatId, organId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void updateNom(Long entitatId, String organGestorCodi) {
		delegate.updateNom(entitatId, organGestorCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void updateNoms(Long entitatId) {
		delegate.updateNoms(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public boolean organGestorEnUs(Long organId) {
		return delegate.organGestorEnUs(organId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OrganGestorDto> findAll() {
		return delegate.findAll();
	}
	
	@Override
	public OrganGestorDto findById(Long entitatId, Long id) {
		return delegate.findById(entitatId, id);
	}
	
	@Override
	public OrganGestorDto findByCodi(Long entitatId, String codi) {
		return delegate.findByCodi(entitatId, codi);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<CodiValorDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		return delegate.findOrgansGestorsCodiByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds) {
		return delegate.findByProcedimentIds(procedimentIds);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<OrganGestorDto> findDescencentsByCodi(Long entitatId, String organCodi) {
		return delegate.findDescencentsByCodi(entitatId, organCodi);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	public List<OrganGestorDto> findAccessiblesByUsuariActual() {
		return delegate.findAccessiblesByUsuariActual();
	}
	
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id) throws NotFoundException {
		return delegate.permisFind(entitatId, id);
	}

	@Override
	public void permisUpdate(Long entitatId, Long id, PermisDto permis) throws NotFoundException {
		delegate.permisUpdate(entitatId, id, permis);
	}

	@Override
	public void permisDelete(Long entitatId, Long id, Long permisId) throws NotFoundException {
		delegate.permisDelete(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		return delegate.findOrganismes(entitat);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {
		return delegate.findOrganismes(entitat, organGestor);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public String findDenominacioOrganisme(String codiDir3) {
		return delegate.findDenominacioOrganisme(codiDir3);
	}

}
