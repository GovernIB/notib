package es.caib.notib.core.api.ws.callback;

/** Servei per a notificar a les aplicacions clients un event de canvi d'estat o de certificat
 * provinent del WS Adviser de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public interface ClientService {
	
	/**
	 * Tractament del estat de la notificació per part del client
	 * 
	 * @param estatNotificacio
	 *            Objecte amb les dades propies d'el estat d'una notificació
	 */
	public void notificaEstat(
			NotificacioEstatClient estatNotificacio);
	
	
	/**
	 * Tractament de la certificació de la notificació per part del client
	 * 
	 * @param referencia
	 *            Referencia a al destinatari d'una notificació
	 */
	public void notificaCertificacio(
			NotificacioCertificacioClient certificacioNotificacio);
}