/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.OrganGestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.bind.ValidationException;
import java.util.List;

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
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public OrganGestorDto create(OrganGestorDto dto) {
		return delegate.create(dto);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public OrganGestorDto delete(Long entitatId, Long organId) {
		return delegate.delete(entitatId, organId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public void updateNom(Long entitatId, String organGestorCodi) {
		delegate.updateNom(entitatId, organGestorCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public void updateNoms(Long entitatId, String organActualCodiDir3) {
		delegate.updateNoms(entitatId,organActualCodiDir3);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean organGestorEnUs(Long organId) {
		return delegate.organGestorEnUs(organId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OrganGestorDto> findAll() {
		return delegate.findAll();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
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
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		return delegate.findOrgansGestorsCodiByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds) {
		return delegate.findByProcedimentIds(procedimentIds);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<OrganGestorDto> findDescencentsByCodi(Long entitatId, String organCodi) {
		return delegate.findDescencentsByCodi(entitatId, organCodi);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			String organCodiDir3,
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(entitatId, organCodiDir3,filtre, paginacioParams);
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
	public void permisUpdate(Long entitatId, Long id, boolean isAdminOrgan, PermisDto permis) throws NotFoundException, ValidationException {
		delegate.permisUpdate(entitatId, id, isAdminOrgan, permis);
	}

	@Override
	public void permisDelete(Long entitatId, Long id, Long permisId) throws NotFoundException {
		delegate.permisDelete(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		return delegate.findOrganismes(entitat);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {
		return delegate.findOrganismes(entitat, organGestor);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public String findDenominacioOrganisme(String codiDir3) {
		return delegate.findDenominacioOrganisme(codiDir3);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public LlibreDto getLlibreOrganisme(Long entitatId, String organGestorDir3Codi) {
		return delegate.getLlibreOrganisme(entitatId, organGestorDir3Codi);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<OrganGestorDto> findOrgansGestorsWithPermis(
			Long entitatId, 
			String usuariCodi,
			PermisEnum permis) {
		return delegate.findOrgansGestorsWithPermis(
				entitatId, 
				usuariCodi,
				permis);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<CodiValorDto> getOrgansGestorsDisponiblesConsulta(
    		Long entitatId,
			String usuari,
			RolEnumDto rol,
			String organ) {
        return delegate.getOrgansGestorsDisponiblesConsulta(
        		entitatId,
				usuari,
				rol,
				organ);
    }

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public OrganGestorDto updateOficina(OrganGestorDto dto) {
		return delegate.updateOficina(dto);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<OficinaDto> getOficinesSIR(Long entitatId, String organGestorDir3Codi, boolean isFiltre) {
		return delegate.getOficinesSIR(entitatId, organGestorDir3Codi, isFiltre);
	}

}
