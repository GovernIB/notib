package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.cie.CieFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
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
		if (dto == null) {
			return null;
		}
		return ConversioTipusHelper.convertir(
				dto,
				CieFiltreCommand.class );
	}
	public CieFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				CieFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
