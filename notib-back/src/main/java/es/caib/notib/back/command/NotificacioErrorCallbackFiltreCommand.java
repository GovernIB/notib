/**
 * 
 */
package es.caib.notib.back.command;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @NoArgsConstructor
public class NotificacioErrorCallbackFiltreCommand {
	
	private Date dataInici;
	private Date dataFi;
	private Long procedimentId;
	private String concepte;
	private NotificacioEstatEnumDto estat;
	private String usuari;

	public static NotificacioErrorCallbackFiltreCommand asCommand(NotificacioErrorCallbackFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		NotificacioErrorCallbackFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				NotificacioErrorCallbackFiltreCommand.class );
		return command;
	}
	public static NotificacioErrorCallbackFiltreDto asDto(NotificacioErrorCallbackFiltreCommand command) {
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
