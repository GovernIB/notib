package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica els possibles events per a una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEventTipusEnumDto implements Serializable {

	REGISTRE_ENVIAMENT,
	SIR_ENVIAMENT,
	SIR_CONSULTA,
	NOTIFICA_ENVIAMENT,
	NOTIFICA_CONSULTA,
	ADVISER_CERTIFICACIO,
	ADVISER_DATAT,
	EMAIL_ENVIAMENT,
	CALLBACK_ENVIAMENT,
	API_CARPETA,
	SIR_FI_POOLING,
	CIE_ENVIAMENT,
	CIE_CANCELAR,
	CIE_CONSULTA_ESTAT,
	NOTIFICA_ENVIO_OE;
//	NOTIFICA_ENVIAMENT,  // 0			// Enviar notificacions a Notifica
//	NOTIFICA_CALLBACK_DATAT,			// Rebre adviser amb datat
//	NOTIFICA_CALLBACK_CERTIFICACIO,		// Rebre adviser amb certificació
//	NOTIFICA_CONSULTA_INFO, // 3		// Consulta l'estat de la notificació a Notifica
//	NOTIFICA_CONSULTA_ESTAT,
//	NOTIFICA_CONSULTA_DATAT,
//	NOTIFICA_CONSULTA_CERT, // 6
//	NOTIFICA_REGISTRE,					//
//	CALLBACK_CLIENT,					//
//	CALLBACK_CLIENT_PENDENT, // 9		//
//	REGISTRE_CONSULTA_INFO, 			// Consulta l'estat del registre
//	REGISTRE_CALLBACK_ESTAT,			// Realitza el registre de la notificació
//	NOTIFICA_CONSULTA_ERROR, // 12		//
//	NOTIFICA_CONSULTA_SIR_ERROR,		//
//	CALLBACK_ACTIVAR,
//	EMAIL_ENVIAMENT						//
}
