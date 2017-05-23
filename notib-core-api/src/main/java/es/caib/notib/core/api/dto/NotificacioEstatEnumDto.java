/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - Pendent. Pendent d'enviament a Notifica.
 *  - Enviada a Notifica.
 *  - Processada. Estat final de la notificació. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnumDto implements Serializable {
	PENDENT,
	ENVIADA_NOTIFICA,
	PROCESSADA;
}
