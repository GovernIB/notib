package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica els possibles events per a una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEventTipusEnumDto implements Serializable {

	REGISTRE_ENVIAMENT, //0
	SIR_ENVIAMENT, //1
	SIR_CONSULTA, //2
	NOTIFICA_ENVIAMENT, //3
	NOTIFICA_CONSULTA, //4
	ADVISER_CERTIFICACIO, //5
	ADVISER_DATAT, //6
	EMAIL_ENVIAMENT, //7
	CALLBACK_ENVIAMENT, //8
	API_CARPETA, //9
	SIR_FI_POOLING, //10
	CIE_ENVIAMENT, //11
	CIE_CANCELAR, //12
	CIE_CONSULTA_ESTAT, //13
	NOTIFICA_ENVIO_OE, //14
	AMPLIAR_PLAZO_OE, //15
	CIE_ADVISER, //16
	CIE_ADVISER_CERTIFICACIO, //17
	CIE_ADVISER_DATAT; //18
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
