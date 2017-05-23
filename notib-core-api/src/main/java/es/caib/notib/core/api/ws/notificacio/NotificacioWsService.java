/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;


import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Servei per a la gestió de notificacions des d'altres aplicacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "Notificacio",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio")
public interface NotificacioWsService {

	/**
	 * Dona d'alta una notificació.
	 * 
	 * @param notificacio
	 *            Dades per a donar d'alta la notificació.
	 * @return la llista de referències (una per destinatari).
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public List<String> alta(
			@WebParam(name="notificacio") @XmlElement(required = true) Notificacio notificacio) throws NotificacioWsServiceException;

	/**
	 * Consulta la informació d'una notificació per a un destinatari.
	 * 
	 * @param referencia
	 *            Referència del destinatari a consultar.
	 * @return la informació de la notificació amb el destinatari
	 *            especificat.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public Notificacio consulta(
			@WebParam(name="referencia") @XmlElement(required = true) String referencia) throws NotificacioWsServiceException;

	/**
	 * Consulta l'estat d'una notificació per a un destinatari.
	 * 
	 * @param referencia
	 *            Referència del destinatari a consultar.
	 * @return la informació de l'estat pel destinatari especificat.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioEstat consultaEstat(
			@WebParam(name="referencia") @XmlElement(required = true) String referencia) throws NotificacioWsServiceException;

	/**
	 * Consulta la certificació d'una notificació per a un destinatari.
	 * 
	 * @param referencia
	 *            Referència del destinatari a consultar.
	 * @return la informació de la certificació pel destinatari especificat.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioCertificacio consultaCertificacio(
			@WebParam(name="referencia") @XmlElement(required = true) String referencia) throws NotificacioWsServiceException;

}
