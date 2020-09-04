package es.caib.notib.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.OrganNoRepetit;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */

@Getter @Setter
@OrganNoRepetit
public class OrganGestorCommand {
	
	private Long id;
	@NotEmpty @Size(max=9)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	private Long entitatId;
	private String entitatNom;
	@NotEmpty
	private String llibre;
	private String llibreNom;
	
	public static OrganGestorCommand asCommand(OrganGestorDto dto) {
		if (dto == null) {
			return null;
		}
		return ConversioTipusHelper.convertir(
				dto,
				OrganGestorCommand.class );
	}
	public static OrganGestorDto asDto(OrganGestorCommand command) {
		if (command == null) {
			return null;
		}
		return ConversioTipusHelper.convertir(
				command,
				OrganGestorDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
