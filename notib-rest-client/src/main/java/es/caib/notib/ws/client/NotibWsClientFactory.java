/**
 * 
 */
package es.caib.notib.ws.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import es.caib.notib.ws.notificacio.Notificacio;


/**
 * Utilitat per a instanciar clients per al servei d'enviament de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotibWsClientFactory {

	public static Notificacio getWsClient(
			URL wsdlResourceUrl,
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<Notificacio>().generarClientWs(
				wsdlResourceUrl,
				endpoint,
				new QName(
						"http://www.caib.es/notib/ws/notificacio",
						"NotificacioService"),
				userName,
				password,
				Notificacio.class);
	}

	public static Notificacio getWsClient(
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<Notificacio>().generarClientWs(
				endpoint,
				new QName(
						"http://www.caib.es/notib/ws/notificacio",
						"NotificacioService"),
				userName,
				password,
				Notificacio.class);
	}

}
