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
	CIE_ADVISER_DATAT, //18
	SIR_ADVISER,//19
    NOTIFICA_ANULAR;//20


}
