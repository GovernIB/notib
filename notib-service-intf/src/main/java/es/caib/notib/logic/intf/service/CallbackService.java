/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.callback.CallbackDto;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Mètodes de servei per a gestionar les cridades al servei callback dels clients
 * de Notib. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CallbackService {

	/**
	 * Processa els callbacks pendents d'enviar als clients. 
	 */
	void processarPendents();

	/**
	 * Reintenta un callback fallat
	 *
	 * @param notId Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("isAuthenticated()")
    boolean reintentarCallback(Long notId);

	@PreAuthorize("isAuthenticated()")
	boolean findByNotificacio(Long notId);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	PaginaDto<CallbackDto> findPendentsByeEntitat(EntitatDto entitat, PaginacioParamsDto paginacioParams);
}
