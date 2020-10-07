/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @NoArgsConstructor
public class NotificacioRegistreErrorFiltreCommand {
	
	private Date dataInici;
	private Date dataFi;
	private Long procedimentId;
	private String concepte;
	private String usuari;

	public static NotificacioRegistreErrorFiltreCommand asCommand(NotificacioErrorCallbackFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		NotificacioRegistreErrorFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				NotificacioRegistreErrorFiltreCommand.class );
		return command;
	}
	public static NotificacioErrorCallbackFiltreDto asDto(NotificacioRegistreErrorFiltreCommand command) {
		if (command == null) {
			return null;
		}
		NotificacioErrorCallbackFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				NotificacioErrorCallbackFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
