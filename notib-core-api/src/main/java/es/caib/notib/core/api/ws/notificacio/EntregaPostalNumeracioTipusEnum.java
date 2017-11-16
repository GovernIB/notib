/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de numeració de domicili per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum EntregaPostalNumeracioTipusEnum implements Serializable {
	NUMERO,
	PUNT_KILOMETRIC,
	SENSE_NUMERO,
	APARTAT_CORREUS
}
