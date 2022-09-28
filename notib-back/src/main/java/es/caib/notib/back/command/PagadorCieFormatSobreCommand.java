package es.caib.notib.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorCieFormatSobreCommand {
	
	private Long id;
	@NotEmpty
	@Size(max=64)
	private String codi;
	private Long pagadorCieId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public Long getPagadorCieId() {
		return pagadorCieId;
	}
	public void setPagadorCieId(Long pagadorCieId) {
		this.pagadorCieId = pagadorCieId;
	}
	public static PagadorCieFormatSobreCommand asCommand(CieFormatSobreDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorCieFormatSobreCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorCieFormatSobreCommand.class );
		return command;
	}
	public static CieFormatSobreDto asDto(PagadorCieFormatSobreCommand command) {
		if (command == null) {
			return null;
		}
		CieFormatSobreDto dto = ConversioTipusHelper.convertir(
				command,
				CieFormatSobreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
