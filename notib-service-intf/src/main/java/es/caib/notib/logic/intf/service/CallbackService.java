/**
 * 
 */
package es.caib.notib.logic.intf.service;

/**
 * Mètodes de servei per a gestionar les cridades al servei callback dels clients
 * de Notib. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CallbackService {

	/**
	 * Processa els callbacks pendents d'enviar als clients. 
	 */
	public void processarPendents();

}
