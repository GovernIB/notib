package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PagadorCieFormatSobreCommand {
	
	private Long id;
	@NotEmpty
	@Size(max=64)
	private String codi;
	private Long pagadorCieId;

	public static PagadorCieFormatSobreCommand asCommand(CieFormatSobreDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, PagadorCieFormatSobreCommand.class ) : null;
	}

	public static CieFormatSobreDto asDto(PagadorCieFormatSobreCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, CieFormatSobreDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
