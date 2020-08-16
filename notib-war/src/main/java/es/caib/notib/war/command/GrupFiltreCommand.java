package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.GrupFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class GrupFiltreCommand {
	
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=64)
	private String nom;
	private Long organGestorId;
	
	
	public static GrupFiltreCommand asCommand(GrupFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		GrupFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				GrupFiltreCommand.class );
		return command;
	}
	public static GrupFiltreDto asDto(GrupFiltreCommand command) {
		if (command == null) {
			return null;
		}
		GrupFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				GrupFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
