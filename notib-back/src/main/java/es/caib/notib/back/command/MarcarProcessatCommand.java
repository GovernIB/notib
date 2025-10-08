/**
 * 
 */
package es.caib.notib.back.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;

/**
 * Command per indicar el motiu de processament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
public class MarcarProcessatCommand {

	@NotBlank
	private String motiu;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
