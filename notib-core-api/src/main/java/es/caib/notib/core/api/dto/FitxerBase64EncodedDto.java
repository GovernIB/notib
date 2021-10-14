/**
 * 
 */
package es.caib.notib.core.api.dto;

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
public class FitxerBase64EncodedDto implements Serializable {
	private String nom;
	private String contentType;
	private String contingut;
	private long tamany;
}
