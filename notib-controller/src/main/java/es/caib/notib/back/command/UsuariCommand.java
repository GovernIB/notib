/**
 * 
 */
package es.caib.notib.back.command;

import java.io.Serializable;

import es.caib.notib.back.validation.ValidUsuari;
import es.caib.notib.client.domini.NumElementsPaginaDefecte;
import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ValidUsuari
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlt;
	private Long entitatDefecte;
	private Long procedimentDefecte;
	private Long organDefecte;
	private String idioma;
	private String[] rols;
	private Boolean rebreEmailsNotificacio;
	private Boolean rebreEmailsNotificacioCreats;
	private NumElementsPaginaDefecte numElementsPaginaDefecte;

	public static UsuariCommand asCommand(UsuariDto dto) {
		return ConversioTipusHelper.convertir(dto, UsuariCommand.class);
	}
	public static UsuariDto asDto(UsuariCommand command) {
		return ConversioTipusHelper.convertir(command, UsuariDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	private static final long serialVersionUID = -139254994389509932L;

}
