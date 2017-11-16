/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica el servei que ha originat un error.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ErrorOrigenEnum implements Serializable {
	NOTIB,
	NOTIFICA,
	SEU_CAIB
}
