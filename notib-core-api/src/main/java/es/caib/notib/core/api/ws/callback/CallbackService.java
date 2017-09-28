/**
 * 
 */
package es.caib.notib.core.api.ws.callback;

/**
 * Mètodes de servei per a gestionar els callbacks a les aplicaions client del Notib.
 * Serveix per a notificar les actualitzacions en les notificacions a través del WS 
 * REST de callback de les aplicacions. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CallbackService {

	/**
	 * Consulta els events pendents de notificar a les aplicacions clients i les notifica.
	 */
	public void notificarEventsPendens();
	
	/** Mètode per tractar un event pendent de notificar i notificar a la seva aplicació client si és necessari.
	 * 
	 * @param eventId
	 * 			Identificador de l'event de notificació a notificar.
	 */
	public boolean notifica(Long eventId);	
}
