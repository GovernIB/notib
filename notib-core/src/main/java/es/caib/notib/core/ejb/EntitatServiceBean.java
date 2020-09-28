/**
 * 
 */
package es.caib.notib.core.ejb;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;

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
	public EntitatDto create(EntitatDto entitat) {
		return delegate.create(entitat);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public EntitatDto update(
			EntitatDto entitat) {
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
	public List<PermisDto> permisFindByEntitatId(Long id) throws NotFoundException {
		return delegate.permisFindByEntitatId(id);
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
}
