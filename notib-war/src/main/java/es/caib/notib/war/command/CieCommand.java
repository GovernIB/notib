package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.cie.CieDataDto;
import es.caib.notib.core.api.dto.cie.CieDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class CieCommand {
	
	private Long id;

	@NotEmpty @Size(max=64)
	private String organismePagadorCodi;
	private String nom;
	private Date contracteDataVig;

	public static CieCommand asCommand(CieDto dto) {
		if (dto == null) {
			return null;
		}
		return ConversioTipusHelper.convertir(
				dto,
				CieCommand.class );
	}
	public CieDataDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				CieDataDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
