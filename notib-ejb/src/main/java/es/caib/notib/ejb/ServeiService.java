/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.context.annotation.Primary;

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
@Primary
@Stateless
public class ServeiService extends AbstractService<es.caib.notib.logic.intf.service.ServeiService> implements es.caib.notib.logic.intf.service.ServeiService {

	@Override
	@RolesAllowed("**")
	public ProcSerDto create(Long entitatId, ProcSerDataDto servei) {
		return getDelegateService().create(entitatId, servei);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto update(Long entitatId, ProcSerDataDto servei, boolean isAdmin, boolean isAdminEntitat) throws NotFoundException {
		return getDelegateService().update(entitatId, servei, isAdmin, isAdminEntitat);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerDto updateActiu(Long id, boolean actiu) throws NotFoundException {
		return getDelegateService().updateActiu(id, actiu);
	}

	@Override
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
	public ProcSerDto findByCodi(Long entitatId, String codiServei) throws NotFoundException {
		return getDelegateService().findByCodi(entitatId, codiServei);
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
	public boolean serveiEnUs(Long serveiId) {
		return getDelegateService().serveiEnUs(serveiId);
	}

	@Override
	@RolesAllowed("**")
	public boolean serveiAmbGrups(Long serveiId) {
		return getDelegateService().serveiAmbGrups(serveiId);
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
	public List<ProcSerDto> findServeis(Long entitatId, List<String> grups) {
		return getDelegateService().findServeis(entitatId, grups);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findServeisSenseGrups(Long entitatId) {
		return getDelegateService().findServeisSenseGrups(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcSerDto> findServeisAmbGrups(Long entitatId, List<String> grups) {
		return getDelegateService().findServeisAmbGrups(entitatId, grups);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		return getDelegateService().hasAnyServeisWithPermis(entitatId, grups, permis);
	}

//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
//	public List<ProcSerDto> findServeisByOrganGestor(String organGestorCodi) {
//		return getDelegateService().findServeisByOrganGestor(organGestorCodi);
//	}

	@Override
	@RolesAllowed("**")
	public List<CodiValorOrganGestorComuDto> getServeisOrgan(Long entitatId, String organCodi, Long organFiltre, RolEnumDto rol, PermisEnum permis) {
		return getDelegateService().getServeisOrgan(entitatId, organCodi, organFiltre, rol, permis);
	}

	@Override
	@PermitAll
	public List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, EnviamentTipus enviamentTipus) {
		return getDelegateService().getServeisOrganNotificables(entitatId, organCodi, rol, enviamentTipus);
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

	@Override
	@RolesAllowed("**")
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
