/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.EntitatDataDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntitatService extends AbstractService<es.caib.notib.logic.intf.service.EntitatService> implements es.caib.notib.logic.intf.service.EntitatService {

	@Override
	@RolesAllowed("NOT_SUPER")
	public EntitatDto create(EntitatDataDto entitat) {
		return getDelegateService().create(entitat);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public EntitatDto update(EntitatDataDto entitat) {
		return getDelegateService().update(entitat);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public EntitatDto updateActiva(Long id, boolean activa) {
		return getDelegateService().updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public EntitatDto delete(Long id) {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public EntitatDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public EntitatDto findByCodi(String codi) {
		return getDelegateService().findByCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public EntitatDto findByDir3codi(String dir3Codi) {
		return getDelegateService().findByDir3codi(dir3Codi);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public List<EntitatDto> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<EntitatDto> findAccessiblesUsuariActual(String rolActual) {
		return getDelegateService().findAccessiblesUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public List<PermisDto> permisFindByEntitatId(Long id, PaginacioParamsDto paginacioParams) throws NotFoundException {
		return getDelegateService().permisFindByEntitatId(id, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void permisUpdate(Long entitatId, PermisDto permis) throws NotFoundException {
		getDelegateService().permisUpdate(entitatId, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void permisDelete(Long entitatId, Long permisId) throws NotFoundException {
		getDelegateService().permisDelete(entitatId, permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean hasPermisUsuariEntitat() {
		return getDelegateService().hasPermisUsuariEntitat();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean hasPermisAdminEntitat() {
		return getDelegateService().hasPermisAdminEntitat();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean hasPermisAplicacioEntitat() {
		return getDelegateService().hasPermisAplicacioEntitat();
	}

	@Override
	@PermitAll
	public byte[] getCapLogo() throws NoSuchFileException, IOException {
		return getDelegateService().getCapLogo();
	}

	@Override
	@PermitAll
	public byte[] getPeuLogo() throws NoSuchFileException, IOException {
		return getDelegateService().getPeuLogo();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<TipusDocumentDto> findTipusDocumentByEntitat(Long entitatId) {
		return getDelegateService().findTipusDocumentByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public TipusDocumentEnumDto findTipusDocumentDefaultByEntitat(Long entitatId) {
		return getDelegateService().findTipusDocumentDefaultByEntitat(entitatId);
	}

	@Override
	@PermitAll
	public Map<RolEnumDto, Boolean> getPermisosEntitatsUsuariActual() {
		return getDelegateService().getPermisosEntitatsUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public List<OficinaDto> findOficinesEntitat(String dir3codi) {
		return getDelegateService().findOficinesEntitat(dir3codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public LlibreDto getLlibreEntitat(String dir3Codi) {
		return getDelegateService().getLlibreEntitat(dir3Codi);
	}

	@Override
	@RolesAllowed("tothom")
	public Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatCodi) {
		return getDelegateService().findOrganigramaByEntitat(entitatCodi);
	}

	@Override
	@PermitAll
	public boolean existeixPermis(Long entitatId, String principal) throws Exception {
		return getDelegateService().existeixPermis(entitatId, principal);
	}

	@Override
	@PermitAll
	public void setConfigEntitat(String entitatCodi) {
		getDelegateService().setConfigEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void resetActualitzacioOrgans(Long id) {
		getDelegateService().resetActualitzacioOrgans(id);
	}
}
