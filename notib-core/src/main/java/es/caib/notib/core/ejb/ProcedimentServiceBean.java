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

import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
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
	@RolesAllowed({"NOT_ADMIN"})
	public ProcedimentDto create(
			Long entitatId, 
			ProcedimentDto procediment) {
		return delegate.create(
				entitatId, 
				procediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public ProcedimentDto update(
			Long entitatId, 
			ProcedimentDto procediment,
			boolean isAdmin) throws NotFoundException {
		return delegate.update(
				entitatId, 
				procediment,
				isAdmin);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public ProcedimentDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentGrupDto> findAllGrups() {
		return delegate.findAllGrups();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsSenseGrups() {
		return delegate.findProcedimentsSenseGrups();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
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
	@RolesAllowed({"NOT_ADMIN"})
	public void permisUpdate(
			Long entitatId, 
			Long id, 
			PermisDto permis,
			boolean isAdministrador) throws NotFoundException {
		delegate.permisUpdate(
				entitatId, 
				id, 
				permis,
				isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId,
			boolean isAdministrador) throws NotFoundException {
		delegate.permisDelete(
				entitatId, 
				id, 
				permisId,
				isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
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
	@RolesAllowed({"NOT_ADMIN"})
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
	@RolesAllowed({"NOT_ADMIN"})
	public void grupDelete(
			Long entitatId, 
			Long GrupId) throws NotFoundException {
		delegate.grupDelete(
				entitatId, 
				GrupId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasPermisConsultaProcediment(EntitatDto entitat) {
		return delegate.hasPermisConsultaProcediment(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasPermisNotificacioProcediment(EntitatDto entitat) {
		return delegate.hasPermisNotificacioProcediment(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasGrupPermisConsultaProcediment(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		return delegate.hasGrupPermisConsultaProcediment(
				procediments,
				entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasGrupPermisNotificacioProcediment(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		return delegate.hasGrupPermisNotificacioProcediment(
				procediments,
				entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasPermisGestioProcediment(
			Long procedimentId) {
		return delegate.hasPermisGestioProcediment(
				procedimentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasPermisProcessarProcediment(
			String procedimentCodi,
			Long procedimentId,
			boolean isAdministrador) {
		return delegate.hasPermisProcessarProcediment(
				procedimentCodi,
				procedimentId,
				isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
		return delegate.findTipusAssumpte(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<CodiAssumpteDto> findCodisAssumpte(
			EntitatDto entitat, 
			String codiTipusAssumpte) {
		return delegate.findCodisAssumpte(
				entitat, 
				codiTipusAssumpte);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		return delegate.findOrganismes(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OficinaDto> findOficines(EntitatDto entitat) {
		return delegate.findOficines(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<LlibreDto> findLlibres(
			EntitatDto entitat, 
			String oficina) {
		return delegate.findLlibres(entitat, oficina);
	}


}
