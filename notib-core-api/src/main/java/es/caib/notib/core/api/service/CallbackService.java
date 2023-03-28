/**
 * 
 */
package es.caib.notib.core.api.service;

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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
    boolean reintentarCallback(Long notId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	boolean findByNotificacio(Long notId);
}
