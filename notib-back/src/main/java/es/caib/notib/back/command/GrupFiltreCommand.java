package es.caib.notib.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.GrupFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class GrupFiltreCommand {
	
	@NotEmpty
	@Size(max=64)
	private String codi;
	@NotEmpty @Size(max=64)
	private String nom;
	private Long organGestorId;
	
	
	public static GrupFiltreCommand asCommand(GrupFiltreDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, GrupFiltreCommand.class ): null;
	}
	public static GrupFiltreDto asDto(GrupFiltreCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, GrupFiltreDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
