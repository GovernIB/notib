/**
 * 
 */
package es.caib.notib.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

/**
 * Implementació de UsuariAplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UsuariAplicacioService extends AbstractService<es.caib.notib.logic.intf.service.UsuariAplicacioService> implements es.caib.notib.logic.intf.service.UsuariAplicacioService {

	@Autowired
	UsuariAplicacioService delegate;

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public AplicacioDto create(
			AplicacioDto aplicacio) {
		return delegate.create(aplicacio);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public AplicacioDto update(
			AplicacioDto aplicacio) throws NotFoundException {
		return delegate.update(aplicacio);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public AplicacioDto delete(
			Long id, 
			Long entitatId) throws NotFoundException {
		return delegate.delete(id, entitatId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findById(
			Long aplicacioId) {
		return delegate.findById(aplicacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findByEntitatAndId(Long entitatId, Long aplicacioId) {
		return delegate.findByEntitatAndId(entitatId, aplicacioId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findByUsuariCodi(
			String usuariCodi) {
		return delegate.findByUsuariCodi(usuariCodi);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findByEntitatAndUsuariCodi(
			Long entitatId, 
			String usuariCodi) {
		return delegate.findByEntitatAndUsuariCodi(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public PaginaDto<AplicacioDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public PaginaDto<AplicacioDto> findPaginatByEntitat(Long entitatId, PaginacioParamsDto paginacioParams) {
		return delegate.findPaginatByEntitat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public AplicacioDto findByEntitatAndText(Long entitatId, String text) {
		return delegate.findByEntitatAndText(entitatId, text);
	}

	@Override
	public AplicacioDto updateActiva(Long id, boolean activa) {
		return delegate.updateActiva(id, activa);
	}

}
