package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.DocumentTipusEnumDto;
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
	@Size(max=255)
	private String nom;
	@Size(max=30)
	private String llinatge1;
	@Size(max=30)
	private String llinatge2;
	private DocumentTipusEnumDto documentTipus;
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

	public String getNif() {
		if (nif == null || nif.trim().length() == 0)
			return null;
		return Character.isDigit(nif.trim().charAt(0)) && nif.trim().length() < 9 ? afegirZerosNif() : nif.trim();
	}

	private String afegirZerosNif() {

		nif = nif.trim();
		int length = 9 - nif.length();
		for (int foo = 0; foo < length; foo++) {
			nif = 0 + nif;
		}
		return nif;
	}
}
