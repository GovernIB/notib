/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio2;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent d'enviament a Notifica.
 *  - ENVIADA_NOTIFICA: Enviada a Notifica.
 *  - FINALITZADA: Estat final de la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnum implements Serializable {
	PENDENT,
	ENVIADA,
	FINALITZADA;
}
