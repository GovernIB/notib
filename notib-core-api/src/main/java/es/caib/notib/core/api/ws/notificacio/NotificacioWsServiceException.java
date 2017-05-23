/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

/**
 * Excepció que recull els errors produits al processar les peticions al
 * servei NotificacioWsService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotificacioWsServiceException extends RuntimeException {

	public NotificacioWsServiceException(
			String message) {
		super(message);
	}

	public NotificacioWsServiceException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
