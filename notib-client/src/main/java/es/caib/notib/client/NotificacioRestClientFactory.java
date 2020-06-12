/**
 * 
 */
package es.caib.notib.client;

/**
 * Utilitat per a instanciar clients REST per al servei d'enviament
 * de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioRestClientFactory {

	public static NotificacioRestClient getRestClient(
			String baseUrl,
			String username,
			String password) {
		return new NotificacioRestClient(
				baseUrl,
				username,
				password);
	}
	
	public static NotificacioRestClient getRestClient(
			String baseUrl,
			String username,
			String password,
			boolean autenticacioBasic) {
		return new NotificacioRestClient(
				baseUrl,
				username,
				password,
				autenticacioBasic);
	}
	
	public static NotificacioRestClient getRestClientV2(
			String baseUrl,
			String username,
			String password) {
		return new NotificacioRestClient(
				baseUrl,
				username,
				password);
	}

}
