package es.caib.notib.back.command;

import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.validation.ValidPersona;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	private InteressatTipus interessatTipus;
//	@Size(max=255)
	private String nom;
	private String nomInput;
	private String raoSocialInput;
	private String raoSocial;
	@Size(max=30)
	private String llinatge1;
	@Size(max=30)
	private String llinatge2;
	private DocumentTipus documentTipus;
	@Size(max=9)
	private String nif;
	@Size(max=16)
	private String telefon;
	@Size(max=160)
	private String email;
	@Size(max=9)	
	private String dir3Codi;

//	private void setNom(String nom) {
//		this.nom = InteressatTipusEnumDto.JURIDICA.equals(interessatTipus) ? raoSocial : nomInput;
//	}
	
	public static PersonaCommand asCommand(PersonaDto dto) {
		if (dto == null) {
			return null;
		}
		PersonaCommand command = ConversioTipusHelper.convertir(dto, PersonaCommand.class );
//		if (InteressatTipusEnumDto.JURIDICA.equals(command.getInteressatTipus())) {
//			command.setRaoSocial(dto.getNom());
//		} else {
//			command.setNomInput(dto.getNom());
//		}
		return command;
	}
	public static PersonaDto asDto(PersonaCommand command) {
		if (command == null) {
			return null;
		}
		PersonaDto dto = ConversioTipusHelper.convertir(command, PersonaDto.class);
//		dto.setNom(InteressatTipusEnumDto.JURIDICA.equals(command.getInteressatTipus()) ? command.getNomInput() : command.getRaoSocial());
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getNif() {
		if (nif == null || nif.trim().length() == 0) {
			return null;
		}
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
