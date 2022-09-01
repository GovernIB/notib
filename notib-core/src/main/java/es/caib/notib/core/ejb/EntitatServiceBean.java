/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntitatServiceBean implements EntitatService {

	@Autowired
	EntitatService delegate;

	@Override
	@RolesAllowed("NOT_SUPER")
	public EntitatDto create(EntitatDataDto entitat) {
		return delegate.create(entitat);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public EntitatDto update(
			EntitatDataDto entitat) {
		return delegate.update(entitat);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public EntitatDto delete(
			Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public EntitatDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public EntitatDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public EntitatDto findByDir3codi(String dir3Codi) {
		return delegate.findByDir3codi(dir3Codi);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public List<EntitatDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<EntitatDto> findAccessiblesUsuariActual(String rolActual) {
		return delegate.findAccessiblesUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public List<PermisDto> permisFindByEntitatId(Long id, PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.permisFindByEntitatId(id, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void permisUpdate(Long entitatId, PermisDto permis) throws NotFoundException {
		delegate.permisUpdate(entitatId, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void permisDelete(Long entitatId, Long permisId) throws NotFoundException {
		delegate.permisDelete(entitatId, permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean hasPermisUsuariEntitat() {
		return delegate.hasPermisUsuariEntitat();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean hasPermisAdminEntitat() {
		return delegate.hasPermisAdminEntitat();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean hasPermisAplicacioEntitat() {
		return delegate.hasPermisAplicacioEntitat();
	}

	@Override
	public byte[] getCapLogo() throws NoSuchFileException, IOException {
		return delegate.getCapLogo();
	}

	@Override
	public byte[] getPeuLogo() throws NoSuchFileException, IOException {
		return delegate.getPeuLogo();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<TipusDocumentDto> findTipusDocumentByEntitat(Long entitatId) {
		return delegate.findTipusDocumentByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public TipusDocumentEnumDto findTipusDocumentDefaultByEntitat(Long entitatId) {
		return delegate.findTipusDocumentDefaultByEntitat(entitatId);
	}

	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public Map<RolEnumDto, Boolean> getPermisosEntitatsUsuariActual() {
		return delegate.getPermisosEntitatsUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public List<OficinaDto> findOficinesEntitat(String dir3codi) {
		return delegate.findOficinesEntitat(dir3codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public LlibreDto getLlibreEntitat(String dir3Codi) {
		return delegate.getLlibreEntitat(dir3Codi);
	}

	@Override
	@RolesAllowed("tothom")
	public Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatCodi) {
		return delegate.findOrganigramaByEntitat(entitatCodi);
	}

	@Override
	public boolean existeixPermis(Long entitatId, String principal) throws Exception {
		return delegate.existeixPermis(entitatId, principal);
	}

	@Override
	public void setConfigEntitat(EntitatDto entitatDto) {
		delegate.setConfigEntitat(entitatDto);
	}
}
