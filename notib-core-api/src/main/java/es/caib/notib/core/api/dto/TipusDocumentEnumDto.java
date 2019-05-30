/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus d'enviament de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum TipusDocumentEnumDto implements Serializable {
	CSV,
	UUID,
	ARXIU,
	URL
}
