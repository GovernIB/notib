/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Informació sobre la certificació d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
public class Certificacio {

	private Date data;
	private String origen;
	private String contingutBase64;
	private int tamany;
	private String hash;
	private String metadades;
	private String csv;
	private String tipusMime;

}
