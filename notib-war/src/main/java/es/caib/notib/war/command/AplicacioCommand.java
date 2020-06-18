/**
 * 
 */
package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.CodiAplicacioNoRepetit;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'aplicacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@CodiAplicacioNoRepetit
public class AplicacioCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String usuariCodi;
	@NotEmpty @Size(max=256) 
	private String callbackUrl;
	private Long entitatId;

	
	public static AplicacioCommand asCommand(AplicacioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AplicacioCommand.class);
	}
	public static AplicacioDto asDto(AplicacioCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AplicacioDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
