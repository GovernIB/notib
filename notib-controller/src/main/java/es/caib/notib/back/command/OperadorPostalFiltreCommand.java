package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.cie.OperadorPostalFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
		return dto != null ? ConversioTipusHelper.convertir(dto, OperadorPostalFiltreCommand.class ) : null;
	}

	public OperadorPostalFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, OperadorPostalFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
