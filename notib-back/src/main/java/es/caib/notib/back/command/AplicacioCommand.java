/**
 * 
 */
package es.caib.notib.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.validation.CodiAplicacioNoRepetit;
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
	@NotEmpty
	@Size(max=64)
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
