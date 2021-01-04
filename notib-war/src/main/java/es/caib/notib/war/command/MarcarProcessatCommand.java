/**
 * 
 */
package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
