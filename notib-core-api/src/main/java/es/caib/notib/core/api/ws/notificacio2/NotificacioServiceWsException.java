/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio2;

/**
 * Excepció que recull els errors produits al processar les peticions al
 * servei NotificacioWsService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotificacioServiceWsException extends RuntimeException {

	public NotificacioServiceWsException(
			String message) {
		super(message);
	}

	public NotificacioServiceWsException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
