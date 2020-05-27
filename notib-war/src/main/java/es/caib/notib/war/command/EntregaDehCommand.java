package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.EntregaDehDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
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
	@NotEmpty @Size(max=64)
	private String procedimentCodi;
	private boolean activa;
	
	public static EntregaDehCommand asCommand(DocumentDto dto) {
		if (dto == null) {
			return null;
		}
		EntregaDehCommand command = ConversioTipusHelper.convertir(
				dto,
				EntregaDehCommand.class );
		return command;
	}
	public static EntregaDehDto asDto(DocumentCommand command) {
		if (command == null) {
			return null;
		}
		EntregaDehDto dto = ConversioTipusHelper.convertir(
				command,
				EntregaDehDto.class);
		return dto;
	}

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
