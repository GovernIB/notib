/**
 * 
 */
package es.caib.notib.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment del filtre de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioFiltreCommand {
	
	private Long entitatId;
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private NotificacioEstatEnumDto estat;
	private String concepte;
	private Date dataInici;
	private Date dataFi;
	private String titular;
	private Long procedimentId;
	private TipusUsuariEnumDto tipusUsuari;
	private String numExpedient;
	private String creadaPer;
	private String identificador;
	
	public static NotificacioFiltreCommand asCommand(NotificacioFiltreDto dto) {
		if (dto == null) {
			return null;
		}
		NotificacioFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				NotificacioFiltreCommand.class );
		return command;
	}
	public static NotificacioFiltreDto asDto(NotificacioFiltreCommand command) {
		if (command == null) {
			return null;
		}
		NotificacioFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				NotificacioFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
