/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de UsuariAplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UsuariAplicacioService extends AbstractService<es.caib.notib.logic.intf.service.UsuariAplicacioService> implements es.caib.notib.logic.intf.service.UsuariAplicacioService {

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public AplicacioDto create(AplicacioDto aplicacio) {
		return getDelegateService().create(aplicacio);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public AplicacioDto update(AplicacioDto aplicacio) throws NotFoundException {
		return getDelegateService().update(aplicacio);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_ADMIN"})
	public AplicacioDto delete(Long id, Long entitatId) throws NotFoundException {
		return getDelegateService().delete(id, entitatId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findById(Long aplicacioId) {
		return getDelegateService().findById(aplicacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findByEntitatAndId(Long entitatId, Long aplicacioId) {
		return getDelegateService().findByEntitatAndId(entitatId, aplicacioId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findByUsuariCodi(String usuariCodi) {
		return getDelegateService().findByUsuariCodi(usuariCodi);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public AplicacioDto findByEntitatAndUsuariCodi(Long entitatId, String usuariCodi) {
		return getDelegateService().findByEntitatAndUsuariCodi(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public PaginaDto<AplicacioDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public PaginaDto<AplicacioDto> findPaginatByEntitat(Long entitatId, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginatByEntitat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN"})
	public AplicacioDto findByEntitatAndText(Long entitatId, String text) {
		return getDelegateService().findByEntitatAndText(entitatId, text);
	}

	@Override
	@PermitAll
	public AplicacioDto updateActiva(Long id, boolean activa) {
		return getDelegateService().updateActiva(id, activa);
	}

}
