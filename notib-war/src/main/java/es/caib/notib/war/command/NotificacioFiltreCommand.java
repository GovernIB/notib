/**
 * 
 */
package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

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
	private String organGestor;
	private Long procedimentId;
	private Long serveiId;
	private TipusUsuariEnumDto tipusUsuari;
	private String numExpedient;
	private String creadaPer;
	private String identificador;
	private String referencia;
	private boolean nomesAmbErrors;
	
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

	public NotificacioFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				NotificacioFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
