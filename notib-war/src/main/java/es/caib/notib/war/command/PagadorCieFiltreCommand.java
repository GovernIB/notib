package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorCieFiltreCommand {
	
	private String dir3codi;
	
	
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}
	
	public static PagadorCieFiltreCommand asCommand(PagadorCieFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		PagadorCieFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				PagadorCieFiltreCommand.class );
		return command;
	}
	public static PagadorCieFiltreDto asDto(PagadorCieFiltreCommand command) {
		if (command == null) {
			return null;
		}
		PagadorCieFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				PagadorCieFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
