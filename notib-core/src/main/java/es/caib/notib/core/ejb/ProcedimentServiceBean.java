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
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public ProcedimentDto create(
			Long entitatId, 
			ProcedimentDto procediment) {
		return delegate.create(
				entitatId, 
				procediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
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
	public ProcedimentDto findByCodi(Long entitatId, String codiProcediment) throws NotFoundException {
		return delegate.findByCodi(entitatId, codiProcediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<ProcedimentDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		return delegate.findByOrganGestorIDescendents(entitatId, organGestor);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari, 
			boolean isUsuariEntitat,
			boolean isAdministrador, 
			OrganGestorDto organGestorActual,
			ProcedimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId, 
				isUsuari, 
				isUsuariEntitat, 
				isAdministrador, 
				organGestorActual,
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findAll() {
		return delegate.findAll();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean procedimentEnUs(Long procedimentId) {
		return delegate.procedimentEnUs(procedimentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentGrupDto> findAllGrups() {
		return delegate.findAllGrups();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentGrupDto> findGrupsByEntitat(Long entitatId) {
		return delegate.findGrupsByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findProcediments(Long entitatId, List<String> grups) {
		return delegate.findProcediments(entitatId, grups);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.findProcedimentsWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsSenseGrups(Long entitatId) {
		return delegate.findProcedimentsSenseGrups(entitatId);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
//	public List<ProcedimentDto> findProcedimentsSenseGrupsWithPermis(Long entitatId, PermisEnum permis) {
//		return delegate.findProcedimentsSenseGrupsWithPermis(entitatId, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {
		return delegate.findProcedimentsAmbGrups(entitatId, grups);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
//	public List<ProcedimentDto> findProcedimentsAmbGrupsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
//		return delegate.findProcedimentsAmbGrupsWithPermis(entitatId, grups, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		return delegate.hasAnyProcedimentsWithPermis(entitatId, grups, permis);
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public void permisUpdate(
			Long entitatId,
			Long organGestorId,
			Long id, 
			PermisDto permis) throws NotFoundException {
		delegate.permisUpdate(
				entitatId,
				organGestorId,
				id, 
				permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public void permisDelete(
			Long entitatId,
			Long organGestorId,
			Long id,
			Long permisId) throws NotFoundException {
		delegate.permisDelete(
				entitatId,
				organGestorId,
				id, 
				permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
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
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public void grupDelete(
			Long entitatId, 
			Long GrupId) throws NotFoundException {
		delegate.grupDelete(
				entitatId, 
				GrupId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER", "NOT_APL"})
	public boolean hasPermisProcediment(
			Long procedimentId,
			PermisEnum permis) {
		return delegate.hasPermisProcediment(
				procedimentId,
				permis);
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
	public void refrescarCache(EntitatDto entitat) {
		delegate.refrescarCache(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public List<ProcedimentDto> findProcedimentsByOrganGestor(String organGestorCodi) {
		return delegate.findProcedimentsByOrganGestor(organGestorCodi);
	}

	@Override
	@RolesAllowed({"NOT_USER"})
	public List<ProcedimentDto> findProcedimentsByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi, 
			List<String> grups,
			PermisEnum permis) {
		return delegate.findProcedimentsByOrganGestorWithPermis(
				entitatId, 
				organGestorCodi, 
				grups, 
				permis);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void actualitzaProcediments(EntitatDto entitat) {
		delegate.actualitzaProcediments(entitat);
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		return delegate.getProgresActualitzacio(dir3Codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<OficinaDto> findOficines(Long entitatId) {
		return delegate.findOficines(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public List<LlibreDto> findLlibres(Long entitatId, String oficina) {
		return delegate.findLlibres(entitatId, oficina);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_USER"})
	public LlibreDto getLlibreOrganisme(Long entitatId, String organGestorDir3Codi) {
		return delegate.getLlibreOrganisme(entitatId, organGestorDir3Codi);
	}

}
