package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.ValidPersona;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.validation.constraints.Size;

/**
 * Command per al manteniment de persones (Titulars | Destinataris).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter @Setter
@ValidPersona
public class PersonaCommand {

	private Long id;
	private boolean incapacitat;
	private InteressatTipusEnumDto interessatTipus;
	private String nom;
	@Size(max=40)	
	private String llinatge1;
	@Size(max=40)
	private String llinatge2;
	@Size(max=9)
	private String nif;
	@Size(max=16)
	private String telefon;
	@Size(max=160)
	private String email;
	@Size(max=9)	
	private String dir3Codi;
	
	
	public static PersonaCommand asCommand(PersonaDto dto) {
		if (dto == null) {
			return null;
		}
		PersonaCommand command = ConversioTipusHelper.convertir(
				dto,
				PersonaCommand.class );
		return command;
	}
	public static PersonaDto asDto(PersonaCommand command) {
		if (command == null) {
			return null;
		}
		PersonaDto dto = ConversioTipusHelper.convertir(
				command,
				PersonaDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
