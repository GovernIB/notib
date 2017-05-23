/**
 * 
 */
package es.caib.notib.core.api.service.rest;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - Pendent.
 *  - Error durant l'enviament a Notific@.
 *  - Enviada a Notific@.
 *  - Error publicant a la seu.
 *  - Processada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstat implements Serializable {
	PENDENT,
	NOTIFICA_ERROR,
	NOTIFICA_ENVIADA,
	SEU_ERROR,
	PROCESSADA;
}
