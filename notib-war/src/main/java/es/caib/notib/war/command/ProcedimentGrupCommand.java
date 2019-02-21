package es.caib.notib.war.command;


import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.TipusGrupEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment dels grups d'un procediments (VERIÓ ANTERIOR).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentGrupCommand {
	
	private Long id;
	private TipusGrupEnumDto tipus;
	private Long procedimentId;
	private Long grupId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TipusGrupEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TipusGrupEnumDto tipus) {
		this.tipus = tipus;
	}
	public Long getProcedimentId() {
		return procedimentId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public Long getGrupId() {
		return grupId;
	}
	public void setGrupId(Long grupId) {
		this.grupId = grupId;
	}
	
	public static ProcedimentGrupCommand asCommand(ProcedimentGrupDto dto) {
		if (dto == null) {
			return null;
		}
		ProcedimentGrupCommand command = ConversioTipusHelper.convertir(
				dto,
				ProcedimentGrupCommand.class );
		
		command.setGrupId(dto.getGrup().getId());
		command.setProcedimentId(dto.getProcediment().getId());
		
		return command;
	}
	public static ProcedimentGrupDto asDto(ProcedimentGrupCommand command) {
		if (command == null) {
			return null;
		}
		
		ProcedimentGrupDto dto = ConversioTipusHelper.convertir(
				command,
				ProcedimentGrupDto.class);
		
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
