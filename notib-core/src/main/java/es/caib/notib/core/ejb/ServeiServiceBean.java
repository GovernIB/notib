/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.servei.*;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.ServeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ServeiServiceBean implements ServeiService {

	@Autowired
	ServeiService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ServeiDto create(
			Long entitatId, 
			ServeiDataDto servei) {
		return delegate.create(
				entitatId, 
				servei);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ServeiDto update(
			Long entitatId,
			ServeiDataDto servei,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException {
		return delegate.update(
				entitatId, 
				servei,
				isAdmin,
				isAdminEntitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ServeiDto delete(
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
	public ServeiDto findById(
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
	public ServeiDto findByCodi(Long entitatId, String codiServei) throws NotFoundException {
		return delegate.findByCodi(entitatId, codiServei);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiSimpleDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ServeiSimpleDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		return delegate.findByOrganGestorIDescendents(entitatId, organGestor);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<ServeiDto> findByOrganGestorIDescendentsAndComu(
    		Long id,
			OrganGestorDto organGestor) {
        return delegate.findByOrganGestorIDescendentsAndComu(id, organGestor);
    }

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public PaginaDto<ServeiFormDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari, 
			boolean isUsuariEntitat,
			boolean isAdministrador, 
			OrganGestorDto organGestorActual,
			ServeiFiltreDto filtre,
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
	public List<ServeiDto> findAll() {
		return delegate.findAll();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean serveiEnUs(Long serveiId) {
		return delegate.serveiEnUs(serveiId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean serveiAmbGrups(Long serveiId) {
		return delegate.serveiAmbGrups(serveiId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiGrupDto> findAllGrups() {
		return delegate.findAllGrups();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiGrupDto> findGrupsByEntitat(Long entitatId) {
		return delegate.findGrupsByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiDto> findServeis(Long entitatId, List<String> grups) {
		return delegate.findServeis(entitatId, grups);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiSimpleDto> findServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.findServeisWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiDto> findServeisSenseGrups(Long entitatId) {
		return delegate.findServeisSenseGrups(entitatId);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ServeiDto> findServeisSenseGrupsWithPermis(Long entitatId, PermisEnum permis) {
//		return delegate.findServeisSenseGrupsWithPermis(entitatId, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ServeiDto> findServeisAmbGrups(Long entitatId, List<String> grups) {
		return delegate.findServeisAmbGrups(entitatId, grups);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ServeiDto> findServeisAmbGrupsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
//		return delegate.findServeisAmbGrupsWithPermis(entitatId, grups, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		return delegate.hasAnyServeisWithPermis(entitatId, grups, permis);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<PermisDto> permisFind(
			Long entitatId, 
			boolean isAdministrador, 
			Long serveiId,
			String organ,
			String organActual,
			TipusPermis tipus) throws NotFoundException {
		return delegate.permisFind(
				entitatId, 
				isAdministrador, 
				serveiId,
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
			Long serveiId,
			String organCodi,
			Long permisId,
			TipusPermis tipus) throws NotFoundException {
		delegate.permisDelete(
				entitatId,
				organGestorId,
				serveiId, 
				organCodi,
				permisId,
				tipus);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ServeiGrupDto grupCreate(
			Long entitatId,
			Long id,
			ServeiGrupDto serveiGrup) throws NotFoundException {
		return delegate.grupCreate(
				entitatId,
				id,
				serveiGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ServeiGrupDto grupUpdate(
			Long entitatId, 
			Long id, 
			ServeiGrupDto serveiGrup) throws NotFoundException {
		return delegate.grupUpdate(
				entitatId, 
				id, 
				serveiGrup);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ServeiGrupDto grupDelete(
			Long entitatId, 
			Long GrupId) throws NotFoundException {
		return delegate.grupDelete(
				entitatId, 
				GrupId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public boolean hasPermisServei(
			Long serveiId,
			PermisEnum permis) {
		return delegate.hasPermisServei(
				serveiId,
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
	public List<ServeiDto> findServeisByOrganGestor(String organGestorCodi) {
		return delegate.findServeisByOrganGestor(organGestorCodi);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ServeiDto> findServeisByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi, 
			List<String> grups,
			PermisEnum permis) {
		return delegate.findServeisByOrganGestorWithPermis(
				entitatId, 
				organGestorCodi, 
				grups, 
				permis);
	}

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<CodiValorComuDto> getServeisOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis) {
        return delegate.getServeisOrgan(
        		entitatId,
				organCodi,
				organFiltre,
				rol,
				permis);
    }

	@Override
	public List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol) {
		return delegate.getServeisOrganNotificables(
				entitatId,
				organCodi,
				rol);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean hasServeisComunsAndNotificacioPermission(Long entitatId) {
		return delegate.hasServeisComunsAndNotificacioPermission(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void actualitzaServeis(EntitatDto entitat) {
		delegate.actualitzaServeis(entitat);
	}
	@Override
	public boolean isUpdatingServeis(EntitatDto entitatDto) {
		return delegate.isUpdatingServeis(entitatDto);
	}
	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		return delegate.getProgresActualitzacio(dir3Codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ServeiOrganDto> findServeisOrganWithPermis(
			Long entitatId,
			String usuariCodi,
			PermisEnum permis) {
		return delegate.findServeisOrganWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ServeiOrganDto> findServeisOrganWithPermisByOrgan(
			String organId,
			String entitatCodi,
			List<ServeiOrganDto> serveisOrgans) {
		return delegate.findServeisOrganWithPermisByOrgan(organId, entitatCodi, serveisOrgans);
	}

	@Override
	public List<String> findServeisOrganCodiWithPermisByServei(
			ServeiDto servei,
			String entitatCodi,
			List<ServeiOrganDto> serveisOrgans) {
		return delegate.findServeisOrganCodiWithPermisByServei(servei, entitatCodi, serveisOrgans);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public ServeiDto findByNom(
			Long entitatId,
			String nomServei) throws NotFoundException {
		return delegate.findByNom(entitatId, nomServei);
	}
}
