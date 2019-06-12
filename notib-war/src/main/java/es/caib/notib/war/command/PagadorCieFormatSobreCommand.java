package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorCieFormatSobreCommand {
	
	private Long id;
	@NotEmpty @Size(max=64)
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
	public static PagadorCieFormatSobreCommand asCommand(PagadorCieFormatSobreDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorCieFormatSobreCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorCieFormatSobreCommand.class );
		return command;
	}
	public static PagadorCieFormatSobreDto asDto(PagadorCieFormatSobreCommand command) {
		if (command == null) {
			return null;
		}
		PagadorCieFormatSobreDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorCieFormatSobreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
