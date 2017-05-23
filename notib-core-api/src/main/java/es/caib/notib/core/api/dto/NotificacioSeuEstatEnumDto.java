/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - Pendent.
 *  - Enviada.
 *  - Error d'enviament.
 *  - Entregada.
 *  - Rebutjada.
 *  - Error de processament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioSeuEstatEnumDto implements Serializable {
	PENDENT,
	ENVIADA,
	ERROR_ENVIAMENT,
	LLEGIDA,
	REBUTJADA,
	ERROR_PROCESSAMENT,
	LLEGIDA_NOTIFICA,
	REBUTJADA_NOTIFICA,
	ERROR_NOTIFICA;
}
