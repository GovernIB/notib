/**
 * 
 */
package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

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
		return dto != null ? ConversioTipusHelper.convertir(dto, NotificacioErrorCallbackFiltreCommand.class ) : null;
	}

	public static NotificacioErrorCallbackFiltreDto asDto(NotificacioErrorCallbackFiltreCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, NotificacioErrorCallbackFiltreDto.class) : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
