/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import es.caib.notib.core.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

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
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcedimentDto create(
			Long entitatId, 
			ProcedimentDto procediment) {
		return delegate.create(
				entitatId, 
				procediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcedimentDto update(
			Long entitatId, 
			ProcedimentDto procediment,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException {
		return delegate.update(
				entitatId, 
				procediment,
				isAdmin,
				isAdminEntitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcedimentDto delete(
			Long entitatId, 
			Long id,
			boolean isAdminEntitat) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id,
				isAdminEntitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
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
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public ProcedimentDto findByCodi(Long entitatId, String codiProcediment) throws NotFoundException {
		return delegate.findByCodi(entitatId, codiProcediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcedimentDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		return delegate.findByOrganGestorIDescendents(entitatId, organGestor);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<ProcedimentDto> findByOrganGestorIDescendentsAndComu(
    		Long id,
			OrganGestorDto organGestor) {
        return delegate.findByOrganGestorIDescendentsAndComu(id, organGestor);
    }

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
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
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentDto> findAll() {
		return delegate.findAll();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean procedimentEnUs(Long procedimentId) {
		return delegate.procedimentEnUs(procedimentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean procedimentAmbGrups(Long procedimentId) {
		return delegate.procedimentAmbGrups(procedimentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentGrupDto> findAllGrups() {
		return delegate.findAllGrups();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentGrupDto> findGrupsByEntitat(Long entitatId) {
		return delegate.findGrupsByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentDto> findProcediments(Long entitatId, List<String> grups) {
		return delegate.findProcediments(entitatId, grups);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.findProcedimentsWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsSenseGrups(Long entitatId) {
		return delegate.findProcedimentsSenseGrups(entitatId);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ProcedimentDto> findProcedimentsSenseGrupsWithPermis(Long entitatId, PermisEnum permis) {
//		return delegate.findProcedimentsSenseGrupsWithPermis(entitatId, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcedimentDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {
		return delegate.findProcedimentsAmbGrups(entitatId, grups);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ProcedimentDto> findProcedimentsAmbGrupsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
//		return delegate.findProcedimentsAmbGrupsWithPermis(entitatId, grups, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		return delegate.hasAnyProcedimentsWithPermis(entitatId, grups, permis);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<PermisDto> permisFind(
			Long entitatId, 
			boolean isAdministrador, 
			Long procedimentId,
			String organ,
			String organActual,
			TipusPermis tipus) throws NotFoundException {
		return delegate.permisFind(
				entitatId, 
				isAdministrador, 
				procedimentId,
				organ,
				organActual,
				tipus);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
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
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public void permisDelete(
			Long entitatId,
			Long organGestorId,
			Long procedimentId,
			String organCodi,
			Long permisId,
			TipusPermis tipus) throws NotFoundException {
		delegate.permisDelete(
				entitatId,
				organGestorId,
				procedimentId, 
				organCodi,
				permisId,
				tipus);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcedimentGrupDto grupCreate(
			Long entitatId,
			Long id,
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		return delegate.grupCreate(
				entitatId,
				id,
				procedimentGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcedimentGrupDto grupUpdate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		return delegate.grupUpdate(
				entitatId, 
				id, 
				procedimentGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcedimentGrupDto grupDelete(
			Long entitatId, 
			Long GrupId) throws NotFoundException {
		return delegate.grupDelete(
				entitatId, 
				GrupId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
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
	@RolesAllowed({"tothom"})
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
	@RolesAllowed({"tothom"})
    public List<CodiValorComuDto> getProcedimentsOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis) {
        return delegate.getProcedimentsOrgan(
        		entitatId,
				organCodi,
				organFiltre,
				rol,
				permis);
    }

	@Override
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol) {
		return delegate.getProcedimentsOrganNotificables(
				entitatId,
				organCodi,
				rol);
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
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcedimentOrganDto> findProcedimentsOrganWithPermis(
			Long entitatId,
			String usuariCodi,
			PermisEnum permis) {
		return delegate.findProcedimentsOrganWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcedimentOrganDto> findProcedimentsOrganWithPermisByOrgan(
			String organId,
			String entitatCodi,
			List<ProcedimentOrganDto> procedimentsOrgans) {
		return delegate.findProcedimentsOrganWithPermisByOrgan(organId, entitatCodi, procedimentsOrgans);
	}

	@Override
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(
			ProcedimentDto procediment,
			String entitatCodi,
			List<ProcedimentOrganDto> procedimentsOrgans) {
		return delegate.findProcedimentsOrganCodiWithPermisByProcediment(procediment, entitatCodi, procedimentsOrgans);
	}
}
