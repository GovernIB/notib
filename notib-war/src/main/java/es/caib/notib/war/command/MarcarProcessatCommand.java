/**
 * 
 */
package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Command per indicar el motiu de processament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MarcarProcessatCommand {

	@NotBlank
	private String motiu;

	public String getMotiu() {
		return motiu;
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
