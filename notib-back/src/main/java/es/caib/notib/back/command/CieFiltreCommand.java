package es.caib.notib.back.command;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.cie.CieFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class CieFiltreCommand {
	
	private String organismePagadorCodi;
	private Long organGestorId;
	
	
	public static CieFiltreCommand asCommand(CieFiltreDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, CieFiltreCommand.class ) : null;
	}

	public CieFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, CieFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
