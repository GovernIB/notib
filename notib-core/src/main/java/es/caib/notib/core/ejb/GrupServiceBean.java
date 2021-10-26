/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.GrupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de GrupService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class GrupServiceBean implements GrupService {

	@Autowired
	GrupService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public GrupDto create(
			Long entitatId, 
			GrupDto grup) {
		return delegate.create(
				entitatId,
				grup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public GrupDto update(GrupDto grup) throws NotFoundException {
		return delegate.update(grup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public GrupDto delete(Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<GrupDto> deleteGrupsProcediment(List<GrupDto> grups) throws NotFoundException {
		return delegate.deleteGrupsProcediment(grups);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public GrupDto findById(Long entitatId, Long id) {
		return delegate.findById(entitatId, id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public PaginaDto<ProcSerGrupDto> findByProcSer(
			Long entitatId, 
			Long procedimentId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByProcSer(
				entitatId, 
				procedimentId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<GrupDto> findByProcedimentAndUsuariGrups(Long procedimentId) {
		return delegate.findByProcedimentAndUsuariGrups(procedimentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public ProcSerGrupDto findProcedimentGrupById(
			Long entitatId, 
			Long procedimentGrupId) {
		return delegate.findProcedimentGrupById(
				entitatId, 
				procedimentGrupId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public Boolean existProcedimentGrupByGrupId(
			Long entitatId, 
			Long grupId) {
		return delegate.existProcedimentGrupByGrupId(entitatId, grupId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<GrupDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<GrupDto> findByEntitatAndOrganGestor(
			EntitatDto entitat, 
			OrganGestorDto organGestor) {
		return delegate.findByEntitatAndOrganGestor(entitat, organGestor);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<GrupDto> findAmbFiltrePaginat(
			Long entitatId, 
			GrupFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId, 
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<GrupDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<GrupDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public GrupDto findByCodi(
			String grupCodi,
			Long entitatId) {
		return delegate.findByCodi(grupCodi,entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<GrupDto> findGrupsByProcSer(Long procedimentId) {
		return delegate.findGrupsByProcSer(procedimentId);
	}

}
