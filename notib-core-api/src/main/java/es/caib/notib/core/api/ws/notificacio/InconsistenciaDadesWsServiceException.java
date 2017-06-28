/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

/**
 * Excepció que recull els errors d'inconsisstencia de dades produits
 * al processar les peticions al servei NotificacioWsService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class InconsistenciaDadesWsServiceException extends NotificacioWsServiceException {

	public InconsistenciaDadesWsServiceException(
			String message) {
		super(message);
	}

	public InconsistenciaDadesWsServiceException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
