/**
 * 
 */
package es.caib.notib.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AplicacioDto extends AuditoriaDto {

	private Long id;
	private String usuariCodi;
	private String callbackUrl;
	private Long entitatId;


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
