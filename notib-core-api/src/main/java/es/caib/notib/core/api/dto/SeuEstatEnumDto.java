/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació a dins SEU.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent d'enviament a SEU.
 *  - ENVIADA: Enviada a SEU, i pendent.
 *  - LLEGIDA: Interessat ha llegit la notificació.
 *  - REBUTJADA: Interessat no ha llegit la notificació dins el termini establert
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum SeuEstatEnumDto implements Serializable {
	PENDENT,
	ENVIADA,
	LLEGIDA,
	REBUTJADA,
	INEXISTENT
}
