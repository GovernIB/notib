/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fitxer implements Serializable {

	private String nom;
	private String contentType;
	private byte[] contingut;
	private long tamany;

}
