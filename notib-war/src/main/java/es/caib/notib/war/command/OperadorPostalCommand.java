package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.cie.OperadorPostalDataDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per al manteniment de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class OperadorPostalCommand {
	
	private Long id;

	@NotEmpty @Size(max=255)
	private String nom;
	@NotEmpty @Size(max=64)
	private String organismePagadorCodi;
	@NotEmpty @Size(max=8)
	private String contracteNum;
	private Date contracteDataVig;
	@NotEmpty @Size(max=64)
	private String facturacioClientCodi;

	public static OperadorPostalCommand asCommand(OperadorPostalDto dto) {
		if (dto == null) {
			return null;
		}

		return ConversioTipusHelper.convertir(
				dto,
				OperadorPostalCommand.class );
	}
	public OperadorPostalDataDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				OperadorPostalDataDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
