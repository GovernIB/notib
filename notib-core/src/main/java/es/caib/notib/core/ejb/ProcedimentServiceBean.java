/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.*;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.ProcedimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

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
	public ProcSerDto create(
			Long entitatId, 
			ProcSerDataDto procediment) {
		return delegate.create(
				entitatId, 
				procediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcSerDto update(
			Long entitatId,
			ProcSerDataDto procediment,
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
	public ProcSerDto delete(
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
	public ProcSerDto findById(
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
	public ProcSerDto findByCodi(Long entitatId, String codiProcediment) throws NotFoundException {
		return delegate.findByCodi(entitatId, codiProcediment);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerSimpleDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcSerSimpleDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		return delegate.findByOrganGestorIDescendents(entitatId, organGestor);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(
    		Long id,
			OrganGestorDto organGestor) {
        return delegate.findByOrganGestorIDescendentsAndComu(id, organGestor);
    }

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public PaginaDto<ProcSerFormDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari, 
			boolean isUsuariEntitat,
			boolean isAdministrador, 
			OrganGestorDto organGestorActual,
			ProcSerFiltreDto filtre,
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
	public List<ProcSerDto> findAll() {
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
	public List<ProcSerGrupDto> findAllGrups() {
		return delegate.findAllGrups();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId) {
		return delegate.findGrupsByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerDto> findProcediments(Long entitatId, List<String> grups) {
		return delegate.findProcediments(entitatId, grups);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerSimpleDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.findProcedimentsWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerSimpleDto> findProcedimentServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.findProcedimentServeisWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	public List<ProcSerSimpleDto> findProcedimentServeisWithPermisMenu(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.findProcedimentServeisWithPermisMenu(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerDto> findProcedimentsSenseGrups(Long entitatId) {
		return delegate.findProcedimentsSenseGrups(entitatId);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ProcedimentDto> findProcedimentsSenseGrupsWithPermis(Long entitatId, PermisEnum permis) {
//		return delegate.findProcedimentsSenseGrupsWithPermis(entitatId, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {
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
			TipusPermis tipus,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.permisFind(
				entitatId, 
				isAdministrador, 
				procedimentId,
				organ,
				organActual,
				tipus,
				paginacioParams);
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
	public ProcSerGrupDto grupCreate(
			Long entitatId,
			Long id,
			ProcSerGrupDto procedimentGrup) throws NotFoundException {
		return delegate.grupCreate(
				entitatId,
				id,
				procedimentGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcSerGrupDto grupUpdate(
			Long entitatId, 
			Long id, 
			ProcSerGrupDto procedimentGrup) throws NotFoundException {
		return delegate.grupUpdate(
				entitatId, 
				id, 
				procedimentGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcSerGrupDto grupDelete(
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
	public List<ProcSerDto> findProcedimentsByOrganGestor(String organGestorCodi) {
		return delegate.findProcedimentsByOrganGestor(organGestorCodi);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ProcSerDto> findProcedimentsByOrganGestorWithPermis(
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
    @RolesAllowed({"NOT_ADMIN", "tothom"})
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
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, TipusEnviamentEnumDto enviamentTipus) {
		return delegate.getProcedimentsOrganNotificables(
				entitatId,
				organCodi,
				rol,
				enviamentTipus);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean hasProcedimentsComunsAndNotificacioPermission(Long entitatId, TipusEnviamentEnumDto enviamentTipus) {
		return delegate.hasProcedimentsComunsAndNotificacioPermission(entitatId, enviamentTipus);
	}

	@Override
	public boolean actualitzarProcediment(String codiSia, EntitatDto entitat) {
		return delegate.actualitzarProcediment(codiSia, entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void actualitzaProcediments(EntitatDto entitat) {
		delegate.actualitzaProcediments(entitat);
	}

	@Override
	public boolean isUpdatingProcediments(EntitatDto entitatDto) {
		return delegate.isUpdatingProcediments(entitatDto);
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		return delegate.getProgresActualitzacio(dir3Codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcSerOrganDto> findProcedimentsOrganWithPermis(
			Long entitatId,
			String usuariCodi,
			PermisEnum permis) {
		return delegate.findProcedimentsOrganWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcSerOrganDto> findProcedimentsOrganWithPermisByOrgan(
			String organId,
			String entitatCodi,
			List<ProcSerOrganDto> procedimentsOrgans) {
		return delegate.findProcedimentsOrganWithPermisByOrgan(organId, entitatCodi, procedimentsOrgans);
	}

	@Override
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(
			ProcSerDto procediment,
			String entitatCodi,
			List<ProcSerOrganDto> procedimentsOrgans) {
		return delegate.findProcedimentsOrganCodiWithPermisByProcediment(procediment, entitatCodi, procedimentsOrgans);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public ProcSerDto findByNom(
			Long entitatId,
			String nomProcediment) throws NotFoundException {
		return delegate.findByNom(entitatId, nomProcediment);
	}

	@Override
	public Integer getProcedimentsAmbOrganNoSincronitzat(Long entitatId) {
		return delegate.getProcedimentsAmbOrganNoSincronitzat(entitatId);
	}
}
