/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

/**
 * Servei per a l'enviament i consulta de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "NotificacioServiceV2",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio")
public interface NotificacioServiceWsV2 {

	/**
	 * Dona d'alta una notificació.
	 * 
	 * @param notificacio
	 *            Dades per a donar d'alta la notificació.
	 * @return la llista de referencies generades per NOTIB (una per enviament)).
	 */
	public RespostaAlta alta(
			@WebParam(name="notificacio") @XmlElement(required = true) NotificacioV2 notificacio);


	/**
	 * Dona permís de consulta sobre un procediment.
	 * 
	 * @param notificacio
	 *            Dades per a donar d'alta la notificació.
	 * @return la llista de referencies generades per NOTIB (una per enviament)).
	 */
	public boolean donarPermisConsulta(
			PermisConsulta permisConsulta);
	/**
	 * Consulta l'estat d'un enviament d'una notificació.
	 * 
	 * @param referencia
	 *            Referència de l'enviament a consultar.
	 * @return la informació de la notificació amb l'enviament
	 *            especificat.
	 */
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			@WebParam(name="identificador") @XmlElement(required = true) String identificador);

	/**
	 * Consulta l'estat d'un enviament d'una notificació.
	 * 
	 * @param referencia
	 *            Referència de l'enviament a consultar.
	 * @return la informació de la notificació amb l'enviament
	 *            especificat.
	 */
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			@WebParam(name="referencia") @XmlElement(required = true) String referencia);
	
	/**
	 * Consulta l'estat d'un enviament d'una notificació.
	 * 
	 * @param dadesConsulta
	 *            Dades necessàries per realitzar la consulta.
	 * @return la informació de la notificació amb l'enviament
	 *            especificat.
	 */
	public RespostaConsultaDadesRegistre consultaDadesRegistre(
			DadesConsulta dadesConsulta);

}
