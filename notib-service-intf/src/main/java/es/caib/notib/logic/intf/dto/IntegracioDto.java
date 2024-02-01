/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * Dades d'una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class IntegracioDto implements Serializable {

	private IntegracioCodiEnum codi;
	private String nom;
	private int numErrors;

	private static final long serialVersionUID = -139254994389509932L;

}
