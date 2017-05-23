/**
 * 
 */
package es.caib.notib.core.api.service.rest;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de numeració de domicili per a un destinatari
 * de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliNumeracioTipus implements Serializable {

	NUMERO,
	PUNTO_KILOMETRICO,
	SIN_NUMERO,
	APARTADO_CORREOS
	
}
