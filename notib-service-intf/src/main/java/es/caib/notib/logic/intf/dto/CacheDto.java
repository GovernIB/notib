/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class CacheDto implements Serializable {
	
	private String codi;
	private String descripcio;
	private long localHeapSize;
}
