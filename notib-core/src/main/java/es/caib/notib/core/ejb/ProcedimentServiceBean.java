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

import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.ProcedimentService;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ProcedimentServiceBean implements ProcedimentService {

	@Autowired
	ProcedimentService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public ProcedimentDto create(
			Long entitatId, 
			ProcedimentDto procediment) {
		return delegate.create(
				entitatId, 
				procediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public ProcedimentDto update(
			Long entitatId, 
			ProcedimentDto procediment) throws NotFoundException {
		return delegate.update(
				entitatId, 
				procediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public ProcedimentDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public ProcedimentDto findById(
			Long entitatId, 
			boolean isAdministrador,
			Long id) throws NotFoundException {
		return delegate.findById(
				entitatId, 
				isAdministrador, 
				id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public PaginaDto<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari, 
			boolean isUsuariEntitat,
			boolean isAdministrador, 
			ProcedimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId, 
				isUsuari, 
				isUsuariEntitat, 
				isAdministrador, 
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentGrupDto> findAllGrups() {
		return delegate.findAllGrups();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsSenseGrups() {
		return delegate.findProcedimentsSenseGrups();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<PermisDto> permisFind(
			Long entitatId, 
			boolean isAdministrador, 
			Long id) throws NotFoundException {
		return delegate.permisFind(
				entitatId, 
				isAdministrador, 
				id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void permisUpdate(
			Long entitatId, 
			Long id, 
			PermisDto permis) throws NotFoundException {
		delegate.permisUpdate(
				entitatId, 
				id, 
				permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException {
		delegate.permisDelete(
				entitatId, 
				id, 
				permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void grupCreate(
			Long entitatId,
			Long id,
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		delegate.grupCreate(
				entitatId,
				id,
				procedimentGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void grupUpdate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		delegate.grupUpdate(
				entitatId, 
				id, 
				procedimentGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void grupDelete(
			Long entitatId, 
			Long GrupId) throws NotFoundException {
		delegate.grupDelete(
				entitatId, 
				GrupId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public boolean hasPermisConsultaProcediment() {
		return delegate.hasPermisConsultaProcediment();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public boolean hasPermisNotificacioProcediment() {
		return delegate.hasPermisNotificacioProcediment();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public boolean hasGrupPermisConsultaProcediment(List<ProcedimentDto> procediments) {
		return delegate.hasGrupPermisConsultaProcediment(procediments);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public boolean hasGrupPermisNotificacioProcediment(List<ProcedimentDto> procediments) {
		return delegate.hasGrupPermisNotificacioProcediment(procediments);
	}


}
