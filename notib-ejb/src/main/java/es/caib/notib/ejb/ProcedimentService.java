/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.CodiAssumpteDto;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusAssumpteDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerOrganDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class ProcedimentService extends AbstractService<es.caib.notib.logic.intf.service.ProcedimentService> implements es.caib.notib.logic.intf.service.ProcedimentService {

	@Override
	@RolesAllowed("**")
	public ProcSerDto create(Long entitatId, ProcSerDataDto procediment) {
		return getDelegateService().create(entitatId, procediment);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto update(Long entitatId, ProcSerDataDto procediment, boolean isAdmin, boolean isAdminEntitat) throws NotFoundException {
		return getDelegateService().update(entitatId, procediment, isAdmin, isAdminEntitat);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto updateActiu(Long id, boolean actiu) throws NotFoundException {
		return getDelegateService().updateActiu(id, actiu);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto updateManual(Long id, boolean manual) throws NotFoundException {
		return getDelegateService().updateManual(id, manual);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto delete(Long entitatId, Long id, boolean isAdminEntitat) throws NotFoundException {
		return getDelegateService().delete(entitatId, id, isAdminEntitat);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto findById(Long entitatId, boolean isAdministrador, Long id) throws NotFoundException {
		return getDelegateService().findById(entitatId, isAdministrador, id);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto findByCodi(Long entitatId, String codiProcediment) throws NotFoundException {
		return getDelegateService().findByCodi(entitatId, codiProcediment);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerSimpleDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerSimpleDto> findByOrganGestorIDescendents(Long entitatId, OrganGestorDto organGestor) {
		return getDelegateService().findByOrganGestorIDescendents(entitatId, organGestor);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(Long id, OrganGestorDto organGestor) {
		return getDelegateService().findByOrganGestorIDescendentsAndComu(id, organGestor);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ProcSerFormDto> findAmbFiltrePaginat(Long entitatId, boolean isUsuari, boolean isUsuariEntitat, boolean isAdministrador, OrganGestorDto organGestorActual, ProcSerFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(entitatId, isUsuari, isUsuariEntitat, isAdministrador, organGestorActual, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findAll() {
		return getDelegateService().findAll();
	}

    @Override
	@RolesAllowed("**")
    public List<CodiValorDto> findAllIdDesc() {
        return getDelegateService().findAllIdDesc();
    }

    @Override
	@RolesAllowed("**")
	public boolean procedimentEnUs(Long procedimentId) {
		return getDelegateService().procedimentEnUs(procedimentId);
	}

	@Override
	@RolesAllowed("**")
	public boolean procedimentAmbGrups(Long procedimentId) {
		return getDelegateService().procedimentAmbGrups(procedimentId);
	}

	@Override
	@RolesAllowed("**")
	public boolean procedimentActiu(Long procedimentId) {
		return getDelegateService().procedimentActiu(procedimentId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerGrupDto> findAllGrups() {
		return getDelegateService().findAllGrups();
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId) {
		return getDelegateService().findGrupsByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findProcediments(Long entitatId, List<String> grups) {
		return getDelegateService().findProcediments(entitatId, grups);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findProcedimentsSenseGrups(Long entitatId) {
		return getDelegateService().findProcedimentsSenseGrups(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {
		return getDelegateService().findProcedimentsAmbGrups(entitatId, grups);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		return getDelegateService().hasAnyProcedimentsWithPermis(entitatId, grups, permis);
	}

	@Override
	@RolesAllowed("**")
	public List<PermisDto> permisFind(Long entitatId, boolean isAdministrador, Long procedimentId, String organ, String organActual, TipusPermis tipus, PaginacioParamsDto paginacioParams) throws NotFoundException {
		return getDelegateService().permisFind(entitatId, isAdministrador, procedimentId, organ, organActual, tipus, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public void permisUpdate(Long entitatId, Long organGestorId, Long id, PermisDto permis) throws NotFoundException {
		getDelegateService().permisUpdate(entitatId, organGestorId, id, permis);
	}

	@Override
	@RolesAllowed("**")
	public void permisDelete(Long entitatId, Long organGestorId, Long procedimentId, String organCodi, Long permisId, TipusPermis tipus) throws NotFoundException {
		getDelegateService().permisDelete(entitatId, organGestorId, procedimentId, organCodi, permisId, tipus);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerGrupDto grupCreate(Long entitatId, Long id, ProcSerGrupDto procedimentGrup) throws NotFoundException {
		return getDelegateService().grupCreate(entitatId, id, procedimentGrup);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerGrupDto grupUpdate(Long entitatId, Long id, ProcSerGrupDto procedimentGrup) throws NotFoundException {
		return getDelegateService().grupUpdate(entitatId, id, procedimentGrup);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerGrupDto grupDelete(Long entitatId, Long GrupId) throws NotFoundException {
		return getDelegateService().grupDelete(entitatId, GrupId);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasPermisProcediment(Long procedimentId, PermisEnum permis) {
		return getDelegateService().hasPermisProcediment(procedimentId, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
		return getDelegateService().findTipusAssumpte(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<CodiAssumpteDto> findCodisAssumpte(EntitatDto entitat, String codiTipusAssumpte) {
		return getDelegateService().findCodisAssumpte(entitat, codiTipusAssumpte);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void refrescarCache(EntitatDto entitat) {
		getDelegateService().refrescarCache(entitat);
	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
//	public List<ProcSerDto> findProcedimentsByOrganGestor(String organGestorCodi) {
//		return getDelegateService().findProcedimentsByOrganGestor(organGestorCodi);
//	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findProcedimentsByOrganGestorWithPermis(Long entitatId, String organGestorCodi, List<String> grups, PermisEnum permis) {
		return getDelegateService().findProcedimentsByOrganGestorWithPermis(entitatId, organGestorCodi, grups, permis);
	}

	@Override
	@RolesAllowed("**")
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrgan(Long entitatId, String organCodi, Long organFiltre, RolEnumDto rol, PermisEnum permis) {
		return getDelegateService().getProcedimentsOrgan(entitatId, organCodi, organFiltre, rol, permis);
	}

	@Override
	@PermitAll
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, EnviamentTipus enviamentTipus) {
		return getDelegateService().getProcedimentsOrganNotificables(entitatId, organCodi, rol, enviamentTipus);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasProcedimentsComunsAndNotificacioPermission(Long entitatId, EnviamentTipus enviamentTipus) {
		return getDelegateService().hasProcedimentsComunsAndNotificacioPermission(entitatId, enviamentTipus);
	}

	@Override
	@PermitAll
	public boolean actualitzarProcediment(String codiSia, EntitatDto entitat) {
		return getDelegateService().actualitzarProcediment(codiSia, entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void actualitzaProcediments(EntitatDto entitat) {
		getDelegateService().actualitzaProcediments(entitat);
	}

	@Override
	@PermitAll
	public boolean isUpdatingProcediments(EntitatDto entitatDto) {
		return getDelegateService().isUpdatingProcediments(entitatDto);
	}

	@Override
	@PermitAll
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		return getDelegateService().getProgresActualitzacio(dir3Codi);
	}

	@Override
	@PermitAll
	public boolean procedimentAmbCieExtern(Long procedimentId, String organCodi) {
		return getDelegateService().procedimentAmbCieExtern(procedimentId, organCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerOrganDto> findProcedimentsOrganWithPermisByOrgan(String organId, String entitatCodi, List<ProcSerOrganDto> procedimentsOrgans) {
		return getDelegateService().findProcedimentsOrganWithPermisByOrgan(organId, entitatCodi, procedimentsOrgans);
	}

	@Override
	@PermitAll
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(ProcSerDto procediment, String entitatCodi, List<ProcSerOrganDto> procedimentsOrgans) {
		return getDelegateService().findProcedimentsOrganCodiWithPermisByProcediment(procediment, entitatCodi, procedimentsOrgans);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto findByNom(Long entitatId, String nomProcediment) throws NotFoundException {
		return getDelegateService().findByNom(entitatId, nomProcediment);
	}

	@Override
	@PermitAll
	public Integer getProcedimentsAmbOrganNoSincronitzat(Long entitatId) {
		return getDelegateService().getProcedimentsAmbOrganNoSincronitzat(entitatId);
	}
}
