/**
 * 
 */
package es.caib.notib.back.command;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	private EnviamentTipus enviamentTipus;
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
		return dto != null ? ConversioTipusHelper.convertir(dto, NotificacioFiltreCommand.class ) : null;
	}

	public static NotificacioFiltreDto asDto(NotificacioFiltreCommand command) {
		return command != null ? ConversioTipusHelper.convertir(command, NotificacioFiltreDto.class) : null;
	}

	public NotificacioFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, NotificacioFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
