/**
 * 
 */
package es.caib.notib.core.api.service.rest;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de domicili concret per a un destinatari
 * de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliConcretTipus implements Serializable {

	NACIONAL,
	EXTRANJERO,
	SIN_NORMALIZAR,
	APARTADO_CORREOS

}
