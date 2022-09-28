/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat de notificació d'un event callback cap a les aplicacions clients. Quan arriba
 * una actualització de l'estat o de la certificació via el WS Adviser de Notific@ l'event queda pendent de
 * notificar a l'aplicació client. Un procediment periòdic s'encarregarà de revisar els events pendents de
 * notificar.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent de notificar a l'aplicació client.
 *  - ERROR: Error enviant l'estat a l'aplicació client.
 *  - NOTIFICAT: Enviat a l'aplicació client.
 *  - PROCESSAT: S'ha tractat l'event pendent i no hi ha cap aplicació client que hagi de rebre la crida callback.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum CallbackEstatEnumDto implements Serializable {
	PENDENT,
	ERROR,
	NOTIFICAT,
	PROCESSAT;
}
