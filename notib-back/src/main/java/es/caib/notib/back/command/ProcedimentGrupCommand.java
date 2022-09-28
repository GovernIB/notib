package es.caib.notib.back.command;


import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.TipusGrupEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment dels grups d'un procediments (VERIÓ ANTERIOR).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcedimentGrupCommand {
	
	private Long id;
	private TipusGrupEnumDto tipus;
	private Long procedimentId;
	private Long grupId;

	
	public static ProcedimentGrupCommand asCommand(ProcSerGrupDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentGrupCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentGrupCommand.class );
		
		command.setGrupId(dto.getGrup().getId());
		command.setProcedimentId(dto.getProcSer().getId());
		
		return command;
	}
	
	public static ProcSerGrupDto asDto(ProcedimentGrupCommand command) {
		if (command == null) {
			return null;
		}
		
		ProcSerGrupDto dto = ConversioTipusHelper.convertir(
				command,
				ProcSerGrupDto.class);
		
		GrupDto grupDto = new GrupDto();
		grupDto.setId(command.getGrupId());
		dto.setGrup(grupDto);
		
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
