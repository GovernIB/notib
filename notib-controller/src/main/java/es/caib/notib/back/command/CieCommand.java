package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
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

	@NotEmpty
	@Size(max=64)
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
