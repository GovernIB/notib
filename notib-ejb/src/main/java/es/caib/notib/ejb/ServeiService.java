/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.CodiValorComuDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerOrganDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ServeiService extends AbstractService<es.caib.notib.logic.intf.service.ServeiService> implements es.caib.notib.logic.intf.service.ServeiService {

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcSerDto create(
			Long entitatId, 
			ProcSerDataDto servei) {
		return getDelegateService().create(
				entitatId, 
				servei);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcSerDto update(
			Long entitatId,
			ProcSerDataDto servei,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException {
		return getDelegateService().update(
				entitatId, 
				servei,
				isAdmin,
				isAdminEntitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public ProcSerDto delete(
			Long entitatId, 
			Long id,
			boolean isAdminEntitat) throws NotFoundException {
		return getDelegateService().delete(
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
		return getDelegateService().findById(
				entitatId, 
				isAdministrador, 
				id);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public ProcSerDto findByCodi(Long entitatId, String codiServei) throws NotFoundException {
		return getDelegateService().findByCodi(entitatId, codiServei);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerSimpleDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcSerSimpleDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		return getDelegateService().findByOrganGestorIDescendents(entitatId, organGestor);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(
    		Long id,
			OrganGestorDto organGestor) {
        return getDelegateService().findByOrganGestorIDescendentsAndComu(id, organGestor);
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
		return getDelegateService().findAmbFiltrePaginat(
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
		return getDelegateService().findAll();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean serveiEnUs(Long serveiId) {
		return getDelegateService().serveiEnUs(serveiId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean serveiAmbGrups(Long serveiId) {
		return getDelegateService().serveiAmbGrups(serveiId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerGrupDto> findAllGrups() {
		return getDelegateService().findAllGrups();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId) {
		return getDelegateService().findGrupsByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerDto> findServeis(Long entitatId, List<String> grups) {
		return getDelegateService().findServeis(entitatId, grups);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerSimpleDto> findServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return getDelegateService().findServeisWithPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerDto> findServeisSenseGrups(Long entitatId) {
		return getDelegateService().findServeisSenseGrups(entitatId);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ProcSerDto> findServeisSenseGrupsWithPermis(Long entitatId, PermisEnum permis) {
//		return getDelegateService().findServeisSenseGrupsWithPermis(entitatId, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
	public List<ProcSerDto> findServeisAmbGrups(Long entitatId, List<String> grups) {
		return getDelegateService().findServeisAmbGrups(entitatId, grups);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<ProcSerDto> findServeisAmbGrupsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
//		return getDelegateService().findServeisAmbGrupsWithPermis(entitatId, grups, permis);
//	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		return getDelegateService().hasAnyServeisWithPermis(entitatId, grups, permis);
	}
	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public List<PermisDto> permisFind(
//			Long entitatId,
//			boolean isAdministrador,
//			Long serveiId,
//			String organ,
//			String organActual,
//			TipusPermis tipus) throws NotFoundException {
//		return getDelegateService().permisFind(
//				entitatId,
//				isAdministrador,
//				serveiId,
//				organ,
//				organActual,
//				tipus);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom"})
//	public void permisUpdate(
//			Long entitatId,
//			Long organGestorId,
//			Long id,
//			PermisDto permis) throws NotFoundException {
//		getDelegateService().permisUpdate(
//				entitatId,
//				organGestorId,
//				id,
//				permis);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom"})
//	public void permisDelete(
//			Long entitatId,
//			Long organGestorId,
//			Long serveiId,
//			String organCodi,
//			Long permisId,
//			TipusPermis tipus) throws NotFoundException {
//		getDelegateService().permisDelete(
//				entitatId,
//				organGestorId,
//				serveiId,
//				organCodi,
//				permisId,
//				tipus);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom"})
//	public ServeiGrupDto grupCreate(
//			Long entitatId,
//			Long id,
//			ServeiGrupDto serveiGrup) throws NotFoundException {
//		return getDelegateService().grupCreate(
//				entitatId,
//				id,
//				serveiGrup);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom"})
//	public ServeiGrupDto grupUpdate(
//			Long entitatId,
//			Long id,
//			ServeiGrupDto serveiGrup) throws NotFoundException {
//		return getDelegateService().grupUpdate(
//				entitatId,
//				id,
//				serveiGrup);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom"})
//	public ServeiGrupDto grupDelete(
//			Long entitatId,
//			Long GrupId) throws NotFoundException {
//		return getDelegateService().grupDelete(
//				entitatId,
//				GrupId);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom", "NOT_APL"})
//	public boolean hasPermisServei(
//			Long serveiId,
//			PermisEnum permis) {
//		return getDelegateService().hasPermisServei(
//				serveiId,
//				permis);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN"})
//	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
//		return getDelegateService().findTipusAssumpte(entitat);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN"})
//	public List<CodiAssumpteDto> findCodisAssumpte(
//			EntitatDto entitat,
//			String codiTipusAssumpte) {
//		return getDelegateService().findCodisAssumpte(
//				entitat,
//				codiTipusAssumpte);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN"})
//	public void refrescarCache(EntitatDto entitat) {
//		getDelegateService().refrescarCache(entitat);
//	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public List<ProcSerDto> findServeisByOrganGestor(String organGestorCodi) {
		return getDelegateService().findServeisByOrganGestor(organGestorCodi);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ProcSerDto> findServeisByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi, 
			List<String> grups,
			PermisEnum permis) {
		return getDelegateService().findServeisByOrganGestorWithPermis(
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
        return getDelegateService().getServeisOrgan(
        		entitatId,
				organCodi,
				organFiltre,
				rol,
				permis);
    }

	@Override
	@PermitAll
	public List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, TipusEnviamentEnumDto enviamentTipus) {
		return getDelegateService().getServeisOrganNotificables(
				entitatId,
				organCodi,
				rol, 
				enviamentTipus);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean hasServeisComunsAndNotificacioPermission(Long entitatId, TipusEnviamentEnumDto enviamentTipus) {
		return getDelegateService().hasServeisComunsAndNotificacioPermission(entitatId, enviamentTipus);
	}

	@Override
	@PermitAll
	public boolean actualitzarServei(String codiSia, EntitatDto entitat) {
		return getDelegateService().actualitzarServei(codiSia, entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void actualitzaServeis(EntitatDto entitat) {
		getDelegateService().actualitzaServeis(entitat);
	}
	@Override
	@PermitAll
	public boolean isUpdatingServeis(EntitatDto entitatDto) {
		return getDelegateService().isUpdatingServeis(entitatDto);
	}
	@Override
	@PermitAll
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		return getDelegateService().getProgresActualitzacio(dir3Codi);
	}

//	@Override
//	@RolesAllowed({"NOT_ADMIN", "tothom"})
//	public List<ServeiOrganDto> findServeisOrganWithPermis(
//			Long entitatId,
//			String usuariCodi,
//			PermisEnum permis) {
//		return getDelegateService().findServeisOrganWithPermis(entitatId, usuariCodi, permis);
//	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<ProcSerOrganDto> findServeisOrganWithPermisByOrgan(
			String organId,
			String entitatCodi,
			List<ProcSerOrganDto> serveisOrgans) {
		return getDelegateService().findServeisOrganWithPermisByOrgan(organId, entitatCodi, serveisOrgans);
	}

	@Override
	@PermitAll
	public List<String> findServeisOrganCodiWithPermisByServei(
			ProcSerDto servei,
			String entitatCodi,
			List<ProcSerOrganDto> serveisOrgans) {
		return getDelegateService().findServeisOrganCodiWithPermisByServei(servei, entitatCodi, serveisOrgans);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public ProcSerDto findByNom(
			Long entitatId,
			String nomServei) throws NotFoundException {
		return getDelegateService().findByNom(entitatId, nomServei);
	}

	@Override
	@PermitAll
	public Integer getServeisAmbOrganNoSincronitzat(Long entitatId) {
        return getDelegateService().getServeisAmbOrganNoSincronitzat(entitatId);
    }
}
