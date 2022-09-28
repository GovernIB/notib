/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de numeració de domicili per a un destinatari
 * de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliNumeracioTipusEnumDto implements Serializable {
	NUMERO,
	PUNT_KILOMETRIC,
	SENSE_NUMERO,
	APARTAT_CORREUS
}
