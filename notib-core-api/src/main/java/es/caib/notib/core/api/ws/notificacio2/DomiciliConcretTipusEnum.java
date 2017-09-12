/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio2;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus concret de domicili per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DomiciliConcretTipusEnum implements Serializable {
	NACIONAL,
	ESTRANGER,
	APARTAT_CORREUS,
	SENSE_NORMALITZAR
}
