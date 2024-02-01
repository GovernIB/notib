/**
 * 
 */
package es.caib.notib.logic.intf.service;

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
}
