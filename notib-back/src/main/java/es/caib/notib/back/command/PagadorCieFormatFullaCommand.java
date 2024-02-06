package es.caib.notib.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.back.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PagadorCieFormatFullaCommand {
	
	private Long id;
	@NotEmpty
	@Size(max=64)
	private String codi;
	private Long pagadorCieId;

	public static PagadorCieFormatFullaCommand asCommand(CieFormatFullaDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, PagadorCieFormatFullaCommand.class ) : null;
	}

	public static CieFormatFullaDto asDto(PagadorCieFormatFullaCommand command) {
		return command !=null ? ConversioTipusHelper.convertir(command, CieFormatFullaDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
