package es.caib.notib.back.command;

import com.google.common.base.Strings;
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
	private String dir3CodiInput;

	
	public static PersonaCommand asCommand(PersonaDto dto) {
		return dto != null ? ConversioTipusHelper.convertir(dto, PersonaCommand.class ) : null;
	}

	public static PersonaDto asDto(PersonaCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, PersonaDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getNif() {
		return nif != null && nif.trim().length() != 0 ? Character.isDigit(nif.trim().charAt(0)) && nif.trim().length() < 9 ? afegirZerosNif() : nif.trim() : null;
	}

	public void setNomInput(String nomInput) {

		this.nomInput = nomInput;
		nom = nomInput;
	}

	public void setRaoSocialInput(String raoSocialInput) {

		this.raoSocialInput = raoSocialInput;
		raoSocial = raoSocialInput;
	}

	private String afegirZerosNif() {

		nif = nif.trim();
		var length = 9 - nif.length();
		for (var foo = 0; foo < length; foo++) {
			nif = 0 + nif;
		}
		return nif;
	}
}
