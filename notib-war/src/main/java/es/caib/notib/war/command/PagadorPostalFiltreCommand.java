package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PagadorPostalFiltreCommand {
	
	private String dir3codi;
	private String contracteNum;
	private Long organGestorId;
	
	
	public static PagadorPostalFiltreCommand asCommand(PagadorPostalFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorPostalFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorPostalFiltreCommand.class );
		return command;
	}
	public static PagadorPostalFiltreDto asDto(PagadorPostalFiltreCommand command) {
		if (command == null) {
			return null;
		}
		PagadorPostalFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorPostalFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
