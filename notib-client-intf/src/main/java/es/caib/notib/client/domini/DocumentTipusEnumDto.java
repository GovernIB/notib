/**
 * 
 */
package es.caib.notib.client.domini;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de document per a un interessat sense nif.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentTipusEnumDto implements Serializable {
	PASSAPORT,
	ESTRANGER,
	ALTRE
}
