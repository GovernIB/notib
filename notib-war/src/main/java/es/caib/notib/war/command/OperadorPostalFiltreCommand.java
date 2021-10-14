package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.cie.OperadorPostalFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al manteniment del filtre de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OperadorPostalFiltreCommand {
	
	private String organismePagador;
	private String contracteNum;
	private Long organGestorId;

	public static OperadorPostalFiltreCommand asCommand(OperadorPostalFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		return ConversioTipusHelper.convertir(
				dto,
				OperadorPostalFiltreCommand.class );
	}
	public OperadorPostalFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				OperadorPostalFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
