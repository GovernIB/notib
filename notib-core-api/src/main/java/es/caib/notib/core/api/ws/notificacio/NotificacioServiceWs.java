/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;


import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;


/**
 * Servei per a l'enviament i consulta de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "NotificacioService",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio")
public interface NotificacioServiceWs {

	/**
	 * Dona d'alta una notificació.
	 * 
	 * @param notificacio
	 *            Dades per a donar d'alta la notificació.
	 * @return la llista de referencies generades per NOTIB (una per enviament)).
	 */
	public List<String> alta(
			@WebParam(name="notificacio") @XmlElement(required = true) Notificacio notificacio) throws NotificacioServiceWsException;

	/**
	 * Consulta la informació d'una notificació donat un enviament.
	 * 
	 * @param referencia
	 *            Referència de l'enviament a consultar.
	 * @return la informació de la notificació amb l'enviament
	 *            especificat.
	 */
	public InformacioEnviament consulta(
			@WebParam(name="referencia") @XmlElement(required = true) String referencia) throws NotificacioServiceWsException;

}
