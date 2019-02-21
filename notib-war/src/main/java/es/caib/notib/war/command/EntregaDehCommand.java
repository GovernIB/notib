package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.EntregaDehDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de entregues direcció electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class EntregaDehCommand {

	@NotEmpty @Size(max=50)
	private boolean obligat;
	private String procedimentCodi;

	public boolean isObligat() {
		return obligat;
	}
	public void setObligat(boolean obligat) {
		this.obligat = obligat;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	
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
