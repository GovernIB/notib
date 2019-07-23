/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica els possibles events per a una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEventTipusEnumDto implements Serializable {
	NOTIFICA_ENVIAMENT,
	NOTIFICA_CALLBACK_DATAT,
	NOTIFICA_CALLBACK_CERTIFICACIO,
	NOTIFICA_CONSULTA_INFO,
	NOTIFICA_CONSULTA_ESTAT,
	NOTIFICA_CONSULTA_DATAT,
	NOTIFICA_CONSULTA_CERT,
	NOTIFICA_REGISTRE,
	CALLBACK_CLIENT;
}
