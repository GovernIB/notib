/**
 * 
 */
package es.caib.notib.war.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlt;
	private String idioma;
	private String[] rols;
	private Boolean rebreEmailsNotificacio;
	private Boolean rebreEmailsNotificacioCreats;

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
