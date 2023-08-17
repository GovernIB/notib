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

//	private static NotificacioRestClient clientV1;
//	private static NotificacioRestClientV2 clientV2;

	/**
	 * Crea un client per a connectar-se amb la API REST v1 de NOTIB.
	 *
	 * @param baseUrl Url de Notib
	 * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Notib
	 * @param password Contrasenya de l'usuari
	 * @return Client per a interactuar amb Notib. El client per defecte està configurat amb autenticació tipus form,
	 *  	(per defecte en entorn CAIB), i amb timeouts de 20s de connexió i 2 min de lectura
	 * @deprecated
	 */
	@Deprecated
	public static NotificacioRestClient getRestClient(String baseUrl, String username, String password) {

//		if (clientV1 != null) {
//			return clientV1;
//		}
		return new NotificacioRestClient(baseUrl, username, password);
	}

	/**
	 * Crea un client per a connectar-se amb la API REST v1 de NOTIB.
	 *
	 * @param baseUrl Url de Notib
	 * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Notib
	 * @param password Contrasenya de l'usuari
	 * @param autenticacioBasic Indica si utilitzar autenticació basic. En cas negatiu s'utilitzarà autenticació form (per defecte en entorn CAIB).
	 * @return Client per a interactuar amb Notib. El client per defecte està configurat timeouts de 20s de connexió i 2 min de lectura
	 * @deprecated
	 */
	@Deprecated
	public static NotificacioRestClient getRestClient(String baseUrl, String username, String password, boolean autenticacioBasic) {

//		if (clientV1 != null) {
//			return clientV1;
//		}
		return new NotificacioRestClient(baseUrl, username, password, autenticacioBasic);
	}

	/**
	 * Crea un client per a connectar-se amb la API REST v1 de NOTIB.
	 *
	 * @param baseUrl Url de Notib
	 * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Notib
	 * @param password Contrasenya de l'usuari
	 * @param autenticacioBasic Indica si utilitzar autenticació basic. En cas negatiu s'utilitzarà autenticació form (per defecte en entorn CAIB).
	 * @param connecTimeout Timeout de connexió en milisegons
	 * @param readTimeout Timeout de lectura en milisegons
	 * @return Client per a interactuar amb Notib.
	 * @deprecated
	 */
	@Deprecated
	public static NotificacioRestClient getRestClient(String baseUrl, String username, String password, boolean autenticacioBasic, int connecTimeout, int readTimeout) {

//		if (clientV1 != null) {
//			return clientV1;
//		}
		return new NotificacioRestClient(baseUrl, username, password, autenticacioBasic, connecTimeout, readTimeout);
	}


	// API v2
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Crea un client per a connectar-se amb la API REST v2 de NOTIB.
	 *
	 * @param baseUrl Url de Notib
	 * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Notib
	 * @param password Contrasenya de l'usuari
	 * @return Client per a interactura amb Notib. El client per defecte està configurat amb autenticació tipus form,
	 *  	(per defecte en entorn CAIB), i amb timeouts de 20s de connexió i 2 min de lectura.
	 */
	public static NotificacioRestClientV2 getRestClientV2(String baseUrl, String username, String password) {

//		if (clientV2 != null) {
//			return clientV2;
//		}
		return new NotificacioRestClientV2(baseUrl, username, password);
	}

	/**
	 * Crea un client per a connectar-se amb la API REST v2 de NOTIB.
	 *
	 * @param baseUrl Url de Notib
	 * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Notib
	 * @param password Contrasenya de l'usuari
	 * @param autenticacioBasic Indica si utilitzar autenticació basic. En cas negatiu s'utilitzarà autenticació form (per defecte en entorn CAIB).
	 * @return Client per a interactuar amb Notib. El client per defecte està configurat timeouts de 20s de connexió i 2 min de lectura
	 */
	public static NotificacioRestClientV2 getRestClientV2(String baseUrl, String username, String password, boolean autenticacioBasic) {

//		if (clientV2 != null) {
//			return clientV2;
//		}
		return new NotificacioRestClientV2(baseUrl, username, password, autenticacioBasic);
	}

	/**
	 * Crea un client per a connectar-se amb la API REST v2 de NOTIB.
	 *
	 * @param baseUrl Url de Notib
	 * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Notib
	 * @param password Contrasenya de l'usuari
	 * @param autenticacioBasic Indica si utilitzar autenticació basic. En cas negatiu s'utilitzarà autenticació form (per defecte en entorn CAIB).
	 * @param connecTimeout Timeout de connexió en milisegons
	 * @param readTimeout Timeout de lectura en milisegons
	 * @return Client per a interactuar amb Notib.
	 */
	public static NotificacioRestClientV2 getRestClientV2(String baseUrl, String username, String password, boolean autenticacioBasic, int connecTimeout, int readTimeout) {

//		if (clientV2 != null) {
//			return clientV2;
//		}
		return new NotificacioRestClientV2(baseUrl, username, password, autenticacioBasic, connecTimeout, readTimeout);
	}
}
