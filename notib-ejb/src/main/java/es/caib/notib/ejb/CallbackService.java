package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.callback.CallbackDto;
import es.caib.notib.logic.intf.dto.callback.CallbackFiltre;
import es.caib.notib.logic.intf.dto.callback.CallbackResposta;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Set;

/**
 * Implementació de CallbackService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.

 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Primary
@Stateless
public class CallbackService extends AbstractService<es.caib.notib.logic.intf.service.CallbackService> implements es.caib.notib.logic.intf.service.CallbackService {
	
	@Override
	@PermitAll
	public void processarPendents() {
		getDelegateService().processarPendents();
	}

	@Override
	@PermitAll
	public boolean reintentarCallback(Long notId) {
		return false;
	}

	@Override
	@PermitAll
	public boolean findByNotificacio(Long notId) {
		return false;
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_ADMIN_LECTURA"})
	public PaginaDto<CallbackDto> findPendentsByEntitat(CallbackFiltre filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPendentsByEntitat(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public CallbackResposta enviarCallback(Long callbackId) {
		return getDelegateService().enviarCallback(callbackId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public CallbackResposta enviarCallback(Set<Long> callbacks) {
		return getDelegateService().enviarCallback(callbacks);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public boolean pausarCallback(Long callbackId, boolean pausat) {
		return getDelegateService().pausarCallback(callbackId, pausat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public CallbackResposta pausarCallback(Set<Long> callbacks, boolean pausat) {
		return getDelegateService().pausarCallback(callbacks, pausat);
	}

    @Override
    @RolesAllowed({"NOT_ADMIN"})
    public boolean esborrarCallback(Long callbackId) {
        return getDelegateService().esborrarCallback(callbackId);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN"})
    public CallbackResposta esborrarCallback(Set<Long> callbacks) {
        return getDelegateService().esborrarCallback(callbacks);
    }

	@Override
	@PermitAll
	public void processarPendentsJms() {
		getDelegateService().processarPendentsJms();
	}

}
