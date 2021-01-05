package es.caib.notib.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorFiltreCommand {
	
	private String codi;
	private String nom;
	private String oficina;
	
	public static OrganGestorFiltreCommand asCommand(OrganGestorFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		OrganGestorFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				OrganGestorFiltreCommand.class );
		return command;
	}
	public static OrganGestorFiltreDto asDto(OrganGestorFiltreCommand command) {
		if (command == null) {
			return null;
		}
		OrganGestorFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				OrganGestorFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
