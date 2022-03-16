package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica els possibles events per a una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEventTipusEnumDto implements Serializable {
	NOTIFICA_ENVIAMENT,  // 0
	NOTIFICA_CALLBACK_DATAT,
	NOTIFICA_CALLBACK_CERTIFICACIO,
	NOTIFICA_CONSULTA_INFO, // 3
	NOTIFICA_CONSULTA_ESTAT,
	NOTIFICA_CONSULTA_DATAT,
	NOTIFICA_CONSULTA_CERT, // 6
	NOTIFICA_REGISTRE,
	CALLBACK_CLIENT,
	CALLBACK_CLIENT_PENDENT, // 9
	REGISTRE_CONSULTA_INFO,
	REGISTRE_CALLBACK_ESTAT,
	NOTIFICA_CONSULTA_ERROR, // 12
	NOTIFICA_CONSULTA_SIR_ERROR,
	CALLBACK_ACTIVAR,
	EMAIL_ENVIAMENT
}
