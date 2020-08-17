package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PagadorCieFiltreCommand {
	
	private String dir3codi;
	private Long organGestorId;
	
	
	public static PagadorCieFiltreCommand asCommand(PagadorCieFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorCieFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorCieFiltreCommand.class );
		return command;
	}
	public static PagadorCieFiltreDto asDto(PagadorCieFiltreCommand command) {
		if (command == null) {
			return null;
		}
		PagadorCieFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorCieFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
