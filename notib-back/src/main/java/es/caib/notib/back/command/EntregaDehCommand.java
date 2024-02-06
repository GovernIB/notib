package es.caib.notib.back.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.EntregaDehDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de entregues direcció electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Getter @Setter
public class EntregaDehCommand {

	private String emisorNif;
	private boolean obligat;
	@Size(max=64)
	private String procedimentCodi;
	private boolean activa;
	
	public static EntregaDehCommand asCommand(DocumentDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, EntregaDehCommand.class ) : null;
	}

	public static EntregaDehDto asDto(DocumentCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, EntregaDehDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
