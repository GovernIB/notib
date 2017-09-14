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
public class Notificacio2ServiceException extends RuntimeException {

	public Notificacio2ServiceException(
			String message) {
		super(message);
	}

	public Notificacio2ServiceException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
