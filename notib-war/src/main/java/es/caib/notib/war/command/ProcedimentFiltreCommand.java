package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcedimentFiltreCommand {
	
	private String codi;
	private String nom;
	private String organGestor;
	private Long entitatId;
	private Boolean comu;
	
	
	public static ProcedimentFiltreCommand asCommand(ProcedimentFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentFiltreCommand.class );
		return command;
	}
	public static ProcedimentFiltreDto asDto(ProcedimentFiltreCommand command) {
		if (command == null) {
			return null;
		}
		ProcedimentFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ProcedimentFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
